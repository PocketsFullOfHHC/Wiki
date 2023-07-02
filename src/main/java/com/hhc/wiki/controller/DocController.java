package com.hhc.wiki.controller;

import com.hhc.wiki.req.DocQueryReq;
import com.hhc.wiki.req.DocSaveReq;
import com.hhc.wiki.resp.CommonResp;
import com.hhc.wiki.resp.DocQueryResp;
import com.hhc.wiki.resp.PageResp;
import com.hhc.wiki.service.DocService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

// 返回字符串(内容实例)
@RestController
// 用来提取出路径中公共的部分，因为整个doc表的controller都要在这里写，因此可以把/doc路径提取出来
@RequestMapping("/doc")
public class DocController {

    // 将service注入进来
    @Resource
    private DocService docService;

    @GetMapping("/list")
    // @Valid开启校验规则
    public CommonResp list(@Valid DocQueryReq req) {
        // 创建一个返回值通用类的对象，因为查询表返回的是List<Doc>
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        // success默认为true，message为null，因此都无需设置
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/all")
    // @Valid开启校验规则
    public CommonResp all() {
        // 创建一个返回值通用类的对象，因为查询表返回的是List<Doc>
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all();
        // success默认为true，message为null，因此都无需设置
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    // 编辑时传入的参数和查询时传入的参数要有所区别：编辑需要传入的参数应该和分类实体类一样的
    // @RequestBody注解对应的是json方式的POST提交，这样后端才能用json接收到
    public CommonResp save(@Valid @RequestBody DocSaveReq req) {
        // 创建一个返回值通用类的对象，因为查询表返回的是List<Doc>
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{idsStr}")
    // {id}是restful编码风格传入删除内容的id的方法，加上{}是因为id是变化的。@PathVariable注解可以接收到路径传入的id参数
    public CommonResp delete(@PathVariable String idsStr) {
        // 创建一个返回值通用类的对象，因为查询表返回的是List<Doc>
        CommonResp resp = new CommonResp<>();
        // 先将字符串转为数组，再将其转化为集合List
        List<String> list = Arrays.asList(idsStr.split(","));
        docService.delete(list);
        return resp;
    }
}
