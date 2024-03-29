package com.sky.controller.admin;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult result = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(result);
    }

    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> queryByType(@RequestParam Integer type){
        List<Category> list = categoryService.queryByType(type);
        return Result.success(list);
    }

    @PostMapping
    @ApiOperation("新增分类")
    public Result add(@RequestBody Category category){
        categoryService.add(category);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用分类")
    public Result changeStatus(@PathVariable Integer status , @RequestParam Integer id){
        categoryService.changeStatus(status, id);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCate(@RequestBody Category category){
        categoryService.updateCate(category);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除分类")
    public Result deleteById(@RequestParam Integer id){
        categoryService.deleteById(id);
        return Result.success();
    }
}
