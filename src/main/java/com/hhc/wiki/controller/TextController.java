package com.hhc.wiki.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

// 返回字符串
@RestController
// 用来返回页面
//@Controller
public class TextController {
    // 添加接口对应的请求地址，如果只是简单的用@RequestMapping进行注解，表示这个接口支持所有的请求方式(GET/POST/PUT/DELETE)
    // @GetMapping("/hello")
    // 当然也可以用@GetMapping等只支持一种请求方式的注解
    // restful风格演示：删除user为1的那一项
    // @RequestMapping(value = "user/1", method = RequestMethod.DELETE)
    // 这里面浏览器直接访问地址，为GET请求，若这里的注解写成@PostMapping，则会报405错误
    // @RequestMapping("/hello")
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @PostMapping("/hello/post")
    public String helloPost(String name) {
        return "Hello World! Post," + name;
    }
}
