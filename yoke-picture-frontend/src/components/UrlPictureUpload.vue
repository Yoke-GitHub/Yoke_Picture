<template>
  <div class="url-picture-upload">
    <a-input-group compact>
      <a-input
        v-model:value="fileUrl"
        style="width: calc(100% - 120px)"
        placeholder="请输入图片Url"
      />
      <a-button type="primary" style="width: 120px" :loading="loading" @click="handleUpload"
        >提交
      </a-button>
    </a-input-group>
    <div class="img-bar">
      <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
    </div>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { uploadPictureByUrlUsingPost } from '@/api/pictureController.ts'

interface Props {
  picture?: API.PictureVO
  spaceId?: number
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()
const fileUrl = ref<String>()
const loading = ref<boolean>(false)

/**
 * 上传图片
 * @param file
 */
const handleUpload = async () => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = { fileUrl: fileUrl.value }
    params.spaceId = props.spaceId
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('上传成功')
      //更新图片
      props.onSuccess?.(res.data.data)
    } else {
      message.error('上传失败' + res.data.message)
    }
  } catch (error) {
    console.log('上传失败')
    message.error('上传失败' + error.message)
  } finally {
    loading.value = false
  }
}
</script>
<style scoped>
.url-picture-upload {
  margin-bottom: 16px;
}

.url-picture-upload img {
  max-width: 100%;
  max-height: 320px;
}

.url-picture-upload .img-bar {
  text-align: center;
  margin-top: 16px;
}
</style>
