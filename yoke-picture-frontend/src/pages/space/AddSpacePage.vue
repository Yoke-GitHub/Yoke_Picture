<template>
  <div id="AddSpacePage">
    <h2 style="margin-bottom: 16px">{{ route.query?.id ? '修改' : '创建' }}{{ SPACE_TYPE_MAP[spaceType]}}</h2>
    <!-- 表单信息 -->
    <a-form layout="vertical" :model="spaceForm" @finish="handleSubmit">
      <a-form-item label="空间名称" name="name">
        <a-input v-model:value="spaceForm.spaceName" placeholder="请输入空间名称" />
      </a-form-item>
      <a-form-item label="空间级别" name="spaceLevel">
        <a-select
          v-model:value="spaceForm.spaceLevel"
          :options="SPACE_LEVEL_OPTIONS"
          style="width: 180px"
          placeholder="请选择空间级别"
          allowClear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" :loading="loading" style="width: 100%"
          >提交
        </a-button>
      </a-form-item>
    </a-form>

    <!--空间级别介绍-->
    <a-card style="margin-top: 16px" title="空间级别介绍">
      <a-typography-paragraph
        >* 目前仅支持开通普通版，如需升级空间，请联系
        <a href="mailto:admin@Yoke.com" target="_blank">admin@Yoke.com</a>
      </a-typography-paragraph>
      <a-typography-paragraph v-for="spaceLevel in spaceLevelList"
        >{{ spaceLevel.text }} 大小：{{ spaceLevel.maxSize }}  数量：{{ spaceLevel.maxCount }}
      </a-typography-paragraph>
    </a-card>
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  addSpaceUsingPost,
  getSpaceVoByIdUsingGet,
  listSpaceLevelUsingGet,
  updateSpaceUsingPost,
} from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { SPACE_LEVEL_OPTIONS, SPACE_TYPE_ENUM, SPACE_TYPE_MAP } from '@/constants/Space.ts'

/**
 * 获取数据
 */
const space = ref<API.SpaceVO>()
const spaceForm = reactive<API.SpaceAddRequest | API.SpaceEditRequest>({})
const loading = ref(false)
const spaceLevelList = ref<API.SpaceLevel[]>([])

/**
 * 获取空间级别列表
 */
const fetchSpaceLevelList = async () => {
  const res = await listSpaceLevelUsingGet()
  if (res.data.code === 0 && res.data.data) {
    spaceLevelList.value = res.data.data
  } else {
    message.error('获取空间级别失败' + res.data.message)
  }
}
onMounted(() => {
  fetchSpaceLevelList()
})

const router = useRouter()

// 空间类别
const spaceType = computed(() => {
  if (route.query?.type) {
    return Number(route.query.type)
  }
  return SPACE_TYPE_ENUM.PRIVATE
})


/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const spaceId = space.value?.id
  loading.value = true
  let res
  if (spaceId) {
    // 更新
    res = await updateSpaceUsingPost({
      id: spaceId,
      ...spaceForm,
    })
  } else {
    // 创建
    res = await addSpaceUsingPost({
      ...spaceForm,
      spaceType: spaceType.value,
    })
  }
  // 处理结果
  if (res.data.code === 0 && res.data.data) {
    message.success('操作成功')
    // 跳转到空间详情页
    router.push({
      path: `/space/${res.data.data}`,
    })
  } else {
    message.error('操作失败，' + res.data.message)
  }
  loading.value = false
}

/**
 *使用路由
 */
const route = useRoute()

/**
 * 获取老数据信息
 */
const getOldSpace = async () => {
  /**
   * 获取新数据
   */
  const id = route.query?.id
  if (id) {
    const res = await getSpaceVoByIdUsingGet({
      id: id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      space.value = data
      spaceForm.spaceName = data.spaceName
      spaceForm.spaceLevel = data.spaceLevel
    }
  }
}
/**
 * 首次加载时加载老数据
 */
onMounted(() => {
  getOldSpace()
})
</script>
<style scoped>
#AddSpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
