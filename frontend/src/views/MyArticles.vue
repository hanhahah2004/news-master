<template>
  <div class="my-articles">
    <div class="container">
      <div class="page-header card">
        <h2>我的文章</h2>
        <router-link :to="{ name: 'CreateArticle' }">
          <el-button type="primary">
            <el-icon><Plus /></el-icon>
            发布文章
          </el-button>
        </router-link>
      </div>

      <div class="filter-bar card">
        <el-radio-group v-model="statusFilter" @change="handleFilterChange">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="1">已发布</el-radio-button>
          <el-radio-button :label="0">草稿</el-radio-button>
        </el-radio-group>
      </div>

      <div class="article-list" v-if="articles.length > 0">
        <div class="article-item card" v-for="article in articles" :key="article.id">
          <div class="article-info">
            <h3 class="article-title">
              <router-link :to="{ name: 'ArticleDetail', params: { id: article.id } }">
                {{ article.title }}
              </router-link>
            </h3>
            <p class="article-summary">{{ article.summary || '暂无摘要...' }}</p>
            <div class="article-meta">
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ formatDate(article.createdAt) }}
              </span>
              <span class="meta-item">
                <el-icon><View /></el-icon>
                {{ article.viewCount }} 阅读
              </span>
              <span class="meta-item">
                <el-icon><ChatDotRound /></el-icon>
                {{ article.commentCount }} 评论
              </span>
              <span class="status-tag" :class="getStatusClass(article.status)">
                {{ getStatusText(article.status) }}
              </span>
            </div>
          </div>
          <div class="article-actions">
            <el-button-group>
              <el-button type="primary" size="small" @click="editArticle(article.id)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button v-if="article.status === 0" type="success" size="small" @click="publishArticle(article.id)">
                <el-icon><Promotion /></el-icon>
                发布
              </el-button>
              <el-button v-else type="warning" size="small" @click="unpublishArticle(article.id)">
                <el-icon><Remove /></el-icon>
                下架
              </el-button>
              <el-button type="danger" size="small" @click="deleteArticle(article.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </el-button-group>
          </div>
        </div>
      </div>
      <div class="empty-state card" v-else>
        <el-icon><Document /></el-icon>
        <p>还没有发布过文章</p>
        <router-link :to="{ name: 'CreateArticle' }">
          <el-button type="primary">发布第一篇文章</el-button>
        </router-link>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Clock, View, ChatDotRound, Promotion, Remove, Document } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import { useUserStore } from '@/stores/user'
import { getArticlesByAuthor, publishArticle as publishApi, unpublishArticle as unpublishApi, deleteArticle as deleteApi } from '@/api/article'

dayjs.locale('zh-cn')

const router = useRouter()
const userStore = useUserStore()

const articles = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)
const statusFilter = ref(null)

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function getStatusClass(status) {
  const map = { 0: 'draft', 1: 'published', 2: 'deleted' }
  return map[status] || ''
}

function getStatusText(status) {
  const map = { 0: '草稿', 1: '已发布', 2: '已删除' }
  return map[status] || '未知'
}

async function loadArticles() {
  try {
    if (!userStore.user) {
      // 等待用户信息加载完成
      await userStore.fetchUserInfo()
      if (!userStore.user) {
        console.error('无法获取用户信息')
        return
      }
    }
    
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    if (statusFilter.value !== null) {
      params.status = statusFilter.value
    }
    const res = await getArticlesByAuthor(userStore.user.id, params)
    articles.value = res.data.list
    total.value = res.data.total
    totalPages.value = res.data.totalPages
  } catch (error) {
    console.error('加载文章列表失败:', error)
  }
}

function handlePageChange(page) {
  currentPage.value = page
  loadArticles()
}

function handleFilterChange() {
  currentPage.value = 1
  loadArticles()
}

function editArticle(id) {
  router.push({ name: 'EditArticle', params: { id } })
}

async function publishArticle(id) {
  try {
    await publishApi(id)
    ElMessage.success('发布成功')
    loadArticles()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

async function unpublishArticle(id) {
  try {
    await unpublishApi(id)
    ElMessage.success('已下架')
    loadArticles()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

async function deleteArticle(id) {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteApi(id)
    ElMessage.success('删除成功')
    loadArticles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadArticles()
})
</script>

<style scoped lang="scss">
.my-articles {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }
  }

  .filter-bar {
    margin-bottom: 0;
    border-radius: 8px 8px 0 0;
  }

  .article-list {
    .article-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 0;
      border-radius: 0;
      border-top: none;

      &:last-of-type {
        border-radius: 0 0 8px 8px;
      }

      .article-info {
        flex: 1;

        .article-title {
          font-size: 18px;
          font-weight: 600;
          margin-bottom: 8px;

          a {
            color: #303133;

            &:hover {
              color: #409EFF;
            }
          }
        }

        .article-summary {
          color: #909399;
          font-size: 14px;
          margin-bottom: 8px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          max-width: 600px;
        }

        .article-meta {
          display: flex;
          align-items: center;
          gap: 16px;
          color: #909399;
          font-size: 13px;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 4px;
          }

          .status-tag {
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 12px;

            &.draft {
              background: #e4e7ed;
              color: #909399;
            }

            &.published {
              background: #e1f3d8;
              color: #67c23a;
            }

            &.deleted {
              background: #fde2e2;
              color: #f56c6c;
            }
          }
        }
      }

      .article-actions {
        flex-shrink: 0;
        margin-left: 20px;
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 60px;

    .el-icon {
      font-size: 64px;
      color: #c0c4cc;
      margin-bottom: 16px;
    }

    p {
      color: #909399;
      margin-bottom: 16px;
    }
  }
}

@media (max-width: 768px) {
  .my-articles {
    .article-item {
      flex-direction: column;
      align-items: flex-start !important;

      .article-actions {
        margin-left: 0;
        margin-top: 12px;
      }
    }
  }
}
</style>
