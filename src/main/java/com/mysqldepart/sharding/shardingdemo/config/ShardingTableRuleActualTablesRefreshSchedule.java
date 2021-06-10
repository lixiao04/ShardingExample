package com.mysqldepart.sharding.shardingdemo.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.core.exception.ShardingConfigurationException;
import org.apache.shardingsphere.core.rule.DataNode;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@EnableScheduling
@EnableConfigurationProperties(DynamicTablesProperties.class)
@Slf4j
public class ShardingTableRuleActualTablesRefreshSchedule implements InitializingBean {

    @Autowired
    private DynamicTablesProperties dynamicTablesProperties;

    @Autowired
    private DataSource dataSource;

    private RedisTemplate<Object,Object> redisTemplate;

    private List<String> tnames = new ArrayList<>();
//    //构造代码块，在构造函数调用前调用
    {
        tnames.add("t_order_202106");
        tnames.add("t_order_202107");
    }


    public ShardingTableRuleActualTablesRefreshSchedule() {
    }

    @Scheduled(cron = "0 28 16 9 6 *")
    public void actualTablesRefresh() throws NoSuchFieldException, IllegalAccessException {
//        Object o = redisTemplate.opsForValue().get("dataSource");
//        if(null==o){
//           tnames=new ArrayList<>();
//        }else{
//            tnames= (List<String>)redisTemplate.opsForValue().get("dataSource");
//        }
        System.out.println("开始动态创建表---------------------------------");
        ShardingDataSource dataSource = (ShardingDataSource) this.dataSource;
        if (dynamicTablesProperties.getNames() == null || dynamicTablesProperties.getNames().length == 0) {
//            log.warn("dynamic.table.names为空");
            return;
        }
        for (int i = 0; i < dynamicTablesProperties.getNames().length; i++) {
            TableRule tableRule = null;
            try {
                tableRule = dataSource.getShardingContext().getShardingRule().getTableRule(dynamicTablesProperties.getNames()[i]);
                System.out.println(tableRule);
            } catch (ShardingConfigurationException e) {
//                log.error("逻辑表：{},不存在配置！", dynamicTablesProperties.getNames()[i]);
            }


            List<DataNode> dataNodes = tableRule.getActualDataNodes();

            Field actualDataNodesField = TableRule.class.getDeclaredField("actualDataNodes");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(actualDataNodesField, actualDataNodesField.getModifiers() & ~Modifier.FINAL);
            actualDataNodesField.setAccessible(true);

            // ！！！！！！!！默认水平分表开始时间是2019-12月，每个月新建一张新表！！！！！
            LocalDateTime localDateTime = LocalDateTime.of(2019, 1, 1, 0, 0, new Random().nextInt(59));
            LocalDateTime now = LocalDateTime.now();
            int nowYear = now.getYear();
             now =LocalDateTime.of(nowYear, 12, 31, 0, 0, new Random().nextInt(59));
            String dataSourceName = dataNodes.get(0).getDataSourceName();
            String logicTableName = tableRule.getLogicTable();
            System.out.println("数据库名：="+dataSourceName+"表名前缀："+logicTableName);

            StringBuilder stringBuilder = new StringBuilder(10).append(dataSourceName).append(".").append(logicTableName);
            final int length = stringBuilder.length();
            List<DataNode> newDataNodes = new ArrayList<>();
            while (true) {
                stringBuilder.setLength(length);
                stringBuilder.append("_"+localDateTime.format(DateTimeFormatter.ofPattern("yyyyMM")));
                DataNode dataNode = new DataNode(stringBuilder.toString());
                String table = createTable(dataNode.getTableName());
                System.out.println("新加的表："+table);
                newDataNodes.add(dataNode);
                localDateTime = localDateTime.plusMonths(1L);
                if (localDateTime.isAfter(now)) {
                    break;
                }
            }
           // redisTemplate.opsForValue().set("dataSource",tnames);
            actualDataNodesField.set(tableRule, newDataNodes);
            System.out.println("新的分表节点"+newDataNodes);


            Set<String> actualTables = Sets.newHashSet();
            Map<DataNode, Integer> dataNodeIntegerMap = Maps.newHashMap();

            AtomicInteger a = new AtomicInteger(0);
            newDataNodes.forEach((dataNode -> {
                actualTables.add(dataNode.getTableName());
                if (a.intValue() == 0){
                    a.incrementAndGet();
                    dataNodeIntegerMap.put(dataNode, 0);
                }else {
                    dataNodeIntegerMap.put(dataNode, a.intValue());
                    a.incrementAndGet();
                }
            }));

            //动态刷新：actualTables
            Field actualTablesField = TableRule.class.getDeclaredField("actualTables");
            actualTablesField.setAccessible(true);
            actualTablesField.set(tableRule, actualTables);
            //动态刷新：dataNodeIndexMap
            Field dataNodeIndexMapField = TableRule.class.getDeclaredField("dataNodeIndexMap");
            dataNodeIndexMapField.setAccessible(true);
            dataNodeIndexMapField.set(tableRule, dataNodeIntegerMap);
            //动态刷新：datasourceToTablesMap
//            Map<String, Collection<String>> datasourceToTablesMap = Maps.newHashMap();
//            datasourceToTablesMap.put(dataSourceName, actualTables);
//            Field datasourceToTablesMapField = TableRule.class.getDeclaredField("datasourceToTablesMap");
//            datasourceToTablesMapField.setAccessible(true);
//            datasourceToTablesMapField.set(tableRule, datasourceToTablesMap);
            log.info("-----------------end----------------");


        }

    }
    public String createTable(String tb){

        if(tnames.contains(tb)){
            System.out.println(tb);
        }else{
            tnames.add(tb);
            String creatsql = String.format("CREATE TABLE if not exists `%s` (" +
                    "`order_id`  bigint(20) NOT NULL ,\n" +
                    "`price`  decimal(10,2) NULL DEFAULT NULL ,\n" +
                    "`user_id`  bigint(20) NULL DEFAULT NULL ,\n" +
                    "`status`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,\n" +
                    "`create_time`  bigint(20) NULL DEFAULT NULL ,\n" +
                    "PRIMARY KEY (`order_id`)\n" +
                    ")\n" +
                    "ENGINE=InnoDB\n" +
                    "DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci\n" +
                    "ROW_FORMAT=DYNAMIC",tb);

            Connection conn = null;
            try {
                conn = dataSource.getConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            //执行创建表
            System.out.println("//创建表");
            try{
                Statement stmt = conn.createStatement();
                if(0 == stmt.executeUpdate(creatsql)) {
                    System.out.println("成功创建表！");
                } else {
                    System.out.println("创建表失败！");
                }
                //
                stmt.close();
                conn.close();
                System.out.println("//关闭资源");


            }catch (Exception e){
                e.printStackTrace();
            }


        }
        return tb;
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        actualTablesRefresh();
    }


}
