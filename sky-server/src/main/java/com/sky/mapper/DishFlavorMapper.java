package com.sky.mapper;

import com.sky.annotation.AutoFillAnno;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void insert(DishFlavor flavor);

    List<DishFlavor> queryByDishId(Integer dishId);

    void update(DishFlavor dishFlavor);

    void delete(List<Long> ids);

}
