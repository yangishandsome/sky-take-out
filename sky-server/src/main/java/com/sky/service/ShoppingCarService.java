package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCarService {
    List<ShoppingCart> list();

    void add(ShoppingCartDTO shoppingCartDTO);

    void sub(ShoppingCartDTO shoppingCartDTO);

    void clean();
}
