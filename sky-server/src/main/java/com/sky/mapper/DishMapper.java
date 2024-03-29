package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFillAnno;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    DishVO queryById(Integer id);

    @Select("select id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user " +
            "from dish where category_id = #{categoryId}")
    List<Dish> queryByCategoryId(Integer categoryId);

    @Select("select status from dish where id in (select dish_id from setmeal_dish where setmeal_id = #{setMealId})")
    List<Long> queryDishStatusBySetMealId(Long setMealId);

    @AutoFillAnno(value = OperationType.INSERT)
    void insert(Dish dish);

    @AutoFillAnno(value = OperationType.UPDATE)
    void updateDish(Dish dish);

    void delete(List<Long> ids);

}
