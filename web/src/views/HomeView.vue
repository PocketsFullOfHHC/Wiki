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
      <a-list item-layout="vertical" size="large" :pagination="pagination" :data-source="listData">
        <template #footer>
          <div>
            <b>ant design vue</b>
            footer part
          </div>
        </template>
        <template #renderItem="{ item }">
          <a-list-item key="item.title">
            <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component :is="type" style="margin-right: 8px" />
            {{ text }}
          </span>
            </template>
            <template #extra>
              <img
                      width="272"
                      alt="logo"
                      src="https://gw.alipayobjects.com/zos/rmsportal/mqaQswcyDLcXyDKnZfES.png"
              />
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <a :href="item.href">{{ item.title }}</a>
              </template>
              <template #avatar><a-avatar :src="item.avatar" /></template>
            </a-list-item-meta>
            {{ item.content }}
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent,onMounted,ref,reactive,toRef } from 'vue';
import axios from 'axios';

// 需要加上any：因为typescript是强类型，定义变量必须指定类型，js是弱类型，同一个变量可以放任何东西，因此ts往里面放别的东西会报错
// const listData: String就只能放字符串了，这里用any又把他变成了弱类型，但是会报警告，去eslint中配置警告
// const listData: any = [];
const listData: Record<string, string>[] = [];

for (let i = 0; i < 23; i++) {
  listData.push({
    href: 'https://www.antdv.com/',
    title: `ant design vue part ${i}`,
    avatar: 'https://joeschmoe.io/api/v1/random',
    description:
            'Ant Design, a design language for background applications, is refined by Ant UED Team.',
    content:
            'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
  });
}

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
    const pagination = {
      onChange: (page: number) => {
        console.log(page);
      },
      pageSize: 3,
    };
    const actions: Record<string, string>[] = [
      { type: 'StarOutlined', text: '156' },
      { type: 'LikeOutlined', text: '156' },
      { type: 'MessageOutlined', text: '2' },
    ];
    return{
      // 前端需要拿到这个数据，因此要在setup后返回这个值，类似data
      ebooks,
      // 将其转化为响应式数据
      ebooks2: toRef(ebooks1, "books"),
      listData,
      pagination,
      actions,
    }
  }
});
</script>
