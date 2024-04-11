package com.sky.service;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {
    TurnoverReportVO turnoverCount(LocalDate begin, LocalDate end);

    UserReportVO userCount(LocalDate begin, LocalDate end);

    OrderReportVO ordersCount(LocalDate begin, LocalDate end);

    SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end);

    void export(HttpServletResponse response);
}
