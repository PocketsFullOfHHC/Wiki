<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <p>
        <!-- 表单绑定param，并在输入框中取到关键字作为param的name值 -->
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="名称">
            </a-input>
          </a-form-item>
          <a-form-item>
            <!-- pagination这种响应式变量在html中用就不需要.value了 -->
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
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
      <!-- 定义table里面的各种属性：列；每一行都要给一个key(row_key)，这里直接使用查询到数据的id；数据源；分页；
      等待框：分为true或false，如如果为true，则整个表格存在等待效果；执行点击分页的方法 -->
      <a-table
              :columns="columns"
              :row-key="record => record.id"
              :data-source="categorys"
              :pagination="pagination"
              :loading="loading"
              @change="handleTableChange"
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
      <a-form-item label="父分类">
        <a-input v-model:value="category.parent" />
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
      // 引入分页组件，分页组件内部内置了一些属性：当前页数，每页条数，数据项总数
      const pagination = ref({
        current: 1,
        pageSize: 4,
        total: 0
      });
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
       * 数据查询
       **/
      const handleQuery = (params: any) => {
        loading.value = true;
        axios.get("/category/list", {
          params: {
            page: params.page,
            size: params.size,
            name: param.value.name,
          }
        }).then((response) => {
          loading.value = false;
          const data = response.data;
          if(data.success){
            categorys.value = data.content.list;
            // 修改分页组件属性：改变页码按钮的激活状态及分页的页数
            pagination.value.current = params.page;
            pagination.value.total = data.content.total;
          } else{
            message.error(data.message);
          }
        });
      };

      /**
       * 表格点击页码时触发
       */
      const handleTableChange = (pagination: any) => {
        console.log("看看自带的分页参数都有啥：" + pagination);
        handleQuery({
          page: pagination.current,
          size: pagination.pageSize
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
            handleQuery({
              // 重新定位当前页
              page: pagination.value.current,
              size: pagination.value.pageSize
            });
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
        // 将定义的响应式变量赋值，使其在表单中显示
        // 原版是直接将列表查询出来的值record直接赋值给响应式变量category，直接更改record相当于随时在更改响应式变量category，
        // 应该把查询出来的值复制成一个新的对象变量，赋值给category
        // 这样修改旧变量时就不会影响category响应式变化了，只有当保存后更改数据库重新加载列表才会更改
        // category.value = record;
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
            handleQuery({
              // 重新定位当前页
              page: pagination.value.current,
              size: pagination.value.pageSize
            });
          }
        });
      };

      onMounted(() => {
        // 传参
        handleQuery({
          page: 1,
          // 使用响应式变量pagination必须加上value
          size: pagination.value.pageSize
        });
      });

      return {
        param,
        categorys,
        pagination,
        columns,
        loading,
        handleTableChange,
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

