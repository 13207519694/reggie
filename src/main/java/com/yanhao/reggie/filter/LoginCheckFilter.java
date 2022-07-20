package com.yanhao.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.yanhao.reggie.common.BaseContext;
import com.yanhao.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yanhao
 * @note
 * @create 2022-07-12 下午 9:42
 */

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1 获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求：{}",requestURI);

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3 如果不需要处理，则直接放行
        if(check){

            log.info("本次请求：{}不需要处理",requestURI);

            filterChain.doFilter(request,response);
            return;
        }

        //4 判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){

            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));

            //存储公共字段的id
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        };

        //6 判断登录状态，如果已登录，则直接放行（客户端）
        if(request.getSession().getAttribute("user") != null){

            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            //存储公共字段的id
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        };

        log.info("用户未登录");
        //5 如果未登录则返回未登录结果
        //通过数据流返回
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

//        //{}相当于占位符
//        log.info("拦截到请求：{}",request.getRequestURI());
//        filterChain.doFilter(request,response);
    }

    public boolean check(String[] urls,String requestURI){
        for (String url : urls){
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
