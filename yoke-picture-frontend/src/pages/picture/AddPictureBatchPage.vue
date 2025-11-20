<template>
  <div id="AddPictureBatchPage">
    <h2 style="margin-bottom: 16px">批量创建图片</h2>
    <!-- 表单信息 -->
    <a-form :model="formData" layout="vertical" @finish="handleSubmit">
      <a-form-item label="关键词" name="searchText">
        <a-input v-model:value="formData.searchText" placeholder="请输入关键词" />
      </a-form-item>
      <a-form-item label="抓取数量" name="count">
        <a-input-number
          v-model:value="formData.count"
          :max="30"
          :min="1"
          allowClear
          autoSize
          placeholder="请输入数量"
          style="width: 180px"
        />
      </a-form-item>
      <a-form-item label="名称前缀" name="namePrefix">
        <a-input
          v-model:value="formData.namePrefix"
          allow-clear
          placeholder="请输入名称前缀,会自动补充序号"
        />
      </a-form-item>
      <a-form-item>
        <a-button :loading="loading" html-type="submit" style="width: 100%" type="primary"
          >批量提交
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getPictureVoByIdUsingGet,
  listPictureTagCategoryUsingGet,
  uploadPictureByBatchUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

/**
 * 获取数据
 */
const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})

const loading = ref(false)

/**
 * 路由跳转
 */
const router = useRouter()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  loading.value = true
  try {
    const res = await uploadPictureByBatchUsingPost({
      ...formData,
    })
    if (res.data.code === 0 && res.data.data) {
      message.success('批量创建成功，共 ${res.data.data} 条')
      // 跳转到首页
      router.push({
        path: `/`,
      })
    } else {
      message.error('批量创建失败，' + res.data.message)
    }
  } catch (e) {
    message.error('批量创建失败，' + e)
  }
  loading.value = false
}
</script>
<style scoped>
#AddPictureBatchPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
