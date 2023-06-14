package com.hhc.wiki.service;

import com.hhc.wiki.domain.Test;
import com.hhc.wiki.mapper.TestMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

// 使用持久层
// 使用Service注解，将这个Service交给Spring管理了，这样Spring才会扫描到这个类
@Service
public class TestService {
    // 添加注解将testMapper注入进来：@Resource是jdk自带，@Autowired是springboot的，二者都可以
    @Resource
    // @Autowired
    // 将TestMapper声明
    private TestMapper testMapper;
    public List<Test> list(){
        return testMapper.list();
    }
}
