import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getUserInfo, logout } from '@/api/user'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 1)
  const role = computed(() => user.value?.role || 0)

  async function loginAction(loginData) {
    try {
      const res = await login(loginData)
      token.value = res.data.token
      localStorage.setItem('token', res.data.token)
      await fetchUserInfo()
      return { success: true }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || '登录失败' }
    }
  }

  async function registerAction(registerData) {
    try {
      await register(registerData)
      return { success: true }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || '注册失败' }
    }
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      user.value = res.data
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  async function logoutAction() {
    try {
      await logout()
    } catch (error) {
      console.error('登出失败:', error)
    }
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    router.push({ name: 'Home' })
  }

  function initAuth() {
    if (token.value) {
      fetchUserInfo()
    }
  }

  return {
    token,
    user,
    isLoggedIn,
    isAdmin,
    role,
    loginAction,
    registerAction,
    fetchUserInfo,
    logoutAction,
    initAuth
  }
})
