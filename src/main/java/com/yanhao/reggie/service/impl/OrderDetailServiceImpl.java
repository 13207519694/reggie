package com.yanhao.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanhao.reggie.entity.OrderDetail;
import com.yanhao.reggie.mapper.OrderDetailMapper;
import com.yanhao.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author yanhao
 * @note
 * @create 2022-07-19 下午 7:39
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
