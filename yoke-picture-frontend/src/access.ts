import router from '@/router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'

let firstFetchLoginUser = true

/**
 * 全局权限校验，每次切换页面都会执行
 */
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser

  // 确保页面刷新时，首次加载时，能等待后端返回用户信息后在进行权限校验
  if (firstFetchLoginUser) {
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
    firstFetchLoginUser = false
  }
  const toUrl = to.fullPath
  //  自定义权限校验逻辑 ， 如管理员才能访问 /admin 开头的 页面
  if (toUrl.startsWith('/admin')) {
    if (!loginUser || loginUser.userRole !== 'admin') {
      message.error('没有权限访问该页面')
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
  }
  next()
})
