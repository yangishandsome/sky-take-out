package com.sky.controller.admin;

import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result pageQuery(DishPageQueryDTO dishPageQueryDTO){
        PageResult result = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> queryById(@PathVariable Long id){
        DishVO dishVO = dishService.queryById(id);
        return Result.success(dishVO);
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> queryByCategoryId(@RequestParam Integer categoryId){
        List<Dish> dishes = dishService.queryByCategoryId(categoryId);
        return Result.success(dishes);
    }

    @PostMapping
    @ApiOperation("新增菜品")
    public Result insert(@RequestBody DishVO dishVO){
        cleanRedisCache("dish_" + dishVO.getCategoryId());
        dishService.insert(dishVO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启售、停售菜品")
    public Result updateStatus(@PathVariable Integer status, @RequestParam Integer id){
        cleanRedisCache("dish_*");
        dishService.updateStatus(status, id);
        return Result.success();
    }

    @DeleteMapping()
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        cleanRedisCache("dish_*");
        dishService.delete(ids);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result updateDish(@RequestBody DishVO dishVO){
        cleanRedisCache("dish_*");
        dishService.updateDish(dishVO);
        return Result.success();
    }

    private void cleanRedisCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
