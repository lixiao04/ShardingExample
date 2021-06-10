package com.mysqldepart.sharding.shardingdemo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysqldepart.sharding.shardingdemo.entity.Order;
import com.mysqldepart.sharding.shardingdemo.vo.UserOrderVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper extends BaseMapper<Order> {
   public  List<Order> getOrder(@Param("order") Order order);

   public  List<UserOrderVO> getUserOrder(@Param("userId") Long userId);
}
