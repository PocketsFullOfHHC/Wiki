<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
              mode="inline"
              v-model:openKeys="openKeys"
              :style="{ height: '100%', borderRight: 0 }"
              @click="handleClick"
      >
        <a-menu-item key="welcome">
            <MailOutlined />
            <span>欢迎</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <!-- 一级菜单 -->
          <template v-slot:title>
            <span><user-outlined />{{item.name}}</span>
          </template>
          <!-- 二级菜单 -->
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MailOutlined /><span>{{child.name}}</span>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div class="welcome" v-show="isShowWelcome">
        <h>欢迎使用HHC知识库</h>
      </div>
      <!-- :grid = "{gutter: 20, column: 3}"：每行三列，列与列之间间隔为20px -->
      <a-list v-show="!isShowWelcome" item-layout="vertical" size="large" :grid = "{gutter: 20, column: 3}"  :data-source="ebooks">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
              <span v-for="{ type, text } in actions" :key="type">
                <component :is="type" style="margin-right: 8px" />
                {{ text }}
              </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/doc?ebookId=' + item.id">
                  {{ item.name }}
                </router-link>
              </template>
              <template #avatar><a-avatar :src="item.cover" /></template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent,onMounted,ref} from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import {Tool} from "@/util/tool";

export default defineComponent({
  name: 'Home',

  // vue3新增的初始化方法，是这个组件加载完成后初始会去执行的方法
  // vue3将vue2中的data，method，以及mounted等生命周期函数打包成setup
  setup(){
    // vue3新增ref为定义响应式数据，即在js中动态修改这里面的值，需要实时反馈到界面上
    const ebooks = ref();
    const level1 =  ref();
    let categorys: any;
    /**
     * 查询所有分类
     **/
    const handleQueryCategory = () => {
      axios.get("/category/all").then((response) => {
        const data = response.data;
        if (data.success) {
          categorys = data.content;
          console.log("原始数组：", categorys);

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构：", level1.value);
        } else {
          message.error(data.message);
        }
      });
    };

    // ref中传的值为默认值
    const isShowWelcome = ref(true);
    let categoryId2 = 0;


    const handleQueryEbook = () => {
      axios.get("/ebook/list", {
        params: {
          page: 1,
          size: 1000,
          categoryId2: categoryId2,
        }
      }).then((response) => {
        const data = response.data;
        // ref的对应赋值方法
        ebooks.value = data.content.list;
      })
    };

    // value是click事件(handleClick只是函数名，可以随意该，click才是真正的事件名)自带的参数，可以打印出来看看是什么(也可以查看组件的API文档)
    const handleClick = (value: any) => {
      // console.log("menu click", value);
      if(value.key === 'welcome'){
        isShowWelcome.value = true;
      } else{
        categoryId2 = value.key;
        isShowWelcome.value = false;
        handleQueryEbook();
      }
      // 简写
      // isShowWelcome.value = value.key === 'welcome';
    };

    // 一般把界面的的初始化逻辑都写到onMounted中，不建议直接写到setup()中
    onMounted(() => {
      handleQueryCategory();
      // 返回值为响应内容，用response接收
      // 这里可以配置多环境配置：写在.env.dev和.env.prod中并在package.json中修改命令
    });
    const actions: Record<string, string>[] = [
      { type: 'StarOutlined', text: '156' },
      { type: 'LikeOutlined', text: '156' },
      { type: 'MessageOutlined', text: '2' },
    ];
    return{
      // 前端需要拿到这个数据，因此要在setup后返回这个值，类似data
      ebooks,
      actions,
      handleClick,
      level1,

      isShowWelcome,
    }
  }
});
</script>

<!-- 用f12找到后可以在网页中调整，调整正确后再复制到这里 -->
<!-- scoped表示增加的页面只在当前这个HomeView中生效 -->
<style scoped>
  .ant-avatar {
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
  }
</style>