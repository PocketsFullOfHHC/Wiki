<!-- 组件取名为the-header，不取名header是为了避免与h5里面的header标签冲突 -->
<template>
    <a-layout-footer style="text-align: center">
        HHC电子书，欢迎{{user.name}}
    </a-layout-footer>
</template>
<script lang="ts">
    import { defineComponent, computed } from 'vue';
    import store from "@/store";

    export default defineComponent({
        // 这里的名字可以横杠写，也可以驼峰命名
        name: 'the-footer',
        setup(){
            // 如果一个响应式变量是要根据某个变量的变化而计算得来，可以使用computed
            // 这里面const的user会去实时监听store里面的user的变化
            const user = computed(() => store.state.user)
            return{
                user,
            }
        }
    });
</script>