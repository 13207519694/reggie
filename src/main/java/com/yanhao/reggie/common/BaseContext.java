package com.yanhao.reggie.common;

import com.yanhao.reggie.config.WebMvcConfig;

/**
 * 基于ThreadLocal封装的工具类，用户保存和获取当前登录用户ID
 * @author yanhao
 * @note
 * @create 2022-07-13 下午 8:20
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
