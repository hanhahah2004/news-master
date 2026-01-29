<template>
  <div class="article-detail" v-if="article">
    <div class="container">
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ name: 'Home' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ name: 'Category', params: { id: article.categoryId } }">
            {{ article.categoryName }}
          </el-breadcrumb-item>
          <el-breadcrumb-item>{{ article.title }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="article-header">
        <h1 class="article-title">{{ article.title }}</h1>
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
            {{ article.viewCount }} 阅读
          </span>
          <span class="meta-item">
            <el-icon><ChatDotRound /></el-icon>
            {{ article.commentCount }} 评论
          </span>
        </div>
      </div>

      <div class="article-cover" v-if="article.coverImage">
        <el-image :src="article.coverImage" fit="cover" />
      </div>

      <div class="article-summary" v-if="article.summary">
        <el-alert type="info" :closable="false">
          {{ article.summary }}
        </el-alert>
      </div>

      <div class="article-body card">
        <div class="article-content" v-html="renderedContent"></div>
      </div>

      <div class="article-actions">
        <el-button type="primary" :icon="Star" @click="handleLike">
          点赞 ({{ article.likeCount }})
        </el-button>
        <el-button :icon="Collection" @click="handleCollect">
          收藏
        </el-button>
      </div>

      <div class="article-comments card">
        <div class="card-header">
          <h3 class="card-title">评论 ({{ article.commentCount }})</h3>
        </div>

        <div class="comment-form" v-if="userStore.isLoggedIn">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
            maxlength="500"
            show-word-limit
          />
          <div class="comment-form-actions">
            <el-button type="primary" @click="submitComment" :loading="submitting">
              发布评论
            </el-button>
          </div>
        </div>
        <div class="comment-login-tip" v-else>
          <span>登录后才能评论，</span>
          <router-link :to="{ name: 'Login' }">立即登录</router-link>
        </div>

        <div class="comment-list" v-if="comments.length > 0">
          <div class="comment-item" v-for="comment in comments" :key="comment.id">
            <div class="comment-avatar">
              <el-avatar :src="comment.userAvatar" :size="40">
                {{ comment.userNickname?.charAt(0) }}
              </el-avatar>
            </div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.userNickname }}</span>
                <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
              </div>
              <div class="comment-text">{{ comment.content }}</div>
              <div class="comment-actions">
                <span class="reply-btn" @click="showReplyForm(comment)">
                  <el-icon><ChatDotRound /></el-icon>
                  回复
                </span>
              </div>
              <div class="reply-form" v-if="replyingTo === comment.id">
                <el-input
                  v-model="replyContent"
                  type="textarea"
                  :rows="2"
                  placeholder="写下你的回复..."
                  maxlength="500"
                  show-word-limit
                />
                <div class="reply-form-actions">
                  <el-button size="small" @click="replyingTo = null">取消</el-button>
                  <el-button type="primary" size="small" @click="submitReply(comment.id)">
                    发布
                  </el-button>
                </div>
              </div>
              <div class="replies" v-if="comment.replies && comment.replies.length > 0">
                <div class="reply-item" v-for="reply in comment.replies" :key="reply.id">
                  <div class="reply-avatar">
                    <el-avatar :src="reply.userAvatar" :size="28">
                      {{ reply.userNickname?.charAt(0) }}
                    </el-avatar>
                  </div>
                  <div class="reply-content">
                    <span class="reply-author">{{ reply.userNickname }}</span>
                    <span v-if="reply.parentNickname" class="reply-to">
                      @{{ reply.parentNickname }}
                    </span>
                    : {{ reply.content }}
                    <span class="reply-time">{{ formatDate(reply.createdAt) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="empty-comments" v-else>
          暂无评论，快来抢沙发吧！
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
  </div>
  <div class="loading" v-else>
    <el-skeleton :rows="5" animated />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { marked } from 'marked'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import { ElMessage } from 'element-plus'
import { User, Clock, View, ChatDotRound, Star, Collection } from '@element-plus/icons-vue'
import { getArticleDetail, likeArticle } from '@/api/article'
import { getCommentsByArticle, getRepliesByParent, createComment } from '@/api/comment'
import { useUserStore } from '@/stores/user'

dayjs.locale('zh-cn')

const route = useRoute()
const userStore = useUserStore()

const article = ref(null)
const comments = ref([])
const commentContent = ref('')
const replyContent = ref('')
const replyingTo = ref(null)
const submitting = ref(false)

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)

const renderedContent = computed(() => {
  if (article.value?.content) {
    return marked(article.value.content)
  }
  return ''
})

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

async function loadArticle() {
  try {
    const res = await getArticleDetail(route.params.id)
    article.value = res.data
  } catch (error) {
    ElMessage.error('加载文章失败')
  }
}

async function loadComments() {
  try {
    const res = await getCommentsByArticle(route.params.id, {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    comments.value = res.data.list
    total.value = res.data.total
    totalPages.value = res.data.totalPages
    for (const comment of comments.value) {
      const repliesRes = await getRepliesByParent(comment.id)
      comment.replies = repliesRes.data.replies
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

async function handleLike() {
  try {
    await likeArticle(route.params.id)
    article.value.likeCount++
    ElMessage.success('点赞成功')
  } catch (error) {
    ElMessage.error('点赞失败')
  }
}

function handleCollect() {
  ElMessage.info('收藏功能开发中')
}

function showReplyForm(comment) {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  replyingTo.value = comment.id
}

async function submitComment() {
  if (!commentContent.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }

  submitting.value = true
  try {
    await createComment({
      articleId: route.params.id,
      content: commentContent.value
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    loadComments()
    article.value.commentCount++
  } catch (error) {
    ElMessage.error('评论失败')
  } finally {
    submitting.value = false
  }
}

async function submitReply(parentId) {
  if (!replyContent.value.trim()) {
    ElMessage.warning('回复内容不能为空')
    return
  }

  try {
    await createComment({
      articleId: route.params.id,
      parentId,
      content: replyContent.value
    })
    ElMessage.success('回复成功')
    replyContent.value = ''
    replyingTo.value = null
    loadComments()
  } catch (error) {
    ElMessage.error('回复失败')
  }
}

function handlePageChange(page) {
  currentPage.value = page
  loadComments()
}

onMounted(() => {
  loadArticle()
  loadComments()
})
</script>

<style scoped lang="scss">
.article-detail {
  .breadcrumb {
    margin-bottom: 20px;
  }

  .article-header {
    margin-bottom: 20px;
  }

  .article-title {
    font-size: 32px;
    font-weight: 700;
    color: #303133;
    margin-bottom: 16px;
    line-height: 1.3;
  }

  .article-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    color: #909399;
    font-size: 14px;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 4px;

      .el-icon {
        font-size: 16px;
      }
    }
  }

  .article-cover {
    margin-bottom: 20px;
    border-radius: 8px;
    overflow: hidden;

    .el-image {
      width: 100%;
      max-height: 400px;
    }
  }

  .article-summary {
    margin-bottom: 20px;
  }

  .article-body {
    min-height: 300px;
  }

  .article-actions {
    display: flex;
    justify-content: center;
    gap: 20px;
    padding: 20px 0;
  }

  .article-comments {
    .comment-form {
      margin-bottom: 20px;

      .comment-form-actions {
        margin-top: 10px;
        text-align: right;
      }
    }

    .comment-login-tip {
      text-align: center;
      padding: 20px;
      background: #f5f7fa;
      border-radius: 4px;
      margin-bottom: 20px;
    }

    .comment-list {
      .comment-item {
        display: flex;
        gap: 12px;
        padding: 16px 0;
        border-bottom: 1px solid #ebeef5;

        &:last-child {
          border-bottom: none;
        }

        .comment-content {
          flex: 1;

          .comment-header {
            margin-bottom: 8px;

            .comment-author {
              font-weight: 600;
              color: #303133;
              margin-right: 12px;
            }

            .comment-time {
              color: #909399;
              font-size: 12px;
            }
          }

          .comment-text {
            color: #606266;
            line-height: 1.6;
          }

          .comment-actions {
            margin-top: 8px;

            .reply-btn {
              color: #909399;
              cursor: pointer;
              font-size: 13px;
              display: inline-flex;
              align-items: center;
              gap: 4px;

              &:hover {
                color: #409EFF;
              }
            }
          }

          .reply-form {
            margin-top: 12px;
            padding: 12px;
            background: #f5f7fa;
            border-radius: 4px;

            .reply-form-actions {
              margin-top: 8px;
              text-align: right;
            }
          }

          .replies {
            margin-top: 12px;
            padding-left: 12px;
            border-left: 2px solid #e4e7ed;

            .reply-item {
              display: flex;
              gap: 8px;
              padding: 8px 0;

              .reply-content {
                flex: 1;
                font-size: 14px;
                color: #606266;
                line-height: 1.6;

                .reply-author {
                  color: #409EFF;
                  font-weight: 500;
                }

                .reply-to {
                  color: #909399;
                }

                .reply-time {
                  color: #c0c4cc;
                  font-size: 12px;
                  margin-left: 8px;
                }
              }
            }
          }
        }
      }
    }

    .empty-comments {
      text-align: center;
      padding: 40px;
      color: #909399;
    }
  }
}

.loading {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
}

@media (max-width: 768px) {
  .article-detail {
    .article-title {
      font-size: 24px;
    }

    .article-meta {
      gap: 12px;
      font-size: 13px;
    }
  }
}
</style>
