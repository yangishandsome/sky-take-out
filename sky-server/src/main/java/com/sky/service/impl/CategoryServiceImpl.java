package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetMealMapper setMealMapper;

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        int start = categoryPageQueryDTO.getPage();
        int pageSize = categoryPageQueryDTO.getPageSize();

        PageHelper.startPage(start, pageSize);
        Page<Category> page= categoryMapper.pageQuery(categoryPageQueryDTO);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getPageSize());
        pageResult.setRecords(page.getResult());

        return pageResult;
    }

    @Override
    public void add(Category category) {
        category.setStatus(0);

        categoryMapper.add(category);
    }

    @Override
    public void changeStatus(Integer status, Integer id) {
        Category category = new Category();
        category.setId(id.longValue());
        category.setStatus(status);

        categoryMapper.changeStatus(category);
    }

    @Override
    public List<Category> queryByType(Integer type) {
        return categoryMapper.queryByType(type);
    }

    @Override
    public void updateCate(Category category) {
        categoryMapper.updateCate(category);
    }

    @Override
    public void deleteById(Integer id) {
        List<Dish> dishes = dishMapper.queryByCategoryId(id);
        if(!dishes.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        List<Setmeal> setmeals = setMealMapper.queryByCategoryId(id);
        if(!setmeals.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        categoryMapper.deleteById(id);
    }
}
