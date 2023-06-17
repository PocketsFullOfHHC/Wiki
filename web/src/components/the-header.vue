<!-- 组件取名为the-header，不取名header是为了避免与h5里面的header标签冲突 -->
<template>
    <a-layout-header class="header">
        <div class="logo" />
        <a-menu
                theme="dark"
                mode="horizontal"
                v-model:selectedKeys="selectedKeys1"
                :style="{ lineHeight: '64px' }"
        >
            <a-menu-item key="1">
                nav 1
            </a-menu-item>
            <a-menu-item key="2">
                nav 2
            </a-menu-item>
            <a-menu-item key="3">
                nav 3
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