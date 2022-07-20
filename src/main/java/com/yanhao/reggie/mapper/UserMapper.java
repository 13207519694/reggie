package com.yanhao.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanhao.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yanhao
 * @note
 * @create 2022-07-18 下午 9:34
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
