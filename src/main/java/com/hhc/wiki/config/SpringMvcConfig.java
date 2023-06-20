// 配置需要拦截器的接口请求
package com.hhc.wiki.config;

// 引入拦截器
import com.hhc.wiki.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    // 将拦截器注入进来
    @Resource
    LogInterceptor logInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                // 针对所有的请求(除了登录接口：登录接口无需做登录校验，因为此时一定是未登录的，这样会陷入死循环)
                .addPathPatterns("/**").excludePathPatterns("/login");
    }
}
