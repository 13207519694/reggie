package com.yanhao.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanhao.reggie.entity.User;
import com.yanhao.reggie.mapper.UserMapper;
import com.yanhao.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author yanhao
 * @note
 * @create 2022-07-18 下午 9:36
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
