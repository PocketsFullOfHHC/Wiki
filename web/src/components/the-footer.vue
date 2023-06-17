<!-- 组件取名为the-header，不取名header是为了避免与h5里面的header标签冲突 -->
<template>
    <a-layout-footer style="text-align: center">
        HHC电子书
    </a-layout-footer>
</template>
<script lang="ts">
    import { defineComponent } from 'vue';

    export default defineComponent({
        // 这里的名字可以横杠写，也可以驼峰命名
        name: 'the-footer',
    });
</script>