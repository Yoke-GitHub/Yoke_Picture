<template>
  <div id="UserLoginPage">
    <h2 class="title">Yoke云图库-用户登录</h2>
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
      <div class="tips">
        没有账号？
        <RouterLink to="/user/register">去注册</RouterLink>
      </div>

      <a-form-item>
        <a-button
          type="primary"
          html-type="submit"
          style="width: 100%"
          >登录
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { userLoginUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

// 用于接受表单输入的值
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const loginUserStore = useLoginUserStore()

/**
 * 表单提交事件
 * @param values
 */
const handSubmit = async (values: any) => {
  try {
    const res = await userLoginUsingPost(values)
    // 登录成功，把登录态保存到全局状态中
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('登录成功')
      await router.push({
        path: '/',
        replace: true,
      })
    } else {
      message.error('登录失败' + res.data.message)
    }
  } catch (e) {
    message.error('登录失败' + e.message)
  }
}
</script>
<style scoped>
#UserLoginPage {
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
