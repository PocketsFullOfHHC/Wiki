<!-- 组件取名为the-header，不取名header是为了避免与h5里面的header标签冲突 -->
<template>
    <a-layout-header class="header">
        <div class="logo" />
        <a-menu
                theme="dark"
                mode="horizontal"
                :style="{ lineHeight: '64px' }"
        >
            <a-menu-item key="/">
                <router-link to="/">首页</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/user">
              <router-link to="/admin/user">用户管理</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/ebook">
                <router-link to="/admin/ebook">电子书管理</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/category">
                <router-link to="/admin/category">分类管理</router-link>
            </a-menu-item>
            <a-menu-item key="/about">
                <router-link to="/about">关于我们</router-link>
            </a-menu-item>
        </a-menu>
    </a-layout-header>
</template>
<script lang="ts">
    import { defineComponent } from 'vue';

    export default defineComponent({
        // 这里的名字可以横杠写，也可以驼峰命名
        name: 'the-header',
    });
</script>