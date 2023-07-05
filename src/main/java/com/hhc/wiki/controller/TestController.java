package com.hhc.wiki.controller;

import com.hhc.wiki.domain.Test;
import com.hhc.wiki.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

// 返回字符串
@RestController
// 用来返回页面(内容实例)
//@Controller
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
    
    // 变量需要用${}包住
    // 启动时，SpringBoot就会扫描所有的类，扫描到类里面有Value注解时，就会找里面text.hello这个配置项，若没有这个配置项，则会返回默认配置值TEST(优先读配置文件)
    @Value("${test.hello:TEST}")
    private String testHello;

    // 将service注入进来
    @Resource
    private TestService testService;

    @Resource
    private RedisTemplate redisTemplate;

    // 添加接口对应的请求地址，如果只是简单的用@RequestMapping进行注解，表示这个接口支持所有的请求方式(GET/POST/PUT/DELETE)
    // @GetMapping("/hello")
    // 当然也可以用@GetMapping等只支持一种请求方式的注解
    // restful风格演示：删除user为1的那一项
    // @RequestMapping(value = "user/1", method = RequestMethod.DELETE)
    // 这里面浏览器直接访问地址，为GET请求，若这里的注解写成@PostMapping，则会报405错误
    // @RequestMapping("/hello")
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!" + testHello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name) {
        return "Hello World! Post," + name;
    }

    @GetMapping("/test/list")
    public List<Test> list() {
        return testService.list();
    }

    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        LOG.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable Long key) {
        Object object = redisTemplate.opsForValue().get(key);
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }
}
