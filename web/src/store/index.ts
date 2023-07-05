import { createStore } from 'vuex'

declare let SessionStorage: any;
const USER = "USER";

const store =  createStore({
  // 定义变量
  state: {
    // 去redis缓存中寻找Key为USER的数据，如果找不到赋值为空对象，避免空指针异常
    user:SessionStorage.get(USER) || {}
  },
  getters: {
  },
  // 同步操作
  mutations: {
    // 将外部登录成功后传进来的user赋值给内部定义的user
    setUser(state, user){
      state.user = user;
      // 登录成功后将其缓存为名为USER的数据
      SessionStorage.set(USER, user);
    }
  },
  // 异步操作
  actions: {
  },
  modules: {
  }
});

export default store;