package com.yanhao.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanhao.reggie.dto.DishDto;
import com.yanhao.reggie.entity.Dish;
import com.yanhao.reggie.entity.DishFlavor;
import com.yanhao.reggie.mapper.DishMapper;
import com.yanhao.reggie.service.DishFlavorService;
import com.yanhao.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yanhao
 * @note
 * @create 2022-07-13 下午 9:41
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品里有口味配置
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //只提交对应的数据？
        this.save(dishDto);

        Long dishId = dishDto.getId();
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();

        //Stream流/管道
        dishFlavorList.stream().map(item -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //增强for循环
//        for(DishFlavor dishFlavor : dishFlavorList){
//            dishFlavor.setDishId(dishId);
//        }
        //foreach
//        dishFlavorList.forEach(dishFlavor -> {
//            dishFlavor.setDishId(dishId);
//        });

        dishFlavorService.saveBatch(dishFlavorList);
    }

    /**
     * 修改菜品时获取完整的DishDto数据
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    /**
     * 更新/修改菜品（包含口味配置）
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map(item -> {
            LambdaUpdateWrapper<DishFlavor> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(DishFlavor::getDishId,item.getDishId());
            boolean update = dishFlavorService.update(item,lambdaUpdateWrapper);
            return item;
        }).collect(Collectors.toList());

    }

    /**
     * 批量删除菜品及其口味
     * @param dishIds
     */
    @Override
    public void removeByIdsWithFlavor(List<Long> dishIds) {
        this.removeByIds(dishIds);
//        boolean remove = dishFlavorService.removeByIds(dishIds);

        for (Long item : dishIds){
            LambdaUpdateWrapper<DishFlavor> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(DishFlavor::getDishId,item);
            dishFlavorService.remove(lambdaUpdateWrapper);
        }

    }

    /**
     * 删除菜品及其口味
     * @param dishId
     */
    @Override
    @Transactional
    public void removeByIdWithFlavor(Long dishId) {
        this.removeById(dishId);
//        boolean remove = dishFlavorService.removeById(dishId);
        LambdaUpdateWrapper<DishFlavor> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(lambdaUpdateWrapper);
    }
}
