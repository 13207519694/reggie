package com.yanhao.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanhao.reggie.common.CustomException;
import com.yanhao.reggie.entity.Category;
import com.yanhao.reggie.entity.Dish;
import com.yanhao.reggie.entity.Setmeal;
import com.yanhao.reggie.mapper.CategoryMapper;
import com.yanhao.reggie.service.CategoryService;
import com.yanhao.reggie.service.DishService;
import com.yanhao.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yanhao
 * @note
 * @create 2022-07-13 下午 8:45
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行诊断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int countDish = dishService.count(dishLambdaQueryWrapper);
        if(countDish > 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询当前套餐是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int countSetmeal = setmealService.count(setmealLambdaQueryWrapper);
        if(countSetmeal > 0){
            throw new CustomException("当前套餐下关联了菜品，不能删除");
        }
        super.removeById(id);
    }
}
