package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFillAnno;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        int start = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();

        PageHelper.startPage(start, pageSize);
        Page<SetmealVO> page = setMealMapper.pageQuery(setmealPageQueryDTO);

        PageResult result = new PageResult();
        result.setTotal(page.getTotal());
        result.setRecords(page.getResult());

        return result;
    }

    @Transactional
    @Override
    public SetmealVO queryById(Long id) {
        SetmealVO setmealVO = setMealMapper.queryById(id);
        setmealVO.setSetmealDishes(setMealDishMapper.queryBySetMealId(id));
        return setmealVO;
    }


    @Transactional
    @Override
    public void insert(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.insert(setmeal);

        Long id = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish dish : setmealDishes) {
            dish.setSetmealId(id);
        }
        setMealDishMapper.insert(setmealDishes);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        if(status == StatusConstant.ENABLE){
            List<Long> dishStatus = dishMapper.queryDishStatusBySetMealId(id);
            for(Long statue : dishStatus){
                if(statue == StatusConstant.DISABLE.longValue()){
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status).build();
        setMealMapper.update(setmeal);
    }


    @Override
    @Transactional
    public void delete(List<Long> ids) {
        for(Long id : ids){
            SetmealVO setmealVO = setMealMapper.queryById(id);
            if(setmealVO != null && setmealVO.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        setMealMapper.delete(ids);
        setMealDishMapper.delete(ids);
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.update(setmeal);


        Long id = setmealDTO.getId();
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        for(SetmealDish dish : dishes){
            dish.setSetmealId(id);
        }
        setMealDishMapper.delete(Collections.singletonList(id));
        setMealDishMapper.insert(dishes);
    }

    public List<Setmeal> list(Setmeal setmeal) {
        return setMealMapper.list(setmeal);
    }

    public List<DishItemVO> getDishItemById(Long id) {
        return setMealMapper.getDishItemBySetmealId(id);
    }
}
