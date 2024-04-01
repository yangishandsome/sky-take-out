package com.sky.service;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCarMapper;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCarServiceImpl implements ShoppingCarService{

    @Autowired
    private ShoppingCarMapper shoppingCarMapper;

    @Autowired
    private SetMealMapper setMealMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        return shoppingCarMapper.list(userId);
    }

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCarMapper.getByIds(shoppingCart);

        if(list != null && !list.isEmpty()){
            shoppingCart = list.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCarMapper.update(shoppingCart);
        }else{
            if(shoppingCart.getSetmealId() != null){
                shoppingCart.setUserId(BaseContext.getCurrentId());
                SetmealVO setmealVO = setMealMapper.queryById(shoppingCart.getSetmealId());

                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setNumber(1);
                shoppingCart.setAmount(setmealVO.getPrice());
            }else {
                DishVO dishVO = dishMapper.queryById(shoppingCart.getDishId());

                shoppingCart.setName(dishVO.getName());
                shoppingCart.setImage(dishVO.getImage());
                shoppingCart.setDishFlavor(dishVO.getFlavors().toString());
                shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCart.setNumber(1);
                shoppingCart.setAmount(dishVO.getPrice());
            }
            shoppingCarMapper.add(shoppingCart);
        }



    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCarMapper.sub(shoppingCart);
    }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCarMapper.clean(userId);
    }
}
