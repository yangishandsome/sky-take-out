package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult result = setMealService.pageQuery(setmealPageQueryDTO);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> queryById(@PathVariable Long id){
        SetmealVO setmealVO = setMealService.queryById(id);
        return Result.success(setmealVO);
    }

    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setMealCache",key = "#setmealDTO.categoryId")
    public Result insert(@RequestBody SetmealDTO setmealDTO){
        setMealService.insert(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐启售、停售")
    @CacheEvict(cacheNames = "setMealCache",allEntries = true)
    public Result updateStatus(@PathVariable Integer status, @RequestParam Long id){
        setMealService.updateStatus(status, id);
        return  Result.success();
    }

    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setMealCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        setMealService.delete(ids);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setMealCache",allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setMealService.update(setmealDTO);
        return Result.success();
    }
}
