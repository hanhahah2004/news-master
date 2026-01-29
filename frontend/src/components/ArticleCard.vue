<template>
  <div class="article-card" @click="goToArticle">
    <div class="card-content">
      <h3 class="title">{{ article.title }}</h3>
      <p class="summary">{{ article.summary || stripHtml(article.content) }}</p>
      <div class="meta">
        <span class="meta-item">
          <el-icon><User /></el-icon>
          {{ article.authorNickname || article.nickname || '匿名' }}
        </span>
        <span class="meta-item">
          <el-icon><Clock /></el-icon>
          {{ formatDate(article.createdAt) }}
        </span>
        <span class="meta-item">
          <el-icon><View /></el-icon>
          {{ article.viewCount || 0 }}
        </span>
        <span class="meta-item">
          <el-icon><ChatLineRound /></el-icon>
          {{ article.commentCount || 0 }}
        </span>
      </div>
    </div>
    <div class="card-cover" v-if="article.coverImage">
      <img :src="article.coverImage" :alt="article.title" />
    </div>
  </div>
</template>

<script setup>
import { User, Clock, View, ChatLineRound } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import relativeTime from 'dayjs/plugin/relativeTime'

dayjs.locale('zh-cn')
dayjs.extend(relativeTime)

const props = defineProps({
  article: {
    type: Object,
    required: true
  }
})

const router = useRouter()

function goToArticle() {
  router.push({ name: 'ArticleDetail', params: { id: props.article.id } })
}

function formatDate(date) {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD')
}

function stripHtml(html) {
  if (!html) return ''
  return html.replace(/<[^>]*>/g, '').slice(0, 150) + '...'
}
</script>

<style scoped lang="scss">
.article-card {
  display: flex;
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: all 0.3s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: #fafafa;
    padding-left: 10px;
    padding-right: 10px;
    margin-left: -10px;
    margin-right: -10px;
    border-radius: 8px;

    .title {
      color: #409EFF;
    }
  }

  .card-content {
    flex: 1;
    min-width: 0;
  }

  .title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 12px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    transition: color 0.3s;
  }

  .summary {
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
    margin: 0 0 16px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .meta {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    font-size: 13px;
    color: #909399;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 4px;

      .el-icon {
        font-size: 14px;
      }
    }
  }

  .card-cover {
    width: 180px;
    height: 120px;
    margin-left: 20px;
    flex-shrink: 0;
    border-radius: 8px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s;
    }
  }
}

@media (max-width: 768px) {
  .article-card {
    flex-direction: column;

    .card-cover {
      width: 100%;
      height: 180px;
      margin-left: 0;
      margin-top: 12px;
    }
  }
}
</style>
