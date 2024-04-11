package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult result = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(result);
    }

    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics(){
        OrderStatisticsVO statistics = orderService.statistics();
        return Result.success(statistics);
    }

    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable Long id){
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.cancelOrder(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    public Result deliver(@PathVariable Long id){
        orderService.deliver(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }
}
