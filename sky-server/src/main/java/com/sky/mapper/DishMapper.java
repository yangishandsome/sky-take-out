package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFillAnno;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    DishVO queryById(Long id);

    List<Dish> queryByDish(Dish dish);

    @Select("select status from dish where id in (select dish_id from setmeal_dish where setmeal_id = #{setMealId})")
    List<Long> queryDishStatusBySetMealId(Long setMealId);

    @AutoFillAnno(value = OperationType.INSERT)
    void insert(Dish dish);

    @AutoFillAnno(value = OperationType.UPDATE)
    void updateDish(Dish dish);

    void delete(List<Long> ids);

    Integer countByMap(Map map);
}
