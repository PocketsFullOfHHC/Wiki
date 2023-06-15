package com.hhc.wiki.service;

import com.hhc.wiki.domain.Ebook;
import com.hhc.wiki.domain.EbookExample;
import com.hhc.wiki.mapper.EbookMapper;
import com.hhc.wiki.req.EbookReq;
import com.hhc.wiki.resp.EbookResp;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

// 使用持久层
// 使用Service注解，将这个Service交给Spring管理了，这样Spring才会扫描到这个类
@Service
public class EbookService {
    // 添加注解将ebookMapper注入进来：@Resource是jdk自带，@Autowired是springboot的，二者都可以
    // @resource注解就是把一个bean注入到当前的类中，可以不必通过配置文件或者导包的方式注入就可以使用该bean
    // Spring Bean是被实例的，组装的及被Spring 容器管理的Java对象
    @Resource
    // @Autowired
    // 将EbookMapper声明
    private EbookMapper ebookMapper;
    public List<EbookResp> list(EbookReq req){
        // 只要使用到example，前面的两句话都是固定的
        EbookExample ebookExample = new EbookExample();
        // criteria相当于where语句，用来添加条件
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        // 使用模糊查询(左右匹配)
        criteria.andNameLike("%"+ req.getName() +"%");
        // Example相当于sql查询中的where语句，用于select的添加条件
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        // 持久层返回了List<Ebook>类的ebookList，此时需要转化为List<EbookResp>，先创建一个空的List<EbookResp>
        List<EbookResp> respList = new ArrayList<>();
        for (Ebook ebook : ebookList) {
            EbookResp ebookResp = new EbookResp();
            // 需要每个都set一遍，太麻烦，这里使用BeanUtils工具类直接把整个ebook对象复制给ebookResp对象
            // ebookResp.setId(ebook.getId());
            // 参数：从第一个参数表示的对象拷贝到第二个参数表示的对象里面
            BeanUtils.copyProperties(ebook, ebookResp);
            // 测试：id是long类型
            // ebookResp.setId(123L);
            // 添加进respList中
            respList.add(ebookResp);
        }
        return respList;
    }
}
