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
          title="分类表单"
          v-model:visible="modalVisible"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
  >
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="名称">
        <a-input v-model:value="category.name" />
      </a-form-item>
      <!-- 修改父分类为下拉框 -->
      <a-form-item label="父分类">
        <a-select
                v-model:value="category.parent"
                ref="select"
        >
          <a-select-option value="0">
            无
          </a-select-option>
          <!-- 只有两级，因此父分类只需循环level1即可 -->
          <!-- :disabled设置不能让自己选择自己的id作为父分类，否则会断枝 -->
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id">
            {{c.name}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';
  import { message } from 'ant-design-vue';
  import {Tool} from "@/util/tool";

  export default defineComponent({
    name: 'AdminCategory',
    setup() {
      const param = ref();
      // 初始化为一个空对象，不加会报错
      param.value = {};
      const categorys = ref();
      const loading = ref(false);

      const columns = [
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '父分类',
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
       * 一级分类树，children属性就是二级分类
       * [{
       *   id: "",
       *   name: "",
       *   children: [{
       *     id: "",
       *     name: "",
       *   }]
       * }]
       */
      const level1 = ref(); // 一级分类树，children属性就是二级分类

      /**
       * 数据查询
       **/
      const handleQuery = () => {
        loading.value = true;
        // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
        level1.value = [];
        axios.get("/category/all").then((response) => {
          loading.value = false;
          const data = response.data;
          if(data.success){
            categorys.value = data.content;
            console.log("原始数组：", categorys.value);

            level1.value = [];
            // 第一层递归时父分类的id均为0
            level1.value = Tool.array2Tree(categorys.value, 0);
            console.log("树形结构：", level1);
          } else{
            message.error(data.message);
          }
        });
      };

      // -------- 表单 ---------
      const category = ref({});
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      // 点击OK后的逻辑
      const handleModalOk = () => {
        modalLoading.value = true;
        // post请求无需像get请求一样传params
        axios.post("/category/save", category.value).then((response) => {
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
       * 编辑
       */
      const edit = (record: any) => {
        modalVisible.value = true;
        category.value = Tool.copy(record);
      };

      /**
       * 新增
       */
      const add = () => {
        modalVisible.value = true;
        // 新增时的表单项应该是空的，这里清空
        category.value = {};
      };

      /**
       * 删除
       */
      // Long类型对应的前端类型是number
      const handleDelete = (id: number) => {
        axios.delete("/category/delete/" + id).then((response) => {
          // const data中的data就是CommonResp
          const data = response.data;
          if(data.success){
            // 删除成功后重新加载列表
            handleQuery();
          }
        });
      };

      onMounted(() => {
        // 传参
        handleQuery();
      });

      return {
        param,
        // categorys,
        level1,
        columns,
        loading,
        handleQuery,

        edit,
        add,

        category,
        modalVisible,
        modalLoading,
        handleModalOk,

        handleDelete
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

