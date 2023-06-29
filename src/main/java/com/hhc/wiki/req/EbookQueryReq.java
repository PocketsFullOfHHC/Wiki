package com.hhc.wiki.req;

// 很多内容都是分页查询，因此后面的很多请求参数类都需要集成分页参数类
public class EbookQueryReq extends PageReq{
    // 后端处理还是Long，返回前端会变成String
    // @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    private String name;

    private Long categoryId2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId2() {
        return categoryId2;
    }

    public void setCategoryId2(Long categoryId2) {
        this.categoryId2 = categoryId2;
    }

    // 项目里toString主要用来打日志

    @Override
    public String toString() {
        return "EbookQueryReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryId2=" + categoryId2 +
                "} " + super.toString();
    }
}