package com.yanhao.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanhao.reggie.entity.ShoppingCart;
import com.yanhao.reggie.mapper.ShoppingCartMapper;
import com.yanhao.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author yanhao
 * @note
 * @create 2022-07-19 下午 4:51
 */

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
