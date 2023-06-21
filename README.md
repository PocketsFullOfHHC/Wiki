# 项目概述：


## 第一天工作：
1. **搭建SpringBoot框架**：
    * 修改pom.xml里面的版本号
2. **项目的初始配置**：
    * 编码配置：UTF-8
    * Maven配置：阿里云镜像settings.xml
    * Git配置：通过.gitignore来忽略文件
3. **代码关联Git远程仓库**：
    * github新建仓库Wiki及SSH密钥
    * 相关git命令：push, pull, clone, remote，commit
    * 增加README.md文件
4. **启动日志优化**：
    * 增加logback配置文件
    * 修改启动成功的文案和图案
5. **开发Hello World接口**：
    * 制作接口@GetMapping和@RestController(指定返回字符串Hello World)
    * 项目分层:
        * config层用于放配置类WikiApplication：@ComponentScan指定该wiki启动类扫描的包的范围
        * controller层用于定义接口，是请求的入口
6. **增加hello接口测试test.http**：
    * IDEA自带的HTTP Client测试接口
7. **配置文件**：
    * SpringBoot会自动识别下面这组配置文件：
        * application.properties/yml
        * config/application.properties/yml
    * 如果是SpringCloud，还会自动识别下面这组配置文件：
        * bootstrap.properties/yml
        * config/bootstrap.properties/yml
    * 自定义配置项的使用：用@Value来读。
8. **修改错误命名的test.http和TestController**

## 第二天工作：

1. **集成热部署**：
    * 引入依赖包
    * 开启静态自动编译
    * 开启动态自动编译
2. **本地数据库准备**：
    * 在root@localhost(主链接)创建数据库wiki
    * 创建专用账户Wiki@localhost并赋予其wiki的访问权限
    * 新建链接wiki@locallhost
3. **IDEA数据库插件配置**：
    * 新增数据源，指定数据库的专用用户名和访问密码及数据库名称(配置时区)
    * 建表测试：all.sql
4. **集成持久层框架Mybatis**：
    * 集成Mybatis:
        * 引入依赖
        * 配置数据源(同样需要配置时区)
    * 测试Mybatis：开发/test/list接口，查询test表所有数据。流程：
        1. 程序的入口在TestController
        2. 我们在test.http中访问了/test/list，相当于访问了TestController中的list()方法
        3. 该方法返回TestService.list()，而这个方法会返回TestMapper.list()
        4. 而TestMapper会映射到对应的TestMapper.xml并根据方法名和id的对应关系选择对应的sql语句
5. **将数据库更改为wikidev**

## 第三天工作：
1. **集成mybatis generator官方代码生成器**：
    * 集成步骤：
        * pom.xml中增加插件依赖
        * 增加配置文件generator-config.xml
        * 增加执行命令，生成持久层代码
    * 测试demo表的查询功能：
        * 生成demo表all.sql
        * 添加DemoController.java，并用@RequestMapping注解提取公共路径
        * 添加DemoService.java使用持久层
        * 添加demo.http测试查询功能
2. **开发电子书列表查询接**：
    * 添加电子书表ebook
    * 使用代码生成器生成持久层代码
    * 添加EbookController.java和EbookService.java并添加ebook.http进行测试
    * 增加通用返回类CommonResp(包括success，message，context)，以后所有端口的返回值类型均为CommonResp
3. **测试电子书模糊查询**：
4. **封装请求参数和返回参数**：
    * 建立一个专门接收返回值的实体类EbookResp.java
    * 建立一个专门用来传参的实体类EbookReq.java
    * BeanUtils工具类的使用：复制对象
5. **制作工具类CopyUtil封装BeanUtils**：
    * 单体对象复制
    * 列表复制
6. **创建Vue CLI项目**：
    * 安装Vue CLI
    * 创建web应用
    * 启动web应用
7. **集成Ant Design Vue**：
    * 集成Ant Design Vue
    * 用Ant Design Vue添加danger按钮
    
## 第五天工作：
1. **网站首页布局开发**：
    * 加入Ant Design Vue布局(解决bug：下载新版组件库，css文件名为reset.css)
    * 首页路由开发：Vue3 router默认加载views文件夹下全部vue文件，views中修改HomeView.vue
    * eslint关闭vue/no-unused-components校验
2. **制作the-header和the-footer组件**：
    * 将header和footer提取成组件
    * 在Vue.app中注册组件并使用
3. **去掉HelloWord组件相关代码**
4. **集成axios**
5. **使用axios调用后端接口/ebook/list**：
    * 在HomeView路由组件中使用setup初始化函数axios访问地址并接受响应，打印在控制台上
    * 解决跨域报错问题
    * 解决警告问题
    
## 第七天工作：
1. **使用setup函数并利用ref和reactive实现数据绑定**
2. **解决报错**：
    * 删除node_modules并npm install重新下载
3. **电子书列表界面展示**：
    * 引用Ant Design Vue中的list组件(需单独下载图标，在指定页面中单独引入注册)
    * 新版的组件库自带ts格式，老版的需要将ts强类型改成弱类型并将eslint中使用any的警告关掉
    * 将单独页面引入图标改成全局集成图标组件
    * 电子书列表界面展示及布局调整：
        * 引入真实数据
        * 引入栅格系统
        * 删除不必要的图片及文字
        * 去除多页展示
        * 封面图标样式调整
    * 电子书列表查询接口支持动态SQL

## 第八天工作：
1. **Vue多环境配置**：
    * 增加dev和prod配置文件，包含环境变量NODE_ENV和自定义参数VUE_APP_XXX
    * 编译和启动增加多环境  
    * 增加–port参数来修改前端启动端口
    * 通过设置axios.defaults.baseURL，来统一设置后端的IP端口或域名
2. **配置axios拦截器打印前端日志**：
    * 打印请求参数和返回参数
3. **增加过滤器，打印接口耗时**：
    * 过滤器依赖于servlet容器
4. **增加Spring拦截器，打印接口耗时**：
    * 拦截器依赖Spring框架
    * 建议能用拦截器的都用拦截器，因为过滤器不用注入其它类，拦截器可注入其它类
5. **增加AOP，打印接口耗时**：
    * 加入AOP和fastjson依赖
    * 定义切点
    * 过滤敏感字段，内容太长的字段
    * 打印请求参数和返回参数
    * 去掉过滤器拦截器

## 第九天工作：
1. **增加电子书管理界面**：
    * 修改组件名字为小写字母开头
    * 修改eslint的检查限制：取消组件驼峰命名法的报错提示
    * 新增电子书管理界面：
        * 新增管理界面
        * 新增路由
        * 修改导航菜单
2. **增加电子书表格展示**：
    * 修改电子书管理界面的布局
    * 添加封面图片及其他数据进数据库
    * 修改eslint的检查限制：取消定义变量不使用的报错提示
    * 使用Ant-design-vue组件完成电子书表格
3. **集成PageHelper插件**：
    * Pom.xml集成PageHelper插件并使用完成后端分页功能
    * 获取分页信息(总条数及总页数)
    * 修改日志等级来打印所有的日志
4. **封装分页请求参数和返回参数**：
    * 封装分页请求参数PageReq和返回参数PageResp
    * 修改相应的Controller和Service
    * 传参测试