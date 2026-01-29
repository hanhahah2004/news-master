<template>
  <div class="search-page">
    <div class="container">
      <div class="search-header card">
        <h2>搜索: "{{ keyword }}"</h2>
        <span class="search-count">找到 {{ total }} 条结果</span>
      </div>

      <div class="search-form card">
        <el-input
          v-model="searchKeyword"
          placeholder="输入关键词搜索..."
          size="large"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>

      <div class="article-list" v-if="articles.length > 0">
        <div class="article-item card" v-for="article in articles" :key="article.id" @click="goToDetail(article.id)">
          <div class="article-cover" v-if="article.coverImage">
            <el-image :src="article.coverImage" fit="cover" />
          </div>
          <div class="article-info">
            <h3 class="article-title" v-html="highlightKeyword(article.title)"></h3>
            <p class="article-summary" v-html="highlightKeyword(article.summary || '暂无摘要...')"></p>
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
              <span class="meta-item category-tag">
                <el-icon><Folder /></el-icon>
                {{ article.categoryName }}
              </span>
            </div>
          </div>
        </div>
      </div>
      <div class="empty-state card" v-else-if="hasSearched">
        <el-icon><Search /></el-icon>
        <p>未找到相关文章</p>
        <p class="tip">试试其他关键词</p>
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
import { User, Clock, View, Search, Folder } from '@element-plus/icons-vue'
import { searchArticles } from '@/api/article'

dayjs.locale('zh-cn')

const route = useRoute()
const router = useRouter()

const searchKeyword = ref('')
const keyword = ref('')
const articles = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)
const hasSearched = ref(false)

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD')
}

function highlightKeyword(text) {
  if (!text || !keyword.value) return text
  const regex = new RegExp(`(${keyword.value})`, 'gi')
  return text.replace(regex, '<span class="highlight">$1</span>')
}

async function handleSearch() {
  if (!searchKeyword.value.trim()) return
  keyword.value = searchKeyword.value
  currentPage.value = 1
  hasSearched.value = true
  await loadArticles()
  router.push({ name: 'Search', query: { keyword: searchKeyword.value } })
}

async function loadArticles() {
  try {
    const res = await searchArticles(keyword.value, {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    articles.value = res.data.list
    total.value = res.data.total
    totalPages.value = res.data.totalPages
  } catch (error) {
    console.error('搜索失败:', error)
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
  if (route.query.keyword) {
    searchKeyword.value = route.query.keyword
    keyword.value = route.query.keyword
    hasSearched.value = true
    loadArticles()
  }
})

watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    searchKeyword.value = newKeyword
    keyword.value = newKeyword
    currentPage.value = 1
    hasSearched.value = true
    loadArticles()
  }
})
</script>

<style scoped lang="scss">
.search-page {
  .search-header {
    display: flex;
    align-items: baseline;
    gap: 12px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }

    .search-count {
      color: #909399;
      font-size: 14px;
    }
  }

  .search-form {
    margin-bottom: 20px;
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

          :deep(.highlight) {
            color: #F56C6C;
            font-weight: 600;
          }
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

          :deep(.highlight) {
            color: #F56C6C;
            font-weight: 600;
          }
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
      font-size: 16px;
    }

    .tip {
      margin-top: 8px;
      font-size: 14px;
      color: #c0c4cc;
    }
  }
}

@media (max-width: 768px) {
  .search-page {
    .article-item {
      flex-direction: column;

      .article-cover {
        width: 100%;
        height: 200px;
      }
    }
  }
}
</style>
