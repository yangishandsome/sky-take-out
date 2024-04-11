package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    Double sumByMap(Map<String, Object> map);

    Integer getNewUserCount(Map<String, Object> map);

    Integer countByMap(Map<String, Object> map);

    List<GoodsSalesDTO> salesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
