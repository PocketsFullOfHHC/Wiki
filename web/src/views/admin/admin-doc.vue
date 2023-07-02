<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <p>
        <!-- 表单绑定param，并在输入框中取到关键字作为param的name值 -->
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-button type="primary" @click="handleQuery()">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <!-- 定义table里面的各种属性：列；每一行都要给一个key(row_key)，这里直接使用查询到数据的id；数据源；
      等待框：分为true或false，如如果为true，则整个表格存在等待效果 -->
      <a-table
              :columns="columns"
              :row-key="record => record.id"
              :data-source="level1"
              :loading="loading"
              :pagination="false"
      >
        <!-- #是插槽的简写方式：#cover指定作用域 -->
        <!-- 第一个渲染：渲染封面到界面上：如果cover项不空，就用src渲染上去 -->
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <!-- 第二个渲染：放两个按钮，按钮之间要有空格 -->
        <!-- record指每行的数据：从列表中查询到的数据 -->
        <template #action="{ text, record }">
          <!-- 空格组件 -->
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                    title="删除后不可恢复，确认删除?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDelete(record.id)"
            >
                <a-button type="primary" danger>
                  删除
                </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal
          title="文档表单"
          v-model:visible="modalVisible"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
  >
    <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="doc.name" />
      </a-form-item>
      <!-- 修改父文档为下拉框 -->
      <a-form-item label="父文档">
        <!-- 设置最高下拉高度400px，设置树形结构的json数据：treeSelectData，
        不用level1的原因：我们需要下拉框的数据中存在无等等选择，但level1的数据需要渲染到表格上，这样表格上就会存在“无”这一项了
        replaceFields为属性转化(新版本中为fieldNames，且title更改为label)：因为该组件要的数据是title，value和key，而我们给的是id和name，需要属性转化一下 -->
        <a-tree-select
                v-model:value="doc.parent"
                style="width: 100%"
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                :tree-data="treeSelectData"
                placeholder="请选择父文档"
                tree-default-expand-all
                :fieldNames="{label: 'name', value: 'id'}"
        >
        </a-tree-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts">
  import { defineComponent, onMounted, ref, createVNode } from 'vue';
  import axios from 'axios';
  import { message, Modal } from 'ant-design-vue';
  import {Tool} from "@/util/tool";
  import {useRoute} from "vue-router";
  import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";

  export default defineComponent({
    name: 'AdminDoc',
    setup() {
      // useRoute()是路由内置的函数，通过该函数可以得到路由的各种信息
      const route = useRoute();
      console.log("路由：", route);
      console.log("route.path：", route.path);
      console.log("route.query：", route.query);
      console.log("route.param：", route.params);
      // 全路径：包括路径和参数
      console.log("route.fullPath：", route.fullPath);
      console.log("route.name：", route.name);
      console.log("route.meta：", route.meta);
      const param = ref();
      // 初始化为一个空对象，不加会报错
      param.value = {};
      const docs = ref();
      const loading = ref(false);

      const columns = [
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '父文档',
          key: 'parent',
          dataIndex: 'parent'
        },
        {
          title: '顺序',
          dataIndex: 'sort'
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];

      /**
       * 一级文档树，children属性就是二级文档
       * [{
       *   id: "",
       *   name: "",
       *   children: [{
       *     id: "",
       *     name: "",
       *   }]
       * }]
       */
      const level1 = ref(); // 一级文档树，children属性就是二级文档

      /**
       * 数据查询
       **/
      const handleQuery = () => {
        loading.value = true;
        // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
        level1.value = [];
        axios.get("/doc/all").then((response) => {
          loading.value = false;
          const data = response.data;
          if(data.success){
            docs.value = data.content;
            console.log("原始数组：", docs.value);

            level1.value = [];
            // 第一层递归时父文档的id均为0
            level1.value = Tool.array2Tree(docs.value, 0);
            console.log("树形结构：", level1);
          } else{
            message.error(data.message);
          }
        });
      };

      // -------- 表单 ---------
      // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
      const treeSelectData = ref();
      treeSelectData.value = [];
      const doc = ref({});
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      // 点击OK后的逻辑
      const handleModalOk = () => {
        modalLoading.value = true;
        // post请求无需像get请求一样传params
        axios.post("/doc/save", doc.value).then((response) => {
          // 只要后端有返回就需要把loading效果去掉
          modalLoading.value = false;
          // const data中的data就是CommonResp
          const data = response.data;
          if(data.success){
            modalVisible.value = false;
            // 编辑后重新加载列表
            handleQuery();
          } else{
            message.error(data.message);
          }
        });
      };

      /**
       * 将某节点及其子孙节点全部置为disabled
       */
      const setDisable = (treeSelectData: any, id: any) => {
        // console.log(treeSelectData, id);
        // 遍历数组，即遍历某一层节点
        for (let i = 0; i < treeSelectData.length; i++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // 如果当前节点就是目标节点
            console.log("disabled", node);
            // 将目标节点设置为disabled
            node.disabled = true;

            // 遍历所有子节点，将所有子节点全部都加上disabled
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j++) {
                setDisable(children, children[j].id)
              }
            }
          } else {
            // 如果当前节点不是目标节点，则到其子节点再找找看。
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              setDisable(children, id);
            }
          }
        }
      };

      const deleteIds: Array<string> = [];
      const deleteNames: Array<string> = [];
      /**
       * 查找要删除的整根树枝
       */
      const getDeleteIds = (treeSelectData: any, id: any) => {
        // console.log(treeSelectData, id);
        // 遍历数组，即遍历某一层节点
        for (let i = 0; i < treeSelectData.length; i++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // 如果当前节点就是目标节点
            console.log("disabled", node);
            // 将目标id放入结果集ids中
            deleteIds.push(id);
            deleteNames.push(node.name);

            // 遍历所有子节点
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j++) {
                getDeleteIds(children, children[j].id)
              }
            }
          } else {
            // 如果当前节点不是目标节点，则到其子节点再找找看。
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              getDeleteIds(children, id);
            }
          }
        }
      };

      /**
       * 编辑
       */
      const edit = (record: any) => {
        modalVisible.value = true;
        doc.value = Tool.copy(record);
        // 复制level1的数据到树形下拉框的数据中
        treeSelectData.value = Tool.copy(level1.value);
        // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
        setDisable(treeSelectData.value, record.id);

        // 在选择树前面添加一个"无"
        treeSelectData.value.unshift({id: 0, name: '无'});
      };

      /**
       * 新增
       */
      const add = () => {
        modalVisible.value = true;
        // 新增时的表单项应该是空的，这里清空
        doc.value = {
          ebookId: route.query.ebookId
        };

        treeSelectData.value = Tool.copy(level1.value);

        // 为选择树添加一个"无"
        treeSelectData.value.unshift({id: 0, name: '无'});
      };

      /**
       * 删除
       */
      // Long类型对应的前端类型是number
      const handleDelete = (id: number) => {
        getDeleteIds(level1.value, id);
        Modal.confirm({
          title: '重要提醒',
          icon: createVNode(ExclamationCircleOutlined),
          content: '将删除：【' + deleteNames.join("，") + "】删除后不可恢复，确认删除？",
          onOk() {
            // console.log(ids)
            axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
              const data = response.data; // data = commonResp
              if (data.success) {
                // 重新加载列表
                handleQuery();
              }
            })
          }
        });
      };

      onMounted(() => {
        // 传参
        handleQuery();
      });

      return {
        param,
        // docs,
        level1,
        columns,
        loading,
        handleQuery,

        edit,
        add,

        doc,
        modalVisible,
        modalLoading,
        handleModalOk,

        handleDelete,

        treeSelectData,
      }
    }
  });
</script>

<style scoped>
  img {
    width: 50px;
    height: 50px;
  }
</style>
