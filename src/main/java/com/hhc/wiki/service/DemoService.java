package com.hhc.wiki.service;

import com.hhc.wiki.domain.Demo;
import com.hhc.wiki.mapper.DemoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

// 使用持久层
// 使用Service注解，将这个Service交给Spring管理了，这样Spring才会扫描到这个类
@Service
public class DemoService {
    // 添加注解将demoMapper注入进来：@Resource是jdk自带，@Autowired是springboot的，二者都可以
    // @resource注解就是把一个bean注入到当前的类中，可以不必通过配置文件或者导包的方式注入就可以使用该bean
    // Spring Bean是被实例的，组装的及被Spring 容器管理的Java对象
    @Resource
    // @Autowired
    // 将DemoMapper声明
    private DemoMapper demoMapper;
    public List<Demo> list(){
        // Example相当于sql查询中的where语句，用于select的添加条件
        return demoMapper.selectByExample(null);
    }
}
