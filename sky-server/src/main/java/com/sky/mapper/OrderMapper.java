package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    @Select("select * from orders where id = #{id}")
    OrderVO getById(Long id);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    List<OrderVO> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(id) from orders where status = #{status}")
    Integer getByStatus(Integer status);

    @Select("select * from orders where status = #{status} and order_time < #{dateTime}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime dateTime);

    Integer countByMap(Map map);

    Double sumByMap(Map map);
}
