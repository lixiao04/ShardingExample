---id分隔的建表sql
create table ‘t_oder’
    ‘order_id’ BIGINT(20) not null COMMENT ‘订单id’,
    ‘price’ DECIMAL(10,2) not null COMMENT ‘价格’,
    ‘USER_ID’ BIGINT(20) NOT NULL COMMENT ‘用户id’,
    ‘status’ VARCHAR(50) character set utf8_general_ci not null comment ‘状态’,
    PRIMARY key (‘order_id’) using BTREE
    )ENGINE=INNODB character set utf8 COLLATE = utf8_general_ci ROw FORMAT = DYNAMIC;


----日期分隔的建表sql
CREATE TABLE `t_order_1` (
                            `order_id`  bigint(20) NOT NULL ,
                            `price`  decimal(10,2) NULL DEFAULT NULL ,
                            `user_id`  bigint(20) NULL DEFAULT NULL ,
                            `status`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
                            `create_time`  bigint(20) NULL DEFAULT NULL ,
                            PRIMARY KEY (`order_id`)
)
    ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
ROW_FORMAT=DYNAMIC
;

