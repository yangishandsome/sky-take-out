package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCarMapper shoppingCarMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);

        AddressBook addressBook = addressBookMapper.getById(orders.getAddressBookId());
        if(addressBook == null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        orders.setAddress(addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setUserId(BaseContext.getCurrentId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));

        orderMapper.insert(orders);

        List<ShoppingCart> list = shoppingCarMapper.list(orders.getUserId());
        if(list == null || list.isEmpty()){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        for(ShoppingCart shoppingCart : list){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailMapper.insert(orderDetail);
        }

        shoppingCarMapper.clean(BaseContext.getCurrentId());

        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime()).build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }

        String orderNumber = ordersPaymentDTO.getOrderNumber();
        Orders ordersDB = orderMapper.getByNumber(orderNumber);
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        JSONObject json = new JSONObject();
        json.put("type", 1);
        json.put("orderId", ordersDB.getId());
        json.put("content", "订单号：" + orderNumber);
        String string = json.toString();
        webSocketServer.sendToAllClient(string);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        List<OrderVO> orderVOS = orderMapper.historyOrders(ordersPageQueryDTO);
        for(OrderVO orderVO : orderVOS){
            Long id = orderVO.getId();
            orderVO.setOrderDetailList(orderDetailMapper.getByOrderId(id));
        }
        Page<OrderVO> page = (Page<OrderVO>) orderVOS;
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        List<OrderVO> orderVOS = orderMapper.historyOrders(ordersPageQueryDTO);
        for(OrderVO orderVO : orderVOS){
            Long id = orderVO.getId();
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
            String orderDishes = "";
            for(OrderDetail detail : orderDetails){
                orderDishes += detail.getName() + "\n";
            }
            orderVO.setOrderDishes(orderDishes);
        }
        Page<OrderVO> page = (Page<OrderVO>) orderVOS;
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }

    @Override
    public OrderVO getOrderDetail(Long id) {
        OrderVO orderVO = orderMapper.getById(id);
        orderVO.setOrderDetailList(orderDetailMapper.getByOrderId(id));
        return orderVO;
    }

    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {
        Long id = ordersCancelDTO.getId();
        String cancelReason = ordersCancelDTO.getCancelReason();
        Orders orders = orderMapper.getById(id);
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason(cancelReason);
        if(Objects.equals(orders.getPayStatus(), Orders.PAID)){
            orders.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(orders);
    }

    @Override
    public void repetition(Long id) {
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        for(OrderDetail orderDetail : orderDetails){
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCarMapper.add(shoppingCart);
        }
    }

    @Override
    @Transactional
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(orderMapper.getByStatus(Orders.CONFIRMED));
        orderStatisticsVO.setDeliveryInProgress(orderMapper.getByStatus(Orders.DELIVERY_IN_PROGRESS));
        orderStatisticsVO.setToBeConfirmed(orderMapper.getByStatus(Orders.TO_BE_CONFIRMED));
        return orderStatisticsVO;
    }

    @Override
    public OrderVO details(Long id) {
        OrderVO orderVO = orderMapper.getById(id);
        orderVO.setOrderDetailList(orderDetailMapper.getByOrderId(id));
        return orderVO;
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.getById(ordersRejectionDTO.getId());
        if(orders == null || !orders.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        if(orders.getPayStatus().equals(Orders.PAID)){
            orders.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(orders);
    }

    @Override
    public void deliver(Long id) {
        Orders orders = orderMapper.getById(id);
        if(orders == null || !orders.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    @Override
    public void complete(Long id) {
        Orders orders = orderMapper.getById(id);
        if(orders == null || !orders.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.getById(id);
        if(orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("type", 2);
        map.put("orderId", id);
        map.put("content", "订单号" + orders.getNumber());
        String jsonString = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }


}
