package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFillAnno;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO queryById(Long id);

    @Select("select id, category_id, name, price, status, description, image, create_time, update_time, create_user, update_user " +
            "from setmeal where category_id = #{categoryId}")
    List<Setmeal> queryByCategoryId(Integer categoryId);

    @AutoFillAnno(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    void delete(List<Long> ids);

    @AutoFillAnno(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    List<Setmeal> list(Setmeal setmeal);

    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

}
