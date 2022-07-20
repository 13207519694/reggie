package com.yanhao.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanhao.reggie.dto.DishDto;
import com.yanhao.reggie.entity.Dish;

import java.util.List;

/**
 * @author yanhao
 * @note
 * @create 2022-07-13 下午 9:39
 */
public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlavor(Long id);
    public void updateWithFlavor(DishDto dishDto);
    public void removeByIdWithFlavor(Long dishId);
    public void removeByIdsWithFlavor(List<Long> dishIds);
}
