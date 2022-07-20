package com.yanhao.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanhao.reggie.entity.Orders;

/**
 * @author yanhao
 * @note
 * @create 2022-07-15 下午 3:25
 */
public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);
}
