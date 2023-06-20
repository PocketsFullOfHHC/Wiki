import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
// 引入组件库的图标库
import * as Icons from '@ant-design/icons-vue';
// 全局配置axios请求域(域：即请求地址的前半段：http://127.0.0.1:8880)
import axios from 'axios';
axios.defaults.baseURL = process.env.VUE_APP_SERVER;

const app = createApp(App);
app.use(store).use(router).use(Antd).mount('#app');
// 全局使用图标
const icons: any = Icons;
for (const i in icons) {
  app.component(i, icons[i]);
}

// 测试多环境配置是否成功
// process.env.xxx负责读取环境变量，可以读出配置文件中的名称
console.log('环境：', process.env.NODE_ENV);
console.log('服务端：', process.env.VUE_APP_SERVER);