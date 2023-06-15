package com.hhc.wiki.controller;

import com.hhc.wiki.domain.Ebook;
import com.hhc.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

// 返回字符串(内容实例)
@RestController
// 用来提取出路径中公共的部分，因为整个ebook表的controller都要在这里写，因此可以把/ebook路径提取出来
@RequestMapping("/ebook")
public class EbookController {

    // 将service注入进来
    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public List<Ebook> list() {
        return ebookService.list();
    }
}
