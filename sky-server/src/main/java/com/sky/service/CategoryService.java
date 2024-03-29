package com.sky.service;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    void add(Category category);

    void changeStatus(Integer status, Integer id);

    List<Category> queryByType(Integer type);

    void updateCate(Category category);

    void deleteById(Integer id);

}
