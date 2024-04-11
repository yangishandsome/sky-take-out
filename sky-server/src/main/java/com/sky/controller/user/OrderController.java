package com.sky.controller.user;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "订单相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(int page, int pageSize, Integer status){
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setPage(page);
        ordersPageQueryDTO.setPageSize(pageSize);
        ordersPageQueryDTO.setStatus(status);
        PageResult result = orderService.historyOrders(ordersPageQueryDTO);
        return Result.success(result);
    }

    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id){
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Long id){
        OrdersCancelDTO ordersCancelDTO = new OrdersCancelDTO();
        ordersCancelDTO.setId(id);
        ordersCancelDTO.setCancelReason(null);
        orderService.cancelOrder(ordersCancelDTO);
        return  Result.success();
    }

    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable Long id){
        orderService.repetition(id);
        return  Result.success();
    }

    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Long id){
        orderService.reminder(id);
        return Result.success();
    }

}
