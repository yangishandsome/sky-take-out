package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public TurnoverReportVO turnoverCount(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> sumList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map<String, Object> map = new HashMap<>();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);

            Double sum = reportMapper.sumByMap(map);
            sum = sum == null ? 0 : sum;
            sumList.add(sum);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(sumList, ","))
                .build();
    }

    @Override
    public UserReportVO userCount(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map<String, Object> map = new HashMap<>();

            map.put("endTime", endTime);
            Integer totalUser = reportMapper.getNewUserCount(map);
            totalUser = totalUser == null ? 0 : totalUser;
            totalUserList.add(totalUser);

            map.put("beginTime", beginTime);
            Integer newUser = reportMapper.getNewUserCount(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO ordersCount(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> validOrderList = new ArrayList<>();
        List<Integer> totalOrderList = new ArrayList<>();

        Integer total = 0;
        Integer valid = 0;


        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map<String, Object> map = new HashMap<>();
            map.put("endTime", endTime);
            map.put("beginTime", beginTime);

            Integer totalOrder = reportMapper.countByMap(map);
            totalOrder = totalOrder == null ? 0 : totalOrder;
            total += totalOrder;
            totalOrderList.add(totalOrder);

            map.put("status", Orders.COMPLETED);
            Integer validOrder = reportMapper.countByMap(map);
            validOrder = validOrder == null ? 0 : validOrder;
            valid += validOrder;
            validOrderList.add(validOrder);
        }

        double orderCompletionRate = total == 0 ? 0.0 : valid.doubleValue() / total;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(totalOrderList, ","))
                .validOrderCountList(StringUtils.join(validOrderList, ","))
                .totalOrderCount(total)
                .validOrderCount(valid)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesDTOS = reportMapper.salesTop10(beginTime, endTime);
        List<String> nameList = goodsSalesDTOS.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = goodsSalesDTOS.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }

    @Override
    public void export(HttpServletResponse response) {
        LocalDate end = LocalDate.now();
        LocalDate begin = end.plusDays(-30);
        LocalDate temp = begin;

        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        double amount = 0.0, orderCompleteRate = 0.0, averageOrderPrice = 0.0;
        Integer newUser = 0, totalOrder = 0, validOrder = 0;

        List<Double> sumAmountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        List<Double> orderCompleteReteList = new ArrayList<>();
        List<Double> averageOrderPriceList = new ArrayList<>();
        List<Integer> newUserCountList = new ArrayList<>();

        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map<String, Object> map = new HashMap<>();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer totalOrderCount = reportMapper.countByMap(map);
            totalOrderCount = totalOrderCount == null ? 0 : totalOrderCount;
            totalOrder += totalOrderCount;

            Integer newUserCount = reportMapper.getNewUserCount(map);
            newUserCount = newUserCount == null ? 0 : newUserCount;
            newUser += newUserCount;

            map.put("status", 5);
            Integer validOrderCount = reportMapper.countByMap(map);
            validOrderCount = validOrderCount == null ? 0 : validOrderCount;
            validOrder += validOrderCount;

            Double sumAmount = reportMapper.sumByMap(map);
            sumAmount = sumAmount == null ? 0 : sumAmount;
            amount += sumAmount;

            double orderRate = totalOrderCount == 0 ? 0 : validOrderCount.doubleValue() / totalOrderCount;
            double averagePrice = validOrderCount == 0 ? 0 : sumAmount / validOrderCount;

            sumAmountList.add(sumAmount);
            validOrderCountList.add(validOrderCount);
            orderCompleteReteList.add(orderRate);
            averageOrderPriceList.add(averagePrice);
            newUserCountList.add(newUserCount);
        }

        orderCompleteRate = validOrder.doubleValue() / totalOrder;
        averageOrderPrice = amount / validOrder;

        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = excel.getSheetAt(0);

            XSSFRow row = sheet.getRow(1);
            row.getCell(1).setCellValue("时间：" + temp + "至" + end);

            row = sheet.getRow(3);
            row.getCell(2).setCellValue(amount);
            row.getCell(4).setCellValue(orderCompleteRate);
            row.getCell(6).setCellValue(newUser);

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(validOrder);
            row.getCell(4).setCellValue(averageOrderPrice);

            for(int i = 0; i < 30; i++){
                row = sheet.getRow(i + 7);
                row.getCell(1).setCellValue(dateList.get(i).toString());
                row.getCell(2).setCellValue(sumAmountList.get(i));
                row.getCell(3).setCellValue(validOrderCountList.get(i));
                row.getCell(4).setCellValue(orderCompleteReteList.get(i));
                row.getCell(5).setCellValue(averageOrderPriceList.get(i));
                row.getCell(6).setCellValue(newUserCountList.get(i));
            }

            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            excel.close();
            outputStream.close();
            inputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
