<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <!-- 增加栅格之间的间隙：建议数值设置为16+8n -->
      <a-row :gutter="24">
        <a-col :span="8">
          <p>
            <!-- 表单绑定param，model为表单数据对象，并在输入框中取到关键字作为param的name值 -->
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
          等待框：分为true或false，如如果为true，则整个表格存在等待效果，defaultExpandAllRows设置表格树默认展开
          defaultExpandAllRows并不是响应式的，因此需要等到数据level1获取成功后才去展示这个表格 -->
          <a-table
                  v-if="level1.length > 0"
                  :columns="columns"
                  :row-key="record => record.id"
                  :data-source="level1"
                  :loading="loading"
                  :pagination="false"
                  size="small"
                  :defaultExpandAllRows="true"
          >
            <!-- 第一个渲染 -->
            <template #name="{ text, record}">
              {{record.sort}} {{text}}
            </template>
            <!-- 第二个渲染：放两个按钮，按钮之间要有空格 -->
            <!-- record指每行的数据：从列表中查询到的数据 -->
            <template #action="{ text, record }">
              <!-- 空格组件 -->
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>
                <a-popconfirm
                        title="删除后不可恢复，确认删除?"
                        ok-text="是"
                        cancel-text="否"
                        @confirm="handleDelete(record.id)"
                >
                  <a-button type="primary" danger size="small">
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <!-- layout="vertical"为纵向的布局 -->
          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="名称"/>
            </a-form-item>
            <a-form-item>
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
            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="顺序"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent()">
                <EyeOutlined /> 内容预览
              </a-button>
            </a-form-item>
            <a-form-item>
              <div id="content"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>
      <!-- 增加抽屉组件，和表单的modal模态框很像，一开始是看不见的 -->
      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>
    </a-layout-content>
  </a-layout>
</template>
<script lang="ts">
  import { defineComponent, onMounted, ref, createVNode } from 'vue';
  import axios from 'axios';
  import { message, Modal } from 'ant-design-vue';
  import {Tool} from "@/util/tool";
  import {useRoute} from "vue-router";
  import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";
  import E from 'wangeditor';
  import i18next from "i18next";

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

      // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
      const treeSelectData = ref();
      treeSelectData.value = [];

      const columns = [
        {
          title: '名称',
          dataIndex: 'name',
          slots: { customRender: 'name' }
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
      // 为了上面.length不报错，这里先初始化
      level1.value = [];

      /**
       * 数据查询
       **/
      const handleQuery = () => {
        loading.value = true;
        // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
        level1.value = [];
        axios.get("/doc/all/" + route.query.ebookId).then((response) => {
          loading.value = false;
          const data = response.data;
          if(data.success){
            docs.value = data.content;
            console.log("原始数组：", docs.value);

            level1.value = [];
            // 第一层递归时父文档的id均为0
            level1.value = Tool.array2Tree(docs.value, 0);
            console.log("树形结构：", level1);

            // 父文档下拉框初始化，相当于点击新增
            treeSelectData.value = Tool.copy(level1.value);
            // 为选择树添加一个"无"
            treeSelectData.value.unshift({id: 0, name: '无'});
          } else{
            message.error(data.message);
          }
        });
      };

      // -------- 表单 ---------
      const doc = ref();
      doc.value = {};
      const editor = new E('#content');
      // 防止下拉框被富文本框遮住
      editor.config.zIndex = 0;

      // 点击保存后的逻辑
      const handleSave = () => {

        // 获取富文本中的内容
        doc.value.content = editor.txt.html();

        // post请求无需像get请求一样传params
        axios.post("/doc/save", doc.value).then((response) => {
          // const data中的data就是CommonResp
          const data = response.data;
          if(data.success){
            message.success("保存成功！");
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
       * 文档内容查询
       **/
      const handleQueryContent = () => {
        axios.get("/doc/find-content/" + doc.value.id).then((response) => {
          const data = response.data;
          if(data.success){
            editor.txt.html(data.content);
          } else{
            message.error(data.message);
          }
        });
      };

      /**
       * 编辑
       */
      const edit = (record: any) => {
        // 清空富文本框，否则点击别处的编辑仍然会显示前面的文档内容
        editor.txt.html("");

        doc.value = Tool.copy(record);
        handleQueryContent();
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
        // 清空富文本框
        editor.txt.html("");
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
        // 清空数组，否则多次删除时，数组会一直增加
        deleteIds.length = 0;
        deleteNames.length = 0;
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

      // ----------------富文本预览--------------
      const drawerVisible = ref(false);
      const previewHtml = ref();
      const handlePreviewContent = () => {
        const html = editor.txt.html();
        previewHtml.value = html;
        drawerVisible.value = true;
      };
      const onDrawerClose = () => {
        drawerVisible.value = false;
      };

      onMounted(() => {
        handleQuery();
        editor.i18next = i18next;
        editor.create();
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
        handleSave,

        handleDelete,

        treeSelectData,

        drawerVisible,
        previewHtml,
        handlePreviewContent,
        onDrawerClose,
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

