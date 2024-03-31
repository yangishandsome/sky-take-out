package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO queryById(Long id);

    void insert(SetmealDTO setmealDTO);

    void updateStatus(Integer status, Long id);

    void delete(List<Long> ids);

    void update(SetmealDTO setmealDTO);

    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getDishItemById(Long id);
}
