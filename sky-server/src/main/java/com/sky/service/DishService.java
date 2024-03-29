package com.sky.service;

import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    DishVO queryById(Integer id);

    List<Dish> queryByCategoryId(Integer categoryId);

    void insert(DishVO dishVO);

    void updateStatus(Integer status, Integer id);

    void delete(List<Long> ids);

    void updateDish(DishVO dishVO);

}
