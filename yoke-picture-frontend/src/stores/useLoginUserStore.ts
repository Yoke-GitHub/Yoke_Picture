import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUserVoUsingGet } from '@/api/userController.ts'

/**
 *存储登录用户信息的状态
 */
export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<API.LoginUserVO>({
    userName: '未登录',
  })

  /**
   * 远程获取登录信息
   */
  async function fetchLoginUser() {
    const res = await getLoginUserVoUsingGet()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
  }

  /**
   *设置登录用户
   */
  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  const count = ref(0)
  const doubleCount = computed(() => count.value * 2)

  function increment() {
    count.value++
  }

  return { loginUser, fetchLoginUser, setLoginUser }
})
