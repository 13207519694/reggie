package com.yanhao.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanhao.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yanhao
 * @note
 * @create 2022-07-12 下午 5:00
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
