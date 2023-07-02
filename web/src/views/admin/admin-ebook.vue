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
        <!-- 第二个渲染：将分类渲染到界面上，如果是不带具体字段的渲染，text和record是一致的，均为渲染的整条数据，
        如果是带具体字段的渲染，如上面封面的渲染，它的text就是具体的封面的这个字段 -->
        <template v-slot:category="{ text, record }">
          <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
        </template>
        <!-- 第三个渲染：放两个按钮，按钮之间要有空格 -->
        <!-- record指每行的数据：从列表中查询到的数据 -->
        <template #action="{ text, record }">
          <!-- 空格组件 -->
          <a-space size="small">
            <router-link to="doc" >
              <a-button type="primary">
                文档管理
              </a-button>
            </router-link>
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
      <a-form-item label="分类">
        <!-- 级联下拉框 -->
        <!-- value值为该级联选择组件中所选择的多个值(这里只有两个分类)所组成的数组，categoryIds为新定义的响应式变量 -->
        <!-- 下拉框一般会有显示的值和实际取的值，级联下拉框也不例外，label是显示的值，这里显示的是name；value是实际取的值，这里是id -->
        <!-- children是级联的子属性 -->
        <!-- field-name里面的id，name，children均为下面绑定的level1的属性 -->
        <a-cascader
                v-model:value="categoryIds"
                :field-names="{ label: 'name', value: 'id', children: 'children' }"
                :options="level1"
        />
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
  import { message } from 'ant-design-vue';
  import {Tool} from "@/util/tool";

  export default defineComponent({
    name: 'AdminEbook',
    setup() {
      const param = ref();
      // 初始化为一个空对象，不加会报错
      param.value = {};
      // const ebooks = ref({});是一开始就初始化空对象，可以避免空指针异常
      // const ebooks = ref();是不初始化，容易出现空指针异常
      // 写个{}，就说明是个空对象，里面什么属性都没有，结果代码里有去为category1Id赋值，所以报没有这个属性。
      // 不写{}，就相当于只是定义了这么个变量，具体是什么值还没定，由运行时代码来决定。
      // 写个{}，就说明是个空对象，里面什么属性都没有，结果代码里有去为category1Id赋值，所以报没有这个属性。不写{}，就相当于只是定义了这么个变量，具体是什么值还没定，由运行时代码来决定。
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
          title: '分类',
          // 给一个渲染，对应上面名为category的渲染，会自动带上上面的两个参数text和record
          slots: { customRender: 'category' }
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
            name: param.value.name,
          }
        }).then((response) => {
          loading.value = false;
          const data = response.data;
          if(data.success){
            ebooks.value = data.content.list;
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
      /**
       * 数组，[100, 101]对应：前端开发 / Vue
       */
      const categoryIds = ref();
      const ebook = ref();
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      // 点击OK后的逻辑
      const handleModalOk = () => {
        modalLoading.value = true;
        ebook.value.category1Id = categoryIds.value[0];
        ebook.value.category2Id = categoryIds.value[1];
        // post请求无需像get请求一样传params
        axios.post("/ebook/save", ebook.value).then((response) => {
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
        // 原版是直接将列表查询出来的值record直接赋值给响应式变量ebook，直接更改record相当于随时在更改响应式变量ebook，
        // 应该把查询出来的值复制成一个新的对象变量，赋值给ebook
        // 这样修改旧变量时就不会影响ebook响应式变化了，只有当保存后更改数据库重新加载列表才会更改
        // ebook.value = record;
        ebook.value = Tool.copy(record);
        categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id]
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

      const level1 =  ref();
      // 定义全局非响应式变量(不在html中显示，只在js中计算应用)
      let categorys: any;
      /**
       * 查询所有分类
       **/
      const handleQueryCategory = () => {
        loading.value = true;
        // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
        ebooks.value = [];
        axios.get("/category/all").then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            categorys = data.content;
            console.log("原始数组：", categorys);

            level1.value = [];
            level1.value = Tool.array2Tree(categorys, 0);
            console.log("树形结构：", level1.value);

            // 加载完分类后，再加载电子书，否则如果分类树加载很慢，则电子书渲染会报错
            handleQuery({
              page: 1,
              size: pagination.value.pageSize,
            });
          } else {
            message.error(data.message);
          }
        });
      };

      /**
       * 获取指定分类id对应的分类名
       * */
      const getCategoryName = (cid: number) => {
          // console.log(cid)
          let result = "";
          categorys.forEach((item: any) => {
              if (item.id === cid) {
                  // return item.name; // 注意，这里直接return不起作用
                  result = item.name;
                }
            });
          return result;
        };

      onMounted(() => {
        handleQueryCategory();
      });

      return {
        param,
        ebooks,
        pagination,
        columns,
        loading,
        handleTableChange,
        handleQuery,
        getCategoryName,

        edit,
        add,

        ebook,
        modalVisible,
        modalLoading,
        handleModalOk,
        categoryIds,
        level1,

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

