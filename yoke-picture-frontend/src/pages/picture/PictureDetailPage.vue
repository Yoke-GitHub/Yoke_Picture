<template>
  <div id="PictureDetailPage">
    <a-row :gutter="[16, 16]">
      <!-- 图片展示区 -->
      <a-col :sm="24" :md="16" :xl="18">
        <a-card title="图片预览">
          <a-image style="max-height: 600px; object-fit: contain" :src="picture.url" />
        </a-card>
      </a-col>
      <!-- 图片信息区 -->
      <a-col :sm="24" :md="8" :xl="6">
        <a-card title="图片信息">
          <a-descriptions :column="1">
            <a-descriptions-item label="作者">
              <a-space>
                <a-avatar :size="24" :src="picture.user?.userAvatar" />
                <div>{{ picture.user?.userName }}</div>
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="名称">
              {{ picture.name ?? '未命名' }}
            </a-descriptions-item>
            <a-descriptions-item label="简介">
              {{ picture.introduction ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="分类">
              {{ picture.category ?? '默认' }}
            </a-descriptions-item>
            <a-descriptions-item label="标签">
              <a-tag v-for="tag in picture.tags" :key="tag">
                {{ tag }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="格式">
              {{ picture.picFormat ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽度">
              {{ picture.picWidth ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="高度">
              {{ picture.picHeight ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽高比">
              {{ picture.picScale ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="大小">
              {{ formatSize(picture.picSize) }}
            </a-descriptions-item>
            <a-descriptions-item label="主色调">
              <a-space>
                {{ picture.picColor ?? '-' }}
                <div
                  v-if="picture.picColor"
                  :style="{
                    backgroundColor: toHexColor(picture.picColor),
                    width: '16px',
                    height: '16px',
                  }"
                />
              </a-space>
            </a-descriptions-item>
          </a-descriptions>
          <!--图片操作-->
          <a-space wrap>
            <a-button type="primary" @click="doDownload">免费下载</a-button>
            <template #icon>
              <DownOutlined />
            </template>
<!--            <a-button-->
<!--              v-if="!picture.spaceId"-->
<!--              @click="doReviewState(picture.id, PIC_REVIEW_STATUS_ENUM.REJECT)"-->
<!--              >拒绝-->
<!--            </a-button>-->
            <a-button :icon="h(ShareAltOutlined)" type="primary" ghost @click="doShare"
            >分享
            </a-button>
            <a-button v-if="canEdit" :icon="h(EditOutlined)" type="default" @click="doEdit"
              >编辑
            </a-button>
            <a-button v-if="canDelete" :icon="h(DeleteOutlined)" danger @click="doDelete"
              >删除
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
    <ShareModal ref="shareModalRef" :link="shareLink" />
  </div>
</template>

<script setup lang="ts">
import {
  deletePictureUsingPost,
  doPictureReviewUsingPost,
  getPictureVoByIdUsingGet,
} from '@/api/pictureController.ts'
import { DeleteOutlined, DownOutlined, EditOutlined, ShareAltOutlined } from '@ant-design/icons-vue'
import { computed, h, onMounted, ref, type UnwrapRef } from 'vue'
import { message } from 'ant-design-vue'
import { formatSize, previewDownFile, toHexColor } from '@/utils'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { useRouter } from 'vue-router'
import { PIC_REVIEW_STATUS_ENUM } from '@/constants/Picture.ts'
import ShareModal from '@/components/ShareModal.vue'
import { SPACE_PERMISSION_ENUM } from '@/constants/Space.ts'

interface Props {
  id: string | number
}

const props = defineProps<Props>()
const picture = ref<API.PictureVO>({})

const loginUserStore = useLoginUserStore()


// 通用权限检查函数
function createPermissionChecker(permission: string) {
  return computed(() => {
    return (picture.value.permissionList ?? []).includes(permission)
  })
}

// 定义权限检查
const canEdit = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_EDIT)
const canDelete = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_DELETE)


/**
 * 获取图片详情
 */
const fetchPictureDetail = async () => {
  try {
    const res = await getPictureVoByIdUsingGet({
      id: props.id,
    })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
    } else {
      message.error('获取数据失败' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取数据失败' + e.message)
  }
}
const router = useRouter()
//编辑图片
const doEdit = () => {
  router.push({
    path: '/picture/add_picture',
    query: {
      id: picture.value.id,
      spaceId: picture.value.spaceId,
    },
  })
}
// 审核图片
const doReviewState = async (
  record: UnwrapRef<API.Picture['id']> | undefined,
  reviewStatus: number,
) => {
  const reviewMessage = reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? '审核通过' : '审核拒绝'
  const res = await doPictureReviewUsingPost({
    id: record,
    reviewStatus,
    reviewMessage,
  })
  if (res.data.code === 0) {
    message.success('审核成功')
    router.push({
      path: '/',
    })
  } else {
    message.error('审核失败' + res.data.message)
  }
}

// 删除图片
const doDelete = async () => {
  const id = picture.value.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    router.go(-1)
  } else {
    message.error('删除失败' + res.data.message)
  }
}

/**
 * 下载图片
 */
const doDownload = () => {
  previewDownFile(picture.value.url)
}

/**
 * 首次加载时加载老数据
 */
onMounted(() => {
  fetchPictureDetail()
})

//----分享弹窗----

// 分享弹窗引用
const shareModalRef = ref()
// 分享链接
const shareLink = ref<string>()

// 分享图片
const doShare = () => {
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.value.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}

</script>

<style scoped>
#PictureDetailPage {
  margin-bottom: 16px;
}
</style>
