package com.mysqldepart.sharding.shardingdemo;

import com.mysqldepart.sharding.shardingdemo.entity.Order;
import com.mysqldepart.sharding.shardingdemo.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class DoubleDatabaseTest {
    @Autowired
    OrderMapper orderMapper;
    @Test
    void addOrder(){
        Order o=new Order();
        o.setUserId(3l);
        o.setStatus("数据库2");
        o.setPrice(new BigDecimal("3"));
        orderMapper.insert(o);
    }
}
