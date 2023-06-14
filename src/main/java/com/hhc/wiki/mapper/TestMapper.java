package com.hhc.wiki.mapper;

import com.hhc.wiki.domain.Test;

import java.util.List;

// 这个层类似之前的DAO层，即持久层，专门用来执行sql的语句
public interface TestMapper {
    public List<Test> list();
}
