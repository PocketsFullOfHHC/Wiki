package com.hhc.wiki.resp;

import java.util.List;

public class PageResp<T> {
    private long total;

    // 查询到的数据会被封装成一个Resp类，不能确定是什么具体类，因此用泛型
    private List<T> list;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageResp{");
        sb.append("total=").append(total);
        sb.append(", list=").append(list);
        sb.append('}');
        return sb.toString();
    }
}