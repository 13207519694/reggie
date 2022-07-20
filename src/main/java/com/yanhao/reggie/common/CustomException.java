package com.yanhao.reggie.common;

/**
 * @author yanhao
 * @note 自定义业务异常
 * @create 2022-07-13 下午 10:02
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
