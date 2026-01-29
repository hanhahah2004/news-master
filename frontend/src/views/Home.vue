<template>
  <div class="home-page">
    <div class="container">
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="main-content">
            <div class="section-header">
              <h2>最新文章</h2>
            </div>
            <div v-loading="loading" class="article-list">
              <div v-if="articles.length === 0" class="empty-state">
                <el-icon><Document /></el-icon>
                <p>暂无文章</p>
              </div>
              <article-card
                v-for="article in articles"
                :key="article.id"
                :article="article"
              />
            </div>
            <div class="pagination" v-if="total > pageSize">
              <el-pagination
                v-model:current-page="currentPage"
                :page-size="pageSize"
                :total="total"
                layout="prev, pager, next"
                @current-change="handlePageChange"
              />
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <aside class="sidebar">
            <div class="sidebar-card">
              <div class="card-header">
                <h3 class="card-title">热门文章</h3>
              </div>
              <div class="hot-articles" v-loading="hotLoading">
                <div
                  v-for="(article, index) in hotArticles"
                  :key="article.id"
                  class="hot-article-item"
                  @click="goToArticle(article.id)"
                >
                  <span class="rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
                  <span class="title">{{ article.title }}</span>
                  <span class="views"><el-icon><View /></el-icon>{{ article.viewCount }}</span>
                </div>
              </div>
            </div>
            <div class="sidebar-card">
              <div class="card-header">
                <h3 class="card-title">文章分类</h3>
              </div>
              <div class="categories">
                <router-link
                  v-for="category in categories"
                  :key="category?.id"
                  :to="{ name: 'Category', params: { id: category?.id } }"
                  class="category-item"
                >
                  <span class="name">{{ category?.name }}</span>
                  <span class="count">{{ category?.articleCount || category?.count || category?.articles || 0 }}</span>
                </router-link>
              </div>
            </div>
          </aside>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Document, View } from '@element-plus/icons-vue'
import { getArticleList, getHotArticles, getArticlesByCategory } from '@/api/article'
import { getCategoryList } from '@/api/category'
import ArticleCard from '@/components/ArticleCard.vue'

const router = useRouter()
const loading = ref(false)
const hotLoading = ref(false)
const articles = ref([])
const hotArticles = ref([])
const categories = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function fetchArticles() {
  loading.value = true
  try {
    const res = await getArticleList({
      page: currentPage.value,
      size: pageSize.value,
      status: 1
    })
    
    // 尝试从不同的数据结构中获取文章列表
    articles.value = res.data?.records || res.data?.list || res.data || []
    total.value = res.data?.total || res.data?.count || articles.value.length || 0
  } catch (error) {
    console.error('获取文章列表失败:', error)
    articles.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function fetchHotArticles() {
  hotLoading.value = true
  try {
    const res = await getHotArticles(5)
    hotArticles.value = res.data
  } catch (error) {
    console.error('获取热门文章失败:', error)
  } finally {
    hotLoading.value = false
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
    categories.value = []
  }
}

function handlePageChange(page) {
  currentPage.value = page
  fetchArticles()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function goToArticle(id) {
  router.push({ name: 'ArticleDetail', params: { id } })
}

onMounted(() => {
  fetchArticles()
  fetchHotArticles()
  fetchCategories()
})
</script>

<style scoped lang="scss">
.home-page {
  padding: 20px 0;
}

.main-content {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;

  h2 {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;

  .el-icon {
    font-size: 64px;
    margin-bottom: 16px;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.sidebar {
  .sidebar-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    margin-bottom: 20px;
  }

  .card-header {
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }
}

.hot-articles {
  .hot-article-item {
    display: flex;
    align-items: center;
    padding: 10px 0;
    cursor: pointer;
    transition: background 0.3s;
    border-radius: 4px;

    &:hover {
      background: #f5f7fa;
      .title {
        color: #409EFF;
      }
    }

    .rank {
      width: 24px;
      height: 24px;
      border-radius: 4px;
      background: #409EFF;
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: 600;
      margin-right: 12px;
      flex-shrink: 0;

      &.rank-1 {
        background: #f56c6c;
      }

      &.rank-2 {
        background: #e6a23c;
      }

      &.rank-3 {
        background: #909399;
      }
    }

    .title {
      flex: 1;
      font-size: 14px;
      color: #303133;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      transition: color 0.3s;
    }

    .views {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #909399;
      margin-left: 12px;
    }
  }
}

.categories {
  .category-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    color: #606266;
    text-decoration: none;
    transition: all 0.3s;
    border-radius: 4px;

    &:hover {
      background: #f5f7fa;
      color: #409EFF;
      .name {
        color: #409EFF;
      }
    }

    .name {
      font-size: 14px;
      transition: color 0.3s;
    }

    .count {
      font-size: 12px;
      color: #909399;
      background: #f4f4f5;
      padding: 2px 8px;
      border-radius: 10px;
    }
  }
}

@media (max-width: 992px) {
  .el-col:last-child {
    display: none;
  }
}
</style>
