<template>
  <div id="pictureSearchForm">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-collapse v-model:activeKey="activeKey" ghost>
        <a-collapse-panel key="1" header="请根据搜索类型进行搜索">
          <a-space>
            <a-form-item label="关键词">
              <a-input
                v-model:value="searchParams.searchText"
                placeholder="请根据关键词搜索"
                allow-clear
              />
            </a-form-item>
            <a-form-item label="分类" name="category">
              <a-auto-complete
                v-model:value="searchParams.category"
                style="min-width: 180px"
                :options="categoryOptions"
                placeholder="请输入分类"
                allowClear
              />
            </a-form-item>
            <a-form-item label="标签" name="tags">
              <a-select
                v-model:value="searchParams.tags"
                style="min-width: 180px"
                :options="tagOptions"
                mode="tags"
                placeholder="请输入标签"
                allowClear
              />
            </a-form-item>
            <a-form-item label="日期" name="dateRange">
              <a-range-picker
                style="width: 400px"
                show-time
                v-model:value="dateRange"
                :placeholder="['编辑开始时间', '编辑结束时间']"
                format="YYYY/MM/DD HH:mm:ss"
                :presets="rangePresets"
                @change="onRangeChange"
              />
            </a-form-item>
          </a-space>
          <a-space>
            <a-form-item label="名称" name="name">
              <a-input v-model:value="searchParams.name" placeholder="从名称搜索" allow-clear />
            </a-form-item>
            <a-form-item label="简介" name="introduction">
              <a-input
                v-model:value="searchParams.introduction"
                placeholder="从简介搜索"
                allow-clear
              />
            </a-form-item>
            <a-form-item label="宽度" name="picWidth">
              <a-input-number v-model:value="searchParams.picWidth" />
            </a-form-item>
            <a-form-item label="高度" name="picHeight">
              <a-input-number v-model:value="searchParams.picHeight" />
            </a-form-item>
            <a-form-item label="格式" name="picFormat">
              <a-input v-model:value="searchParams.picFormat" placeholder="从格式搜索" allow-clear />
            </a-form-item>
          </a-space>
          <a-form-item style="text-align: right">
            <a-space>
              <a-button type="primary" html-type="submit" style="width: 96px"> 搜索</a-button>
              <a-button html-type="reset" @click="doClear"> 重置</a-button>
            </a-space>
          </a-form-item>
        </a-collapse-panel>
      </a-collapse>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { listPictureTagCategoryUsingGet } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

interface Props {
  onSearch?: (searchParams: API.PictureQueryRequest) => void
}

const props = defineProps<Props>()
const activeKey = ref(['1'])

//  搜索条件
const searchParams = reactive<API.PictureQueryRequest>({})

//  搜索数据
const doSearch = () => {
  props.onSearch?.(searchParams)
}

/**
 * 获取标签和分类选项数据
 */
const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

/**
 * 获取标签和分类选项
 */
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // 转换成下拉选项组件接受的格式
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('获取标签分类列表失败，' + res.data.message)
  }
}

/**
 * 首次加载时标签和分类的变化
 */
onMounted(() => {
  getTagCategoryOptions()
})

const dateRange = ref<[]>([])

/**
 * 日期范围更改时触发
 * @param dates
 * @param dateStrings
 */
const onRangeChange = (dates: any[], dateStrings: string[]) => {
  if (dates.length < 2) {
    searchParams.startEditTime = undefined
    searchParams.endEditTime = undefined
  } else {
    searchParams.startEditTime = dates[0].toDate()
    searchParams.endEditTime = dates[1].toDate()
  }
}

// 日期范围预设
const rangePresets = ref([
  { label: '过去 7 天', value: [dayjs().add(-7, 'd'), dayjs()] },
  { label: '过去 14 天', value: [dayjs().add(-14, 'd'), dayjs()] },
  { label: '过去 30 天', value: [dayjs().add(-30, 'd'), dayjs()] },
  { label: '过去 90 天', value: [dayjs().add(-90, 'd'), dayjs()] },
])

// 清除搜索条件
const doClear = () => {
  // 取消所有对象的值
  Object.keys(searchParams).forEach((key) => {
    searchParams[key] = undefined
  })
  // 清空日期范围，单独操作，必须定义为数组
  dateRange.value = []
  // 重新触发搜索
  props.onSearch?.(searchParams)
}
</script>

<style scoped>
#pictureSearchForm .ant-form-item {
  margin-bottom: 8px;
}
</style>
