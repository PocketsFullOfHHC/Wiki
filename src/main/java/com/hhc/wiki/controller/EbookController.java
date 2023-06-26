package com.hhc.wiki.controller;

import com.hhc.wiki.req.EbookQueryReq;
import com.hhc.wiki.req.EbookSaveReq;
import com.hhc.wiki.resp.CommonResp;
import com.hhc.wiki.resp.EbookQueryResp;
import com.hhc.wiki.resp.PageResp;
import com.hhc.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

// 返回字符串(内容实例)
@RestController
// 用来提取出路径中公共的部分，因为整个ebook表的controller都要在这里写，因此可以把/ebook路径提取出来
@RequestMapping("/ebook")
public class EbookController {

    // 将service注入进来
    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    // @Valid开启校验规则
    public CommonResp list(@Valid EbookQueryReq req) {
        // 创建一个返回值通用类的对象，因为查询表返回的是List<Ebook>
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        // success默认为true，message为null，因此都无需设置
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    // 编辑时传入的参数和查询时传入的参数要有所区别：编辑需要传入的参数应该和电子书实体类一样的
    // @RequestBody注解对应的是json方式的POST提交，这样后端才能用json接收到
    public CommonResp save(@RequestBody EbookSaveReq req) {
        // 创建一个返回值通用类的对象，因为查询表返回的是List<Ebook>
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    // {id}是restful编码风格传入删除内容的id的方法，加上{}是因为id是变化的。@PathVariable注解可以接收到路径传入的id参数
    public CommonResp delete(@PathVariable Long id) {
        // 创建一个返回值通用类的对象，因为查询表返回的是List<Ebook>
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }
}
