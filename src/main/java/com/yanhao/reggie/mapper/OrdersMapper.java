package com.yanhao.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanhao.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yanhao
 * @note
 * @create 2022-07-15 下午 3:24
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
