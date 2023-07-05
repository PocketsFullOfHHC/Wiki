<!-- 组件取名为the-header，不取名header是为了避免与h5里面的header标签冲突 -->
<template>
    <a-layout-header class="header">
        <div class="logo" />
        <a-popconfirm
                title="确认退出登录?"
                ok-text="是"
                cancel-text="否"
                @confirm="logout()"
        >
            <a class="login-menu" v-show="user.id">
                <span>退出登录</span>
            </a>
        </a-popconfirm>
        <a class="login-menu" v-show="user.id">
            <span>您好：{{user.name}}</span>
        </a>
        <a class="login-menu" v-show="!user.id" @click="showLoginModal">
            <span>登录</span>
        </a>
        <a-menu
                theme="dark"
                mode="horizontal"
                :style="{ lineHeight: '64px' }"
        >
            <a-menu-item key="/">
                <router-link to="/">首页</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/user" v-if="user.id">
              <router-link to="/admin/user">用户管理</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/ebook" v-if="user.id">
                <router-link to="/admin/ebook">电子书管理</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/category" v-if="user.id">
                <router-link to="/admin/category">分类管理</router-link>
            </a-menu-item>
            <a-menu-item key="/about">
                <router-link to="/about">关于我们</router-link>
            </a-menu-item>
        </a-menu>

        <a-modal
                title="登录"
                v-model:visible="loginModalVisible"
                :confirm-loading="loginModalLoading"
                @ok="login"
        >
            <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
                <a-form-item label="登录名">
                    <a-input v-model:value="loginUser.loginName" />
                </a-form-item>
                <a-form-item label="密码">
                    <a-input v-model:value="loginUser.password" type="password" />
                </a-form-item>
            </a-form>
        </a-modal>
    </a-layout-header>
</template>
<script lang="ts">
    import {computed, defineComponent, ref} from 'vue';
    import axios from 'axios';
    import { message } from 'ant-design-vue';
    import store from "@/store";

    declare let hexMd5: any;
    declare let KEY: any;

    export default defineComponent({
        // 这里的名字可以横杠写，也可以驼峰命名
        name: 'the-header',
        setup () {
            // 登录后显示user信息
            const user = computed(() => store.state.user);

            // 用来登录
            const loginUser = ref({
                loginName: "test",
                password: "test123"
            });
            const loginModalVisible = ref(false);
            const loginModalLoading = ref(false);
            const showLoginModal = () => {
                loginModalVisible.value = true;
            };

            // 登录
            const login = () => {
                console.log("开始登录");
                loginModalLoading.value = true;
                loginUser.value.password = hexMd5(loginUser.value.password + KEY);
                axios.post('/user/login', loginUser.value).then((response) => {
                    loginModalLoading.value = false;
                    const data = response.data;
                    if (data.success) {
                        loginModalVisible.value = false;
                        message.success("登录成功！");
                        // 用commit触发vuex的store中的mutations里面的方法，给store里面的user赋值
                        store.commit("setUser", data.content);
                    } else {
                        message.error(data.message);
                    }
                });
            };

            // 退出登录
            const logout = () => {
                console.log("退出登录开始");
                axios.get('/user/logout/' + user.value.token).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        message.success("退出登录成功！");
                        // 将vuex中的user值改成空对象避免空指针异常
                        // 因为vuex里面集成了SessionStorage，因此这里的操作也会将缓存里的USER清空
                        store.commit("setUser", {});
                    } else {
                        message.error(data.message);
                    }
                });
            };

            return {
                loginModalVisible,
                loginModalLoading,
                showLoginModal,
                loginUser,
                login,
                user,
                logout,
            }
        }
    });
</script>
<style>
    .login-menu {
        float: right;
        color: white;
        padding-left: 10px;
    }
</style>