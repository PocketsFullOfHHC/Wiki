<template>
  <a-layout id="components-layout-demo-top-side-2">
    <the-header></the-header>
    <router-view/>
    <the-footer></the-footer>
  </a-layout>
</template>
<script lang="ts">
  import { defineComponent } from 'vue';
  // 这里import的组件必须得驼峰命名
  import TheHeader from '@/components/the-header.vue';
  import TheFooter from '@/components/the-footer.vue';

  export default defineComponent({
    name: 'app',
    // 注册该组件中所需的组件
    components:{
      TheHeader,
      TheFooter,
    },
  });
</script>

