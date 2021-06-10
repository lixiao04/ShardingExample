package com.mysqldepart.sharding.shardingdemo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysqldepart.sharding.shardingdemo.entity.Order;
import com.mysqldepart.sharding.shardingdemo.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ShardingdemoApplicationTests {
    @Autowired
    OrderMapper orderMapper;
    @Test
    void contextLoads() {
    }
    @Test
    void addOrder(){
        Order o=new Order();
        o.setUserId(1l);
        o.setPrice(new BigDecimal("3"));
        o.setStatus("1");
        orderMapper.insert(o);

    }
    @Test
    void getOrders(){
        List<Long> list=new ArrayList<Long>();
        list.add(608684015985098753l);
        list.add(608684015985098755l);
        QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("order_id",list);
        queryWrapper.eq("status","2");
        List<Order> orders = orderMapper.selectList(queryWrapper);
        System.out.println(orders);
    }

}
