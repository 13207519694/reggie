package com.yanhao.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanhao.reggie.dto.SetmealDto;
import com.yanhao.reggie.entity.Setmeal;

/**
 * @author yanhao
 * @note
 * @create 2022-07-13 下午 9:40
 */
public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public SetmealDto getByIdWithDish(Long id);
    public void updateWithDish(SetmealDto setmealDto);
    public void removeByIdWithDish(Long setmealId);
}
