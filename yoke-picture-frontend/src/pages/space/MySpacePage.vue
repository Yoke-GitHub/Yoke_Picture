<template>
  <div id="mySpacePage">
    <p>正在跳转，请稍后。。。</p>
  </div>
</template>
<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { listSpaceVoByPageUsingPost } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { onMounted } from 'vue'
import { SPACE_TYPE_ENUM } from '@/constants/Space.ts'

const rooter = useRouter()
const loginUserStore = useLoginUserStore()

const checkUserSpace = async () => {
  const loginUser = loginUserStore.loginUser
  if (!loginUser?.id) {
    rooter.replace('/user/login')
    return
  }
  const res = await listSpaceVoByPageUsingPost({
    userId:loginUser.id,
    current: 1,
    pageSize: 1,
    spaceType: SPACE_TYPE_ENUM.PRIVATE
  })
  if(res.data.code == 0) {
    if(res.data.data?.records?.length > 0) {
      const space = res.data.data.records[0]
      rooter.replace(`/space/${space.id}`)
    } else {
      rooter.replace('/space/add_space')
      message.warn('请先创建空间')
    }
  }else {
    message.error("加载我的空间失败" + res.data.msg)
  }
}

onMounted(() => {
  checkUserSpace()
})
</script>
<style scoped>
#mySpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
