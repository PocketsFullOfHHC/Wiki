<!-- 组件取名为the-header，不取名header是为了避免与h5里面的header标签冲突 -->
<template>
    <a-layout-footer style="text-align: center">
        HHC电子书<span v-show="user.id">，欢迎：{{user.name}}</span>
    </a-layout-footer>
</template>
<script lang="ts">
    import { defineComponent, computed, onMounted} from 'vue';
    import store from "@/store";
    import {Tool} from "@/util/tool";

    export default defineComponent({
        // 这里的名字可以横杠写，也可以驼峰命名
        name: 'the-footer',
        setup(){
            // 如果一个响应式变量是要根据某个变量的变化而计算得来，可以使用computed
            // 这里面const的user会去实时监听store里面的user的变化
            const user = computed(() => store.state.user)

            let websocket: any;
            let token: any;
            const onOpen = () => {
                console.log('WebSocket连接成功，状态码：', websocket.readyState)
            };
            const onMessage = (event: any) => {
                console.log('WebSocket收到消息：', event.data);
            };
            const onError = () => {
                console.log('WebSocket连接错误，状态码：', websocket.readyState)
            };
            const onClose = () => {
                console.log('WebSocket连接关闭，状态码：', websocket.readyState)
            };
            const initWebSocket = () => {
                // 连接成功
                websocket.onopen = onOpen;
                // 收到消息的回调
                websocket.onmessage = onMessage;
                // 连接错误
                websocket.onerror = onError;
                // 连接关闭的回调
                websocket.onclose = onClose;
            };

            onMounted(() => {
                // WebSocket
                if ('WebSocket' in window) {
                    token = Tool.uuid(10);
                    // 连接地址：ws://127.0.0.1:8880/ws/xxx
                    websocket = new WebSocket(process.env.VUE_APP_WS_SERVER + '/ws/' + token);
                    initWebSocket()

                    // 关闭
                    // websocket.close();
                } else {
                    alert('当前浏览器 不支持')
                }
            });

            return{
                user,
            }
        }
    });
</script>