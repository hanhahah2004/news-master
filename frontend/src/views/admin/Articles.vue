<template>
  <div class="articles-management">
    <div class="page-header">
      <h2>文章管理</h2>
    </div>

    <div class="filter-bar card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="标题">
          <el-input v-model="filterForm.title" placeholder="搜索标题" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filterForm.categoryId" placeholder="全部" clearable>
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已下架" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="article-table card">
      <el-table :data="articles" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="authorNickname" label="作者" width="100" />
        <el-table-column prop="viewCount" label="阅读量" width="80" />
        <el-table-column prop="likeCount" label="点赞数" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" size="small" @click="editArticle(row)">编辑</el-button>
              <el-button
                :type="row.status === 1 ? 'warning' : 'success'"
                size="small"
                @click="togglePublish(row)"
              >
                {{ row.status === 1 ? '下架' : '发布' }}
              </el-button>
              <el-button type="danger" size="small" @click="deleteArticle(row)">删除</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

dayjs.locale('zh-cn')

const router = useRouter()
const loading = ref(false)
const articles = ref([])
const categories = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filterForm = reactive({
  title: '',
  categoryId: null,
  status: null
})

const categoriesData = [
  { id: 1, name: '新闻资讯' },
  { id: 2, name: '技术分享' },
  { id: 3, name: '生活感悟' },
  { id: 4, name: '其他' }
]

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function getStatusType(status) {
  const map = { 0: 'info', 1: 'success', 2: 'warning' }
  return map[status] || ''
}

function getStatusText(status) {
  const map = { 0: '草稿', 1: '已发布', 2: '已下架' }
  return map[status] || '未知'
}

async function loadArticles() {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    articles.value = [
      { id: 1, title: '欢迎使用新闻发布系统', categoryId: 1, categoryName: '新闻资讯', authorId: 1, authorNickname: 'admin', viewCount: 100, likeCount: 10, status: 1, createdAt: new Date() }
    ]
    total.value = 1
  } catch (error) {
    ElMessage.error('加载文章列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  loadArticles()
}

function resetFilter() {
  filterForm.title = ''
  filterForm.categoryId = null
  filterForm.status = null
  handleSearch()
}

function handlePageChange(page) {
  currentPage.value = page
  loadArticles()
}

function editArticle(article) {
  router.push({ name: 'EditArticle', params: { id: article.id } })
}

async function togglePublish(article) {
  try {
    await ElMessageBox.confirm(
      `确定要${article.status === 1 ? '下架' : '发布'}文章 "${article.title}" 吗?`,
      '提示',
      { type: 'warning' }
    )
    article.status = article.status === 1 ? 2 : 1
    ElMessage.success('操作成功')
  } catch {
    ElMessage.info('已取消')
  }
}

async function deleteArticle(article) {
  try {
    await ElMessageBox.confirm(`确定要删除文章 "${article.title}" 吗?`, '提示', {
      type: 'warning'
    })
    ElMessage.success('删除成功')
    loadArticles()
  } catch {
    ElMessage.info('已取消')
  }
}

onMounted(() => {
  categories.value = categoriesData
  loadArticles()
})
</script>

<style scoped lang="scss">
.articles-management {
  .page-header {
    margin-bottom: 20px;

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

  .article-table {
    margin-bottom: 0;
    border-radius: 0 0 8px 8px;

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
