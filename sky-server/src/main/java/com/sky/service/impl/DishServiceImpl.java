package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFillAnno;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        int start = dishPageQueryDTO.getPage();
        int pageSize = dishPageQueryDTO.getPageSize();
        PageHelper.startPage(start, pageSize);

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());

        return pageResult;
    }

    @Override
    @Transactional
    public DishVO queryById(Integer id) {
        DishVO dishVO = dishMapper.queryById(id);;
        List<DishFlavor> dishFlavors = dishFlavorMapper.queryByDishId(id);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    public List<Dish> queryByCategoryId(Integer categoryId) {
        return dishMapper.queryByCategoryId(categoryId);
    }

    @Override
    @Transactional
    public void insert(DishVO dishVO) {
        Dish dish = Dish.builder()
                .id(dishVO.getId())
                .name(dishVO.getName())
                .image(dishVO.getImage())
                .price(dishVO.getPrice())
                .categoryId(dishVO.getCategoryId())
                .status(dishVO.getStatus())
                .description(dishVO.getDescription()).build();

        List<DishFlavor> flavors = dishVO.getFlavors();
        dishMapper.insert(dish);
        Long dishId = dish.getId();

        for(DishFlavor flavor : flavors){
            flavor.setDishId(dishId);
            dishFlavorMapper.insert(flavor);
        }
    }

    @Override
    public void updateStatus(Integer status, Integer id) {
        Dish dish = Dish.builder()
                .id(id.longValue())
                .status(status).build();
        dishMapper.updateDish(dish);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        for(Long id : ids){
            DishVO dishVO = dishMapper.queryById(Math.toIntExact(id));
            if(StatusConstant.ENABLE == dishVO.getStatus()){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        List<Dish> dishes = setMealDishMapper.queryByDishIds(ids);
        if(!dishes.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        dishMapper.delete(ids);
        dishFlavorMapper.delete(ids);
    }

    @Override
    @Transactional
    public void updateDish(DishVO dishVO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishVO, dish);
        dishMapper.updateDish(dish);

        List<DishFlavor> flavors = dishVO.getFlavors();
        Long dishId = dishVO.getId();
        dishFlavorMapper.delete(Collections.singletonList(dishId));
        for(DishFlavor dishFlavor : flavors){
            dishFlavor.setDishId((dishId));
            dishFlavorMapper.insert(dishFlavor);
        }
    }


}
