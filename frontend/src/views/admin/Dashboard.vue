<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="stat in stats" :key="stat.title">
        <div class="stat-card">
          <div class="stat-icon" :style="{ background: stat.color }">
            <el-icon :size="24"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-title">{{ stat.title }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <h3 class="card-title">最近文章</h3>
            <router-link to="/admin/articles">查看更多</router-link>
          </div>
          <el-table :data="recentArticles" style="width: 100%">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="authorNickname" label="作者" width="100" />
            <el-table-column prop="viewCount" label="阅读量" width="80" />
            <el-table-column prop="createdAt" label="发布时间" width="160">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <h3 class="card-title">系统信息</h3>
          </div>
          <div class="system-info">
            <div class="info-item">
              <span class="info-label">系统版本</span>
              <span class="info-value">1.0.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">Vue版本</span>
              <span class="info-value">3.4.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">Spring Boot版本</span>
              <span class="info-value">3.2.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">MySQL版本</span>
              <span class="info-value">8.0+</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { DataAnalysis, Document, User, ChatDotRound } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import { getArticleList } from '@/api/article'
import { getDashboardStats } from '@/api/dashboard'

dayjs.locale('zh-cn')

const stats = ref([
  { title: '文章总数', value: 0, icon: Document, color: '#409EFF' },
  { title: '用户总数', value: 0, icon: User, color: '#67C23A' },
  { title: '评论总数', value: 0, icon: ChatDotRound, color: '#E6A23C' },
  { title: '访问量', value: 0, icon: DataAnalysis, color: '#F56C6C' }
])

const recentArticles = ref([])

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

async function loadData() {
  try {
    // 获取文章列表
    const articleRes = await getArticleList({ pageNum: 1, pageSize: 5 })
    recentArticles.value = articleRes.data.list
    
    // 获取仪表盘统计数据
    const statsRes = await getDashboardStats()
    const data = statsRes.data
    
    // 更新统计数据
    stats.value[0].value = data.articleCount
    stats.value[1].value = data.userCount
    stats.value[2].value = data.commentCount
    stats.value[3].value = data.viewCount
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.dashboard {
  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .stat-icon {
      width: 56px;
      height: 56px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }

    .stat-info {
      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: #303133;
      }

      .stat-title {
        color: #909399;
        font-size: 14px;
        margin-top: 4px;
      }
    }
  }

  .card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }

    .system-info {
      .info-item {
        display: flex;
        justify-content: space-between;
        padding: 12px 0;
        border-bottom: 1px solid #ebeef5;

        &:last-child {
          border-bottom: none;
        }

        .info-label {
          color: #909399;
        }

        .info-value {
          color: #303133;
          font-weight: 500;
        }
      }
    }
  }
}
</style>
