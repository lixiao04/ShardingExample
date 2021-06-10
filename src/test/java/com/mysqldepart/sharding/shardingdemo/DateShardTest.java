package com.mysqldepart.sharding.shardingdemo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysqldepart.sharding.shardingdemo.entity.Order;
import com.mysqldepart.sharding.shardingdemo.mapper.OrderMapper;
import com.mysqldepart.sharding.shardingdemo.vo.UserOrderVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class DateShardTest {
    @Autowired
    private OrderMapper orderMapper;
    @Test
     void insertOrder() throws ParseException {
         Order o=new Order();

         o.setCreateTime(new Date().getTime());
         o.setPrice(new BigDecimal("7"));
        o.setUserId(7l);
        o.setStatus("7");
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        Date parse = sdf.parse("2020-07-02");
        o.setCreateTime(parse.getTime());
        orderMapper.insert(o);
    }
    @Test
    void selectTime() throws ParseException {
        Order o=new Order();
        List<Long> orderIdList=new ArrayList<>();
        orderIdList.add(608713824605831169l);
        orderIdList.add(609013842889932801l);
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        Date parse = sdf.parse("2021-06-01");
        Date parse1 = sdf.parse("2021-05-02");
        o.setCreateTime(parse.getTime());
        List<Order> order = orderMapper.getOrder(o);
        System.out.println(order);
        QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("order_id",orderIdList);
        queryWrapper.eq("create_time",parse.getTime());
       // queryWrapper.between("create_time",parse1.getTime(),parse.getTime());
        List<Order> orders = orderMapper.selectList(queryWrapper);

        System.out.println(orders);
    }

    @Test
    void selectByUserId() throws ParseException {
        List<UserOrderVO> userOrder =
                orderMapper.getUserOrder(7l);
        System.out.println("获取到的user:==="+userOrder);
    }



}
