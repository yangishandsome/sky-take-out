package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFillAnno;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CategoryMapper {

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);


    List<Category> queryByType(Integer type);

    @AutoFillAnno(value = OperationType.INSERT)
    void add(Category category);

    @AutoFillAnno(value = OperationType.UPDATE)
    @Update("update sky_take_out.category set status = #{status}, update_time = #{updateTime}, category.update_user = #{updateUser} where id = #{id}")
    void changeStatus(Category category);

    @AutoFillAnno(value = OperationType.UPDATE)
    void updateCate(Category category);

    @Delete("delete from sky_take_out.category where id = #{id}")
    void deleteById(Integer id);

}
