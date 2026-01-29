<template>
  <div class="comments-management">
    <div class="page-header">
      <h2>评论管理</h2>
    </div>

    <div class="filter-bar card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="文章">
          <el-input v-model="filterForm.articleTitle" placeholder="搜索文章" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已删除" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="comment-table card">
      <el-table :data="comments" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
        <el-table-column prop="userNickname" label="用户" width="100" />
        <el-table-column prop="articleTitle" label="文章" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="评论时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                v-if="row.status !== 1"
                type="success"
                size="small"
                @click="approveComment(row)"
              >
                通过
              </el-button>
              <el-button type="danger" size="small" @click="deleteComment(row)">删除</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import { getAllComments, deleteComment as deleteCommentApi } from '@/api/comment'

dayjs.locale('zh-cn')

const loading = ref(false)
const comments = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filterForm = reactive({
  articleTitle: '',
  status: null
})

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function getStatusType(status) {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || ''
}

function getStatusText(status) {
  const map = { 0: '待审核', 1: '已发布', 2: '已删除' }
  return map[status] || '未知'
}

async function loadComments() {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      articleTitle: filterForm.articleTitle,
      status: filterForm.status
    }
    const res = await getAllComments(params)
    comments.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('加载评论列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  loadComments()
}

function resetFilter() {
  filterForm.articleTitle = ''
  filterForm.status = null
  handleSearch()
}

function handlePageChange(page) {
  currentPage.value = page
  loadComments()
}

async function approveComment(comment) {
  try {
    await ElMessageBox.confirm('确定通过这条评论吗?', '提示', { type: 'info' })
    comment.status = 1
    ElMessage.success('操作成功')
  } catch {
    ElMessage.info('已取消')
  }
}

async function deleteComment(comment) {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗?', '提示', { type: 'warning' })
    await deleteCommentApi(comment.id)
    ElMessage.success('删除成功')
    loadComments()
  } catch {
    ElMessage.info('已取消')
  }
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped lang="scss">
.comments-management {
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

  .comment-table {
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
