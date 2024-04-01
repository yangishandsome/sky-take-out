package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCarController {

    @Autowired
    private ShoppingCarService shoppingCarService;

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> result = shoppingCarService.list();
        return Result.success(result);
    }

    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCarService.add(shoppingCartDTO);
        return Result.success();
    }

    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCarService.sub(shoppingCartDTO);
        return Result.success();
    }

    @DeleteMapping("clean")
    public Result clean(){
        shoppingCarService.clean();
        return Result.success();
    }

}
