package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper {

    void insert(OrderDetail orderDetail);
}
