package com.mysqldepart.sharding.shardingdemo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_order")
public class Order {
    private Long orderId;
    private BigDecimal price;
    private Long userId;
    private String status;
    private Long createTime;
}
