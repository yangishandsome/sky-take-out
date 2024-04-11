package com.sky.controller.admin;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.entity.Orders;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverCount(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        TurnoverReportVO turnoverReportVO = reportService.turnoverCount(begin, end);
        return Result.success(turnoverReportVO);
    }

    @GetMapping("/userStatistics")
    public Result<UserReportVO> userCount(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        UserReportVO userReportVO = reportService.userCount(begin, end);
        return Result.success(userReportVO);
    }

    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersCount
            (@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        OrderReportVO orderReportVO = reportService.ordersCount(begin, end);
        return Result.success(orderReportVO);
    }

    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> salesTop10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        SalesTop10ReportVO salesTop10ReportVO = reportService.salesTop10(begin, end);
        return Result.success(salesTop10ReportVO);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response){
        reportService.export(response);
    }
}
