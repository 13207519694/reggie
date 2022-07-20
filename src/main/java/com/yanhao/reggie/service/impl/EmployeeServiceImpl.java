package com.yanhao.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanhao.reggie.entity.Employee;
import com.yanhao.reggie.mapper.EmployeeMapper;
import com.yanhao.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author yanhao
 * @note
 * @create 2022-07-12 下午 5:03
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
