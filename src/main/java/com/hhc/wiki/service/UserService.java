package com.hhc.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hhc.wiki.domain.User;
import com.hhc.wiki.domain.UserExample;
import com.hhc.wiki.exception.BusinessException;
import com.hhc.wiki.exception.BusinessExceptionCode;
import com.hhc.wiki.mapper.UserMapper;
import com.hhc.wiki.req.UserQueryReq;
import com.hhc.wiki.req.UserSaveReq;
import com.hhc.wiki.resp.PageResp;
import com.hhc.wiki.resp.UserQueryResp;
import com.hhc.wiki.util.CopyUtil;
import com.hhc.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {
    // 打印日志
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req){
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        // 动态sql，否则就写死了，需要判断到底有没有name这个参数传进来，如果有就按照name去找，如果没有就不加这个条件
        if(!ObjectUtils.isEmpty(req.getLoginName())){
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        // 分页查询：参数为页码和每页的数据量：注意这里的第一页是从1开始，不是从0开始
        // 该分页查询只对下面的第一条sql语句起作用，后面的sql语句将不再进行分页操作
        PageHelper.startPage(req.getPage(), req.getSize());
        // Example相当于sql查询中的where语句，用于select的添加条件
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        // 获取总行数，并日志打印出来(占位符写法)
        LOG.info("总行数：{}", pageInfo.getTotal());
        // 获取总页数
        LOG.info("总页数：{}", pageInfo.getPages());

        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);
        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存用户数据
     * */
    public void save(UserSaveReq req){
        // 对象单体复制
        User user = CopyUtil.copy(req, User.class);
        if(ObjectUtils.isEmpty(user.getId())){
            User userDB = selectByLoginName(req.getLoginName());
            // 判断用户名是否重复
            if (ObjectUtils.isEmpty(userDB)) {
                // 新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                // 用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        }else{
            // 更新：防止更新后改变用户名
            user.setLoginName(null);
            // 防止修改密码
            user.setPassword(null);
            // 改成Selective表示user有值才去更新，否则不会去更新
            userMapper.updateByPrimaryKeySelective(user);
        }
    }
    /**
    *  删除用户数据
    * */
    public void delete(Long id){
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String LoginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(LoginName);
        // 虽然用户名唯一，最多只能查到一条，但mybatis只能用list接收查询数据
        List<User> userList = userMapper.selectByExample(userExample);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        } else {
            // 返回list数据的第一条
            return userList.get(0);
        }
    }
}
