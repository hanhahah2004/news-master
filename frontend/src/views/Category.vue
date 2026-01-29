<template>
  <div class="category-page">
    <div class="container">
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ name: 'Home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ categoryName }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="category-header card">
        <h1 class="category-title">{{ categoryName }}</h1>
        <p class="category-desc" v-if="categoryDesc">{{ categoryDesc }}</p>
      </div>

      <div class="article-list" v-if="articles.length > 0">
        <div class="article-item card" v-for="article in articles" :key="article.id" @click="goToDetail(article.id)">
          <div class="article-cover" v-if="article.coverImage">
            <el-image :src="article.coverImage" fit="cover" />
          </div>
          <div class="article-info">
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-summary">{{ article.summary || '暂无摘要...' }}</p>
            <div class="article-meta">
              <span class="meta-item">
                <el-icon><User /></el-icon>
                {{ article.authorNickname }}
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ formatDate(article.createdAt) }}
              </span>
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ article.viewCount }}
              </span>
              <span class="meta-item">
                <el-icon><ChatDotRound /></el-icon>
                {{ article.commentCount }}
              </span>
            </div>
          </div>
        </div>
      </div>
      <div class="empty-state card" v-else>
        <el-icon><Document /></el-icon>
        <p>该分类下暂无文章</p>
      </div>

      <div class="pagination" v-if="totalPages > 1">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import { User, Clock, View, ChatDotRound, Document } from '@element-plus/icons-vue'
import { getArticlesByCategory } from '@/api/article'
import { getCategoryById } from '@/api/category'
import { getCategoryList } from '@/api/category'

dayjs.locale('zh-cn')

const route = useRoute()
const router = useRouter()

const articles = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)
const categoryName = ref('')
const categoryDesc = ref('')

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD')
}

async function loadCategory() {
  try {
    const res = await getCategoryById(route.params.id)
    categoryName.value = res.data.name
    categoryDesc.value = res.data.description
  } catch (error) {
    console.error('获取分类信息失败:', error)
  }
}

async function loadArticles() {
  try {
    const res = await getArticlesByCategory(route.params.id, {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    articles.value = res.data.list
    total.value = res.data.total
    totalPages.value = res.data.totalPages
  } catch (error) {
    console.error('获取文章列表失败:', error)
  }
}

function handlePageChange(page) {
  currentPage.value = page
  loadArticles()
}

function goToDetail(id) {
  router.push({ name: 'ArticleDetail', params: { id } })
}

onMounted(() => {
  loadCategory()
  loadArticles()
})

watch(() => route.params.id, () => {
  currentPage.value = 1
  loadCategory()
  loadArticles()
})
</script>

<style scoped lang="scss">
.category-page {
  .breadcrumb {
    margin-bottom: 20px;
  }

  .category-header {
    .category-title {
      font-size: 28px;
      font-weight: 700;
      color: #303133;
      margin-bottom: 8px;
    }

    .category-desc {
      color: #909399;
    }
  }

  .article-list {
    .article-item {
      display: flex;
      gap: 20px;
      cursor: pointer;
      transition: transform 0.3s, box-shadow 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
      }

      .article-cover {
        flex-shrink: 0;
        width: 240px;
        height: 160px;
        border-radius: 8px;
        overflow: hidden;

        .el-image {
          width: 100%;
          height: 100%;
        }
      }

      .article-info {
        flex: 1;
        display: flex;
        flex-direction: column;

        .article-title {
          font-size: 20px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 12px;
          line-height: 1.4;
        }

        .article-summary {
          flex: 1;
          color: #606266;
          font-size: 14px;
          line-height: 1.6;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .article-meta {
          display: flex;
          gap: 20px;
          color: #909399;
          font-size: 13px;
          margin-top: 12px;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 4px;

            .el-icon {
              font-size: 14px;
            }
          }
        }
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 60px 20px;

    .el-icon {
      font-size: 64px;
      color: #c0c4cc;
      margin-bottom: 16px;
    }

    p {
      color: #909399;
    }
  }
}

@media (max-width: 768px) {
  .category-page {
    .article-item {
      flex-direction: column;

      .article-cover {
        width: 100%;
        height: 200px;
      }

      .article-info {
        .article-title {
          font-size: 18px;
        }
      }
    }
  }
}
</style>
