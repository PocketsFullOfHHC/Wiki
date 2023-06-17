package com.hhc.wiki.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // pathPattern参数为映射的请求地址，/**表示针对所有的接口请求地址
        registry.addMapping("/**")
                // 允许来源
                .allowedOriginPatterns("*")
                .allowedHeaders(CorsConfiguration.ALL)
                // 允许的访问方法
                .allowedMethods(CorsConfiguration.ALL)
                // 允许前端带上它的凭证，例如cookie信息等等
                .allowCredentials(true)
                // 1小时内不需要再预检（预检就是前端会在get请求之前先发一个OPTIONS请求，负责检查get请求中的接口是否存在）
                .maxAge(3600);
    }

}
