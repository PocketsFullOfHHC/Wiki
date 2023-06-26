package com.hhc.wiki.req;

// 很多内容都是分页查询，因此后面的很多请求参数类都需要集成分页参数类
public class EbookQueryReq extends PageReq{
    // 后端处理还是Long，返回前端会变成String
    // @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    private String name;

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

    // 项目里toString主要用来打日志
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append("]");
        return sb.toString();
    }
}