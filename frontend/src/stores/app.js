import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getSiteConfig } from '@/api/config'
import { getCategoryList } from '@/api/category'
import { getArticlesByCategory } from '@/api/article'
import { getFriendLinks } from '@/api/friendLink'

export const useAppStore = defineStore('app', () => {
  const siteConfig = ref({
    site_name: '新闻发布系统',
    site_description: '一个现代化的新闻发布系统',
    site_keywords: '新闻,资讯,博客',
    icp_number: '',
    default_avatar: '/default-avatar.png'
  })
  const categories = ref([])
  const hotArticles = ref([])
  const friendLinks = ref([])

  async function fetchSiteConfig() {
    try {
      const res = await getSiteConfig()
      siteConfig.value = { ...siteConfig.value, ...res.data }
    } catch (error) {
      console.error('获取站点配置失败:', error)
    }
  }

  async function fetchCategories() {
    try {
      const res = await getCategoryList()
      
      if (Array.isArray(res.data)) {
        // 为每个分类获取文章数量
        const categoriesWithCount = await Promise.all(res.data.map(async (category) => {
          try {
            // 调用分类文章列表API来获取文章数量
            const articleRes = await getArticlesByCategory(category.id, { page: 1, size: 1 })
            // 从API响应中提取文章总数
            const totalArticles = articleRes.data?.total || articleRes.data?.count || articleRes.data?.list?.length || 0
            
            // 返回包含统计数的分类对象
            return {
              ...category,
              articleCount: totalArticles
            }
          } catch (error) {
            console.error(`获取分类 ${category.name} 的文章数量失败:`, error)
            // 如果获取失败，返回原分类对象并将统计数设为0
            return {
              ...category,
              articleCount: 0
            }
          }
        }))
        
        categories.value = categoriesWithCount
      } else {
        categories.value = res.data
      }
    } catch (error) {
      console.error('获取分类失败:', error)
    }
  }

  function setHotArticles(data) {
    hotArticles.value = data
  }

  async function fetchFriendLinks() {
    try {
      const res = await getFriendLinks()
      // 确保友情链接 URL 包含完整协议
      friendLinks.value = res.data.map(link => {
        let url = link.url.replace('http://localhost:3000/', '')
        // 如果 URL 不包含协议，则添加 http:// 前缀
        if (!/^https?:\/\//i.test(url)) {
          url = `http://${url}`
        }
        return {
          ...link,
          url
        }
      })
    } catch (error) {
      console.error('获取友情链接失败:', error)
    }
  }

  return {
    siteConfig,
    categories,
    hotArticles,
    friendLinks,
    fetchSiteConfig,
    fetchCategories,
    fetchFriendLinks,
    setHotArticles
  }
})
