package com.hhc.wiki.controller;

import com.hhc.wiki.req.CategoryQueryReq;
import com.hhc.wiki.req.CategorySaveReq;
import com.hhc.wiki.resp.CommonResp;
import com.hhc.wiki.resp.CategoryQueryResp;
import com.hhc.wiki.resp.PageResp;
import com.hhc.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

// 返回字符串(内容实例)
@RestController
// 用来提取出路径中公共的部分，因为整个category表的controller都要在这里写，因此可以把/category路径提取出来
@RequestMapping("/category")
public class CategoryController {

    // 将service注入进来
    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    // @Valid开启校验规则
    public CommonResp list(@Valid CategoryQueryReq req) {
        // 创建一个返回值通用类的对象，因为查询表返回的是List<Category>
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        // success默认为true，message为null，因此都无需设置
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    // 编辑时传入的参数和查询时传入的参数要有所区别：编辑需要传入的参数应该和分类实体类一样的
    // @RequestBody注解对应的是json方式的POST提交，这样后端才能用json接收到
    public CommonResp save(@Valid @RequestBody CategorySaveReq req) {
        // 创建一个返回值通用类的对象，因为查询表返回的是List<Category>
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    // {id}是restful编码风格传入删除内容的id的方法，加上{}是因为id是变化的。@PathVariable注解可以接收到路径传入的id参数
    public CommonResp delete(@PathVariable Long id) {
        // 创建一个返回值通用类的对象，因为查询表返回的是List<Category>
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}
