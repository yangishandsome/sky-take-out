package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO getOrderDetail(Long id);

    void cancelOrder(OrdersCancelDTO ordersCancelDTO);

    void repetition(Long id);

    OrderStatisticsVO statistics();

    OrderVO details(Long id);

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void deliver(Long id);

    void complete(Long id);

    void reminder(Long id);
}
