package com.mysqldepart.sharding.shardingdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dynamic.table")
@Data
public class DynamicTablesProperties {
    String[] names;
}
