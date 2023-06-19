<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
              mode="inline"
              v-model:openKeys="openKeys"
              :style="{ height: '100%', borderRight: 0 }"
      >
        <a-sub-menu key="sub1">
          <template #title>
            <span><user-outlined />subnav 1</span>
          </template>
          <a-menu-item key="1">option1</a-menu-item>
          <a-menu-item key="2">option2</a-menu-item>
          <a-menu-item key="3">option3</a-menu-item>
          <a-menu-item key="4">option4</a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="sub2">
          <template #title>
            <span><laptop-outlined />subnav 2</span>
          </template>
          <a-menu-item key="5">option5</a-menu-item>
          <a-menu-item key="6">option6</a-menu-item>
          <a-menu-item key="7">option7</a-menu-item>
          <a-menu-item key="8">option8</a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="sub3">
          <template #title>
            <span><notification-outlined />subnav 3</span>
          </template>
          <a-menu-item key="9">option9</a-menu-item>
          <a-menu-item key="10">option10</a-menu-item>
          <a-menu-item key="11">option11</a-menu-item>
          <a-menu-item key="12">option12</a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <pre>
        <!-- 直接打印会变成一个json字符串(由数组包含)，用pre标签，表示预定义格式文本。在该元素中的文本通常按照原文件中的编排，以等宽字体的形式展现出来，文本中的空白符（比如空格和换行符）都会显示出来 -->
{{ebooks}}
{{ebooks2}}
      </pre>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent,onMounted,ref,reactive,toRef } from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'HomeView',
  // vue3新增的初始化方法，是这个组件加载完成后初始会去执行的方法
  // vue3将vue2中的data，method，以及mounted等生命周期函数打包成setup
  setup(){
    console.log("setup");
    // vue3新增ref为定义响应式数据，即在js中动态修改这里面的值，需要实时反馈到界面上
    const ebooks = ref();
    // reactive中需要放置一个对象，在该对象中添加属性books属性来放response返回值并将该对象赋值给ebooks
    const ebooks1 = reactive({books:[]});
    // 一般把界面的的初始化逻辑都写到onMounted中，不建议直接写到setup()中
    onMounted(() => {
      console.log("onMounted");
      // 返回值为响应内容，用response接收
      axios.get("http://localhost:8880/ebook/list?name=Spring").then((response) => {
        const data = response.data;
        // ref的对应赋值方法
        ebooks.value = data.content;
        ebooks1.books = data.content;
        console.log(response);
      })
    });
    return{
      // 前端需要拿到这个数据，因此要在setup后返回这个值，类似data
      ebooks,
      // 将其转化为响应式数据
      ebooks2: toRef(ebooks1, "books")
    }
  }
});
</script>
