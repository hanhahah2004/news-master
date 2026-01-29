import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    // 检查响应是否成功，处理不同的成功响应格式
    // 1. 包含code字段且为200的响应
    if (response.data.code === 200) {
      return response.data
    }
    // 2. 包含code字段且为其他成功值的响应（如201 Created）
    if (response.data.code && response.data.code >= 200 && response.data.code < 300) {
      return response.data
    }
    // 3. 没有code字段的响应（通常是简单的成功响应）
    if (response.status >= 200 && response.status < 300) {
      // 如果响应体是对象，直接返回；否则返回成功状态
      return response.data || { success: true }
    }
    ElMessage.error(response.data.message || '请求失败')
    return Promise.reject(new Error(response.data.message || '请求失败'))
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      switch (status) {
        case 401:
          localStorage.removeItem('token')
          ElMessage.error(data.message || '登录已过期，请重新登录')
          router.push({ name: 'Login' })
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        default:
          ElMessage.error(data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
