package com.hhc.wiki.req;

// 很多内容都是分页查询，因此后面的很多请求参数类都需要集成分页参数类
public class DocQueryReq extends PageReq{
    @Override
    public String toString() {
        return "DocQueryReq{} " + super.toString();
    }
}