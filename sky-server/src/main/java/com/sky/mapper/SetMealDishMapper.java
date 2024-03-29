package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {

    void insert(List<SetmealDish> list);

    @Select("select * from setmeal_dish where setmeal_id = #{setMealId}")
    List<SetmealDish> queryBySetMealId(Long setMealId);

    List<Dish> queryByDishIds(List<Long> ids);

    void delete(List<Long> ids);

}
