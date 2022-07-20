package com.yanhao.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanhao.reggie.entity.AddressBook;
import com.yanhao.reggie.mapper.AddressBookMapper;
import com.yanhao.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author yanhao
 * @note
 * @create 2022-07-19 下午 3:11
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
