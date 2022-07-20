package com.yanhao.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanhao.reggie.entity.Category;

/**
 * @author yanhao
 * @note
 * @create 2022-07-13 下午 8:44
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
