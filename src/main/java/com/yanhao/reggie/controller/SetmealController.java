package com.yanhao.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanhao.reggie.common.R;
import com.yanhao.reggie.dto.SetmealDto;
import com.yanhao.reggie.entity.*;
import com.yanhao.reggie.service.CategoryService;
import com.yanhao.reggie.service.SetmealDishService;
import com.yanhao.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanhao
 * @note
 * @create 2022-07-15 下午 1:16
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;
//

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());

        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 分页查询套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name)
                .orderByDesc(Setmeal::getUpdateTime);
        pageInfo = setmealService.page(pageInfo,lambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> setmealDtoPageRecords = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoPageRecords);

        return R.success(setmealDtoPage);
    }

    /**
     * 修改套餐信息时查询展示数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        log.info("根据id查询套餐信息...");
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
//        Category category = categoryService.getById(id);
//        if(category != null){
//            setmealDto.setCategoryName(category.getName());
//            return R.success(setmealDto);
//        }
//        return R.error("没有查询到套餐信息");
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    /**
     * 删除套餐
     * 可以直接用list(Long)接收！！！
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String ids){
        //ids传的是字符串，没办法直接赋值List集合
        String[] split = ids.split(",");
        for(String item : split){
            Long setmealId = Long.parseLong(item);
            setmealService.removeByIdWithDish(setmealId);
            log.info(setmealId.toString());
        }
        return R.success("删除菜品成功");
    }

    /**
     * 停售/启售 单个/批量
     * @param request
     * @param ids
     * @return
     */
    @PostMapping("/status/**")
    public R<String> statusStop(HttpServletRequest request, String ids){
        String requestURI = request.getRequestURI();
        String statusStr = requestURI.substring(requestURI.lastIndexOf("/")+1);
        int status = Integer.parseInt(statusStr);
        String[] split = ids.split(",");

        for(String item : split){
            Long setmealId = Long.parseLong(item);
            LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(Setmeal::getStatus,status).eq(Setmeal::getId,setmealId);
            boolean update = setmealService.update(lambdaUpdateWrapper);
            log.info(setmealId.toString() + update);
        }
        return R.success((status == 0) ? "停售菜品成功" : "启售菜品成功");
    }

    /**
     * 展示在新增套餐时的菜品list列表-------------------新增查询套餐内菜品，返回setmealDto
     * http://localhost:8081/dish/list?categoryId=1397844263642378242
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public R<List> list(Long categoryId){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(categoryId != null,Setmeal::getCategoryId,categoryId)
                .eq(Setmeal::getStatus,1)
                .orderByAsc(Setmeal::getUpdateTime);
        List<Setmeal> setmealListt = setmealService.list(lambdaQueryWrapper);

        List<SetmealDto> setmealDtoList = setmealListt.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);

            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            Long setmealId = item.getId();
            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapperSetmealDish = new LambdaQueryWrapper<>();
            lambdaQueryWrapperSetmealDish.eq(SetmealDish::getSetmealId,setmealId);
            List<SetmealDish> setmealDishList = setmealDishService.list(lambdaQueryWrapperSetmealDish);
            setmealDto.setSetmealDishes(setmealDishList);
            return setmealDto;
        }).collect(Collectors.toList());

        return R.success(setmealDtoList);
    }

    /**
     * 查询套餐的菜品详细信息----------------图片功能未完成
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List> dishDetail(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishList = setmealDishService.list(lambdaQueryWrapper);
        return R.success(setmealDishList);
    }
}
