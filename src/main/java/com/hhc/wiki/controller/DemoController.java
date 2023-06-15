package com.hhc.wiki.controller;

import com.hhc.wiki.domain.Demo;
import com.hhc.wiki.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

// 返回字符串(内容实例)
@RestController
// 用来提取出路径中公共的部分，因为整个demo表的controller都要在这里写，因此可以把/demo路径提取出来
@RequestMapping("/demo")
public class DemoController {

    // 将service注入进来
    @Resource
    private DemoService demoService;

    @GetMapping("/list")
    public List<Demo> list() {
        return demoService.list();
    }
}
