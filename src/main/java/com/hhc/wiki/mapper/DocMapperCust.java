package com.hhc.wiki.mapper;

import org.apache.ibatis.annotations.Param;

public interface DocMapperCust {
    // 这里面注解的id要和DocMapperCust.xml里面的动态变量id对应起来
    public void increaseViewCount(@Param("id") Long id);

    public void increaseVoteCount(@Param("id") Long id);

    public void updateEbookInfo();
}
