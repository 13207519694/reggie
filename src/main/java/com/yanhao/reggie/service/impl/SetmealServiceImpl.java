package com.yanhao.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanhao.reggie.dto.SetmealDto;
import com.yanhao.reggie.entity.Category;
import com.yanhao.reggie.entity.Setmeal;
import com.yanhao.reggie.entity.SetmealDish;
import com.yanhao.reggie.mapper.SetmealMapper;
import com.yanhao.reggie.service.CategoryService;
import com.yanhao.reggie.service.SetmealDishService;
import com.yanhao.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanhao
 * @note
 * @create 2022-07-13 下午 9:42
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
//    @Autowired
//    private CategoryService categoryService;

    /**
     * 新增套餐(包含菜品信息)
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map( item -> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 修改套餐的时候展示查询信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);

//        Category category = categoryService.getById(id);
//        if(category != null){
//            setmealDto.setCategoryName(category.getName());
//        }

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(lambdaQueryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    /**
     * 修改套餐
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map( item -> {
            LambdaUpdateWrapper<SetmealDish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId())
                    .eq(SetmealDish::getDishId,item.getDishId());
            setmealDishService.update(item,lambdaUpdateWrapper);
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public void removeByIdWithDish(Long setmealId) {
        this.removeById(setmealId);
        LambdaUpdateWrapper<SetmealDish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setmealDishService.remove(lambdaUpdateWrapper);
    }
}
