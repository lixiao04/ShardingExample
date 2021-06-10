package com.mysqldepart.sharding.shardingdemo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserOrderVO {
    private String username;
    private String gender;
    private Integer age;
    private Long orderId;
    private BigDecimal price;
    private Long orderTime;
}
