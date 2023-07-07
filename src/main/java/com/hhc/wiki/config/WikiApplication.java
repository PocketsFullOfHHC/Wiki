package com.hhc.wiki.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

// 指定该wiki启动类扫描的包的范围：如不指定，原则上只能访问到该类所在包(config包)下面的子包，就不会访问到controller
// 加上扫描包的注解后指定范围(可以写的通用一点：com.hhc，但不要太通用写成com，这样会识别到第三方的jar)即可
// 可能会引入多个包，还可能会扫描jar包，因此用列表将指定范围包起来
@ComponentScan({"com.hhc.wiki"})
@SpringBootApplication
// 让项目知道TestMapper就是持久层
@MapperScan("com.hhc.wiki.mapper")
// 启用定时任务
@EnableScheduling
// 启用异步
@EnableAsync
public class WikiApplication {

    private static final Logger LOG = LoggerFactory.getLogger(WikiApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WikiApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("地址: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
    }

}
