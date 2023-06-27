package com.hhc.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hhc.wiki.domain.Category;
import com.hhc.wiki.domain.CategoryExample;
import com.hhc.wiki.mapper.CategoryMapper;
import com.hhc.wiki.req.CategoryQueryReq;
import com.hhc.wiki.req.CategorySaveReq;
import com.hhc.wiki.resp.CategoryQueryResp;
import com.hhc.wiki.resp.PageResp;
import com.hhc.wiki.util.CopyUtil;
import com.hhc.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

// 使用持久层
// 使用Service注解，将这个Service交给Spring管理了，这样Spring才会扫描到这个类
@Service
public class CategoryService {
    // 打印日志
    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    // 添加注解将categoryMapper注入进来：@Resource是jdk自带，@Autowired是springboot的，二者都可以
    // @resource注解就是把一个bean注入到当前的类中，可以不必通过配置文件或者导包的方式注入就可以使用该bean
    // Spring Bean是被实例的，组装的及被Spring 容器管理的Java对象
    @Resource
    // @Autowired
    // 将CategoryMapper声明
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req){
        // 只要使用到example，前面的两句话都是固定的
        CategoryExample categoryExample = new CategoryExample();
        // criteria相当于where语句，用来添加条件
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        // 分页查询：参数为页码和每页的数据量：注意这里的第一页是从1开始，不是从0开始
        // 该分页查询只对下面的第一条sql语句起作用，后面的sql语句将不再进行分页操作
        PageHelper.startPage(req.getPage(), req.getSize());
        // Example相当于sql查询中的where语句，用于select的添加条件
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        // 获取总行数，并日志打印出来(占位符写法)
        LOG.info("总行数：{}", pageInfo.getTotal());
        // 获取总页数
        LOG.info("总页数：{}", pageInfo.getPages());

        // 注意工具类里第二个参数并不是List<CategoryResp>，而是CategoryResp
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);
        PageResp<CategoryQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存分类数据
     * */
    public void save(CategorySaveReq req){
        // 对象单体复制
        Category category = CopyUtil.copy(req, Category.class);
        if(ObjectUtils.isEmpty(category.getId())){
            // 没有id值说明是新增保存
            // 生成id并赋值
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }else{
            // 点击编辑的保存已有数据后的编辑保存，不是新增保存
            // 根据主键id来更新：点击该方法查看发现传入的参数为category类型
            categoryMapper.updateByPrimaryKey(category);
        }
    }
    /**
    *  删除分类数据
    * */
    public void delete(Long id){
        categoryMapper.deleteByPrimaryKey(id);
    }
}
