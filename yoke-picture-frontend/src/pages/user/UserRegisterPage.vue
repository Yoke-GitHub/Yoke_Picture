<template>
  <div id="UserRegisterPage">
    <h2 class="title">Yoke云图库-用户注册</h2>
    <div class="desc">企业级智能云图库</div>
    <a-form :model="formState" autocomplete="off" @finish="handSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>

      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码长度不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>

      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请输入确认密码' },
          { min: 8, message: '确认密码长度不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="请输入确认密码" />
      </a-form-item>
      <div class="tips">
        已有账号？
        <RouterLink to="/user/register">去登录</RouterLink>
      </div>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { userLoginUsingPost, userRegisterUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

// 用于接受表单输入的值
const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

const loginUserStore = useLoginUserStore()

/**
 * 表单提交事件
 * @param values
 */
const handSubmit = async (values: any) => {
  // 密码和确认密码不一致
  if (values.userPassword !== values.checkPassword) {
    message.error('两次输入的密码不一致')
    return
  }
  try {
    const res = await userRegisterUsingPost(values)
    // 注册成功，跳转到登录页
    if (res.data.code === 0 && res.data.data) {
      message.success('注册成功')
      await router.push({
        path: '/user/login',
        replace: true,
      })
    } else {
      message.error('注册失败' + res.data.message)
    }
  } catch (e) {
    message.error('注册失败' + e.message)
  }
}
</script>
<style scoped>
#UserRegisterPage {
  max-width: 400px;
  margin: 0 auto;
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  margin-bottom: 16px;
  color: #bbb;
}

.tips {
  text-align: right;
  font-size: 13px;
  color: #bbb;
}
</style>
