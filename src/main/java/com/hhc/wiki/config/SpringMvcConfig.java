// 配置需要拦截器的接口请求
package com.hhc.wiki.config;

//// 引入拦截器
//import com.hhc.wiki.interceptor.LogInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import javax.annotation.Resource;
//
//@Configuration
//public class SpringMvcConfig implements WebMvcConfigurer {
//
//    // 将拦截器注入进来
//    @Resource
//    LogInterceptor logInterceptor;
//
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(logInterceptor)
//                // 针对所有的请求(除了登录接口：登录接口无需做登录校验，因为此时一定是未登录的，这样会陷入死循环)
//                .addPathPatterns("/**").excludePathPatterns("/login");
//    }
//}

import com.hhc.wiki.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    LoginInterceptor loginInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        // 配置无需拦截的页面路径，这是拦截器的优势，两个星号表示后面的值是任意的
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/test/**",
                        "/redis/**",
                        "/user/login",
                        "/category/all",
                        "/ebook/list",
                        "/doc/all/**",
                        "/doc/find-content/**",
                        "/doc/vote/**",
                        "/doc/find-content/**"
                );
    }
}
