import { createStore } from 'vuex'

const store =  createStore({
  // 定义变量
  state: {
    user:{}
  },
  getters: {
  },
  // 同步操作
  mutations: {
    // 将外部登录成功后传进来的user赋值给内部定义的user
    setUser(state, user){
      state.user = user;
    }
  },
  // 异步操作
  actions: {
  },
  modules: {
  }
});

export default store;