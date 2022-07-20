package com.yanhao.reggie.config;

import com.yanhao.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author yanhao
 * @note springboot创建的工程要实现WebMvcConfigurer接口；直接创建工程要继承WebMvcConfigurationSupport类；还要注意一下两种方法重写的方法的权限不一样
 * @create 2022-07-13 下午 5:11
 */

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 扩展MVC消息转换器
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展的消息转换器开始工作...");
        //创建对象转换器
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层用Jackson将java对象转为JSON
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息换器对象追加到mvc框架的消息转换器集合
        converters.add(0,messageConverter);
        //super.extendMessageConverters(converters);
    }
}
