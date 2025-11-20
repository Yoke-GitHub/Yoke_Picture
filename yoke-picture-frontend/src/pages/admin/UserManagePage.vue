<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="请输入账号" allow-clear />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="请输入用户名" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit"> 搜索</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 10px"></div>
    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" width="40px" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-if="record.userRole === 'user'">
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button v-if="record.userRole === 'user'" danger @click="doDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { message } from 'ant-design-vue'
import { deleteUserUsingPost, listUserVoByPageUsingPost } from '@/api/userController.ts'
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

//  定义数据
const dataList = ref<API.UserVO[]>([])
const total = ref(0)

//  获取数据
const fetchData = async () => {
  const res = await listUserVoByPageUsingPost({
    ...searchParams,
  })
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败' + res.data.message)
  }
}
//  初始化数据 只请求一次
onMounted(() => {
  fetchData()
})

//  分页参数  computed:计算属性
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: total.value, // 总条数
    showSizeChanger: true, //  显示分页大小选择器
    showTotal: (total: number) => `共 ${total} 条`, //  显示总数
  }
})
//  搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 3,
  sortField: 'createTime',
  sortOrder: 'ascend',
})

//  表格数据变化重新获取数据
const doTableChange = (page: any) => {
  searchParams.current = page.current,
  searchParams.pageSize = page.pageSize,
  fetchData()
}

//  搜索数据
const doSearch = () => {
  //  重置页码
  searchParams.current = 1
  fetchData()
}

// 删除用户
const doDelete = async (id: String) => {
  if (!id) {
    return
  }
  //管理员不能删除自己
  const loginUserStore = useLoginUserStore()
  if (loginUserStore.loginUser.userRole === 'admin') {

    message.error('管理员不能删除自己')
    return
  }
  const res = await deleteUserUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 重新获取数据
     await fetchData()
  } else {
    message.error('删除失败' + res.data.message)
  }
}
</script>
