package com.hhc.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hhc.wiki.domain.Content;
import com.hhc.wiki.domain.Doc;
import com.hhc.wiki.domain.DocExample;
import com.hhc.wiki.exception.BusinessException;
import com.hhc.wiki.exception.BusinessExceptionCode;
import com.hhc.wiki.mapper.ContentMapper;
import com.hhc.wiki.mapper.DocMapper;
import com.hhc.wiki.mapper.DocMapperCust;
import com.hhc.wiki.req.DocQueryReq;
import com.hhc.wiki.req.DocSaveReq;
import com.hhc.wiki.resp.DocQueryResp;
import com.hhc.wiki.resp.PageResp;
import com.hhc.wiki.util.CopyUtil;
import com.hhc.wiki.util.RedisUtil;
import com.hhc.wiki.util.RequestContext;
import com.hhc.wiki.util.SnowFlake;
import com.hhc.wiki.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

// 使用持久层
// 使用Service注解，将这个Service交给Spring管理了，这样Spring才会扫描到这个类
@Service
public class DocService {
    // 打印日志
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    // 添加注解将docMapper注入进来：@Resource是jdk自带，@Autowired是springboot的，二者都可以
    // @resource注解就是把一个bean注入到当前的类中，可以不必通过配置文件或者导包的方式注入就可以使用该bean
    // Spring Bean是被实例的，组装的及被Spring 容器管理的Java对象
    @Resource
    // @Autowired
    // 将DocMapper声明
    private DocMapper docMapper;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private DocMapperCust docMapperCust;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    public WebSocketServer webSocketServer;

    /**
     * 分页查询
     **/
    public PageResp<DocQueryResp> list(DocQueryReq req){
        // 只要使用到example，前面的两句话都是固定的
        DocExample docExample = new DocExample();
        // 按sort字段的升序排序
        docExample.setOrderByClause("sort asc");
        // criteria相当于where语句，用来添加条件
        DocExample.Criteria criteria = docExample.createCriteria();
        // 分页查询：参数为页码和每页的数据量：注意这里的第一页是从1开始，不是从0开始
        // 该分页查询只对下面的第一条sql语句起作用，后面的sql语句将不再进行分页操作
        PageHelper.startPage(req.getPage(), req.getSize());
        // Example相当于sql查询中的where语句，用于select的添加条件
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        // 获取总行数，并日志打印出来(占位符写法)
        LOG.info("总行数：{}", pageInfo.getTotal());
        // 获取总页数
        LOG.info("总页数：{}", pageInfo.getPages());

        // 注意工具类里第二个参数并不是List<DocResp>，而是DocResp
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 查询全部
     * */
    public List<DocQueryResp> all(Long ebookId){
        DocExample docExample = new DocExample();
        // 添加条件：不能再用动态查询了，电子书的id对不上就不会返回任何结果
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        // 按sort字段的升序排序
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        return list;
    }

    /**
     * 保存分类数据
     * */
    public void save(DocSaveReq req){
        // 对象单体复制：只会copy req里面Doc的对应字段
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if(ObjectUtils.isEmpty(doc.getId())){
            // 没有id值说明是新增保存
            // 生成id并赋值
            doc.setId(snowFlake.nextId());
            // 新增时设置点赞数阅读数均为0，因为insert时不传值不会被数据库default
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        }else{
            // 点击编辑的保存已有数据后的编辑保存，不是新增保存
            // 根据主键id来更新：点击该方法查看发现传入的参数为doc类型
            docMapper.updateByPrimaryKey(doc);
            // blob代表富文本：包含大字段的更新，而updateByPrimaryKey只包含小字段的更新
            // 如果content表一开始什么内容都没有就无法根据id查询去添加content，因此此时需要用insert
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if(count == 0){
                contentMapper.insert(content);
            }
        }
    }
    /**
    *  删除分类数据
    * */
    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }

    /**
     *  级联删除文档
     * */
    public void delete(List<String> ids){
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        // 删除多个目标
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    /**
     *  查找文档内容
     * */
    public String findContent(Long id){
        Content content = contentMapper.selectByPrimaryKey(id);
        // 文档阅读数+1
        docMapperCust.increaseViewCount(id);
        // 注意使用getter时一定要判空，如果出现内容为空，则会空指针异常
        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else {
            return content.getContent();
        }
    }

    /**
     *  点赞
     * */
    public void vote(Long id){
        // 远程IP+doc.id作为key，24小时内不能重复
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600 * 24)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
        // 推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        webSocketServer.sendInfo("【" + docDb.getName() + "】被点赞！");
    }

    public void updateEbookInfo() {
        docMapperCust.updateEbookInfo();
    }
}
