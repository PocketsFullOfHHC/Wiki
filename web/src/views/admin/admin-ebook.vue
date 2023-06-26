<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <p>
        <a-button type="primary" @click="add()" size="large">
          新增
        </a-button>
      </p>
      <!-- 定义table里面的各种属性：列；每一行都要给一个key(row_key)，这里直接使用查询到数据的id；数据源；分页；
      等待框：分为true或false，如如果为true，则整个表格存在等待效果；执行点击分页的方法 -->
      <a-table
              :columns="columns"
              :row-key="record => record.id"
              :data-source="ebooks"
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
          title="电子书表单"
          v-model:visible="modalVisible"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
  >
    <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="分类一">
        <a-input v-model:value="ebook.category1Id" />
      </a-form-item>
      <a-form-item label="分类二">
        <a-input v-model:value="ebook.category2Id" />
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" type="textarea" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';

  export default defineComponent({
    name: 'AdminEbook',
    setup() {
      const ebooks = ref();
      // 引入分页组件，分页组件内部内置了一些属性：当前页数，每页条数，数据项总数
      const pagination = ref({
        current: 1,
        pageSize: 4,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: '封面',
          dataIndex: 'cover',
          // 封面给了一个插槽渲染，值为cover
          slots: { customRender: 'cover' }
        },
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '分类一',
          key: 'category1Id',
          dataIndex: 'category1Id'
        },
        {
          title: '分类二',
          dataIndex: 'category2Id'
        },
        {
          title: '文档数',
          dataIndex: 'docCount'
        },
        {
          title: '阅读数',
          dataIndex: 'viewCount'
        },
        {
          title: '点赞数',
          dataIndex: 'voteCount'
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
        axios.get("/ebook/list", {
          params: {
            page: params.page,
            size: params.size,
          }
        }).then((response) => {
          loading.value = false;
          const data = response.data;
          ebooks.value = data.content.list;

          // 修改分页组件属性：改变页码按钮的激活状态及分页的页数
          pagination.value.current = params.page;
          pagination.value.total = data.content.total;
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
      const ebook = ref({});
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      // 点击OK后的逻辑
      const handleModalOk = () => {
        modalLoading.value = true;
        // post请求无需像get请求一样传params
        axios.post("/ebook/save", ebook.value).then((response) => {
          // const data中的data就是CommonResp
          const data = response.data;
          if(data.success){
            modalVisible.value = false;
            modalLoading.value = false;

            // 编辑后重新加载列表
            handleQuery({
              // 重新定位当前页
              page: pagination.value.current,
              size: pagination.value.pageSize
            });
          }
        });
      };

      /**
       * 编辑
       */
      const edit = (record: any) => {
        modalVisible.value = true;
        // 将定义的响应式变量赋值，使其在表单中显示
        ebook.value = record;
      };

      /**
       * 新增
       */
      const add = () => {
        modalVisible.value = true;
        // 新增时的表单项应该是空的，这里清空
        ebook.value = {};
      };

      /**
       * 删除
       */
      // Long类型对应的前端类型是number
      const handleDelete = (id: number) => {
        axios.delete("/ebook/delete/" + id).then((response) => {
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
        ebooks,
        pagination,
        columns,
        loading,
        handleTableChange,

        edit,
        add,

        ebook,
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

