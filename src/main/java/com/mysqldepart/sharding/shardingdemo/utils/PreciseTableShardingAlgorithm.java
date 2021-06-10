package com.mysqldepart.sharding.shardingdemo.utils;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

public class PreciseTableShardingAlgorithm implements PreciseShardingAlgorithm<Long> {

    /**
     * 精确分片算法
     *
     * @param availableTargetNames 所有配置的表列表，这里代表所匹配到库的所有表
     * @param shardingValue        分片值，也就是create_time的值
     * @return                     所匹配表的结果
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        System.out.println("精准查询返回表名");
        long value = shardingValue.getValue();

        if (value <= 0) throw new UnsupportedOperationException("preciseShardingValue is null");

        final String yearJoinMonthStr = DateUtil.getYearJoinMonthByMillisecond(value);
//        for (String availableTargetName : availableTargetNames) {
//            if (availableTargetName.endsWith(yearJoinMonthStr)) {
//                return availableTargetName;
//            }
//        }
//
//        throw new UnsupportedOperationException();

       //动态策略
        StringBuffer tableName = new StringBuffer();


        tableName.append(shardingValue.getLogicTableName()).append("_"+yearJoinMonthStr);
        return tableName.toString();
    }
}
