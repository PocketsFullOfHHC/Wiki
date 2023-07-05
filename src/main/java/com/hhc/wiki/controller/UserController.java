package com.hhc.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.hhc.wiki.req.UserLoginReq;
import com.hhc.wiki.req.UserQueryReq;
import com.hhc.wiki.req.UserResetPasswordReq;
import com.hhc.wiki.req.UserSaveReq;
import com.hhc.wiki.resp.CommonResp;
import com.hhc.wiki.resp.PageResp;
import com.hhc.wiki.resp.UserLoginResp;
import com.hhc.wiki.resp.UserQueryResp;
import com.hhc.wiki.service.UserService;
import com.hhc.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

// 返回字符串(内容实例)
@RestController
// 提取出路径中公共的部分
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    // redis缓存token
    @Resource
    private RedisTemplate redisTemplate;

    // 雪花算法生成id
    @Resource
    private SnowFlake snowFlake;

    @GetMapping("/list")
    // @Valid开启校验规则
    public CommonResp list(@Valid UserQueryReq req) {
        // 创建一个返回值通用类的对象，因为查询表返回的是List<User>
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        // success默认为true，message为null，因此都无需设置
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    // 编辑时传入的参数和查询时传入的参数要有所区别：编辑需要传入的参数应该和电子书实体类一样的
    // @RequestBody注解对应的是json方式的POST提交，这样后端才能用json接收到
    public CommonResp save(@Valid @RequestBody UserSaveReq req) {
        // 密码加密：变成十六进制的密码
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    // {id}是restful编码风格传入删除内容的id的方法，加上{}是因为id是变化的。@PathVariable注解可以接收到路径传入的id参数
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req) {
        // 密码加密：变成十六进制的密码
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp<UserLoginResp> login(@Valid @RequestBody UserLoginReq req) {
        // 登录时将密码原文进行加密
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        // 用雪花算法生成token
        Long token = snowFlake.nextId();
        LOG.info("生成单点登录token：{}，并放入redis中", token);
        userLoginResp.setToken(token.toString());
        // 向redis数据库中放token，参数为key：上面雪花算法生成的token，值为userLoginResp，时效为24小时，时间的单位
        // 向redis中放入类，那么这个类一定要序列化：一般转化为json字符串即可
        redisTemplate.opsForValue().set(token, JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);

        resp.setContent(userLoginResp);
        return resp;
    }

    @GetMapping("/logout/{token}")
    // {id}是restful编码风格传入删除内容的id的方法，加上{}是因为id是变化的。@PathVariable注解可以接收到路径传入的id参数
    public CommonResp logout(@PathVariable String token) {
        CommonResp resp = new CommonResp<>();
        // 在redis中清空token
        redisTemplate.delete(token);
        LOG.info("从redis中删除token: {}", token);
        return resp;
    }
}
