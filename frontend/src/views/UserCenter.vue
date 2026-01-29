<template>
  <div class="user-center">
    <div class="container">
      <div class="user-header card">
        <div class="user-info">
          <el-avatar :src="user.avatar" :size="80">
            {{ user.nickname?.charAt(0) }}
          </el-avatar>
          <div class="user-details">
            <h2 class="user-name">{{ user.nickname }}</h2>
            <p class="user-username">@{{ user.username }}</p>
            <p class="user-bio" v-if="user.bio">{{ user.bio }}</p>
          </div>
        </div>
        <div class="user-stats">
          <div class="stat-item">
            <span class="stat-value">{{ user.articleCount || 0 }}</span>
            <span class="stat-label">文章</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ user.commentCount || 0 }}</span>
            <span class="stat-label">评论</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ user.fansCount || 0 }}</span>
            <span class="stat-label">粉丝</span>
          </div>
        </div>
      </div>

      <div class="user-menu card">
        <el-menu :default-active="activeMenu" mode="horizontal" @select="handleMenuSelect">
          <el-menu-item index="profile">
            <el-icon><User /></el-icon>
            个人资料
          </el-menu-item>
          <el-menu-item index="articles">
            <el-icon><Document /></el-icon>
            我的文章
          </el-menu-item>
          <el-menu-item index="comments">
            <el-icon><ChatDotRound /></el-icon>
            我的评论
          </el-menu-item>
          <el-menu-item index="settings">
            <el-icon><Setting /></el-icon>
            账号设置
          </el-menu-item>
        </el-menu>
      </div>

      <div class="user-content">
        <div v-if="activeMenu === 'profile'" class="profile-section card">
          <h3 class="section-title">个人资料</h3>
          <el-form :model="profileForm" label-width="100px" :rules="profileRules" ref="profileFormRef">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" />
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input v-model="profileForm.bio" type="textarea" :rows="3" maxlength="200" show-word-limit />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="saving">保存</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-if="activeMenu === 'articles'" class="articles-section">
          <div class="section-header card">
            <h3 class="section-title">我的文章</h3>
            <router-link :to="{ name: 'CreateArticle' }">
              <el-button type="primary">
                <el-icon><Plus /></el-icon>
                发布文章
              </el-button>
            </router-link>
          </div>
          <div class="article-list" v-if="myArticles.length > 0">
            <div class="article-item card" v-for="article in myArticles" :key="article.id">
              <div class="article-info">
                <h4 class="article-title">
                  <router-link :to="{ name: 'ArticleDetail', params: { id: article.id } }">
                    {{ article.title }}
                  </router-link>
                </h4>
                <p class="article-summary">{{ article.summary || '暂无摘要...' }}</p>
                <div class="article-meta">
                  <span>{{ formatDate(article.createdAt) }}</span>
                  <span class="status-tag" :class="getStatusClass(article.status)">
                    {{ getStatusText(article.status) }}
                  </span>
                </div>
              </div>
              <div class="article-actions">
                <el-button-group>
                  <el-button type="primary" size="small" @click="editArticle(article.id)">
                    编辑
                  </el-button>
                  <el-button type="danger" size="small" @click="deleteArticle(article.id)">
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
        </div>

        <div v-if="activeMenu === 'comments'" class="comments-section card">
          <h3 class="section-title">我的评论</h3>
          <div class="comment-list" v-if="myComments.length > 0">
            <div class="comment-item" v-for="comment in myComments" :key="comment.id">
              <div class="comment-content">
                <p>{{ comment.content }}</p>
                <div class="comment-meta">
                  评论了
                  <router-link :to="{ name: 'ArticleDetail', params: { id: comment.articleId } }">
                    {{ comment.articleTitle }}
                  </router-link>
                  · {{ formatDate(comment.createdAt) }}
                </div>
              </div>
            </div>
          </div>
          <div class="empty-state" v-else>
            <p>还没有发表过评论</p>
          </div>
        </div>

        <div v-if="activeMenu === 'settings'" class="settings-section card">
          <h3 class="section-title">账号设置</h3>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="修改密码" name="password">
              <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
                <el-form-item label="当前密码" prop="currentPassword">
                  <el-input v-model="passwordForm.currentPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="changePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="绑定手机" name="phone">
              <el-form label-width="100px">
                <el-form-item label="手机号码">
                  <el-input v-model="phoneForm.phone" placeholder="请输入手机号码" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary">绑定手机</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Document, ChatDotRound, Setting, Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import { useUserStore } from '@/stores/user'
import { updateUser } from '@/api/user'
import { getArticlesByAuthor, deleteArticle as deleteArticleApi } from '@/api/article'
import { getCommentsByUser } from '@/api/comment'

dayjs.locale('zh-cn')

const router = useRouter()
const userStore = useUserStore()

const activeMenu = ref('profile')
const activeTab = ref('password')
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const saving = ref(false)

const user = ref({
  nickname: '',
  username: '',
  email: '',
  bio: '',
  avatar: ''
})

const profileForm = reactive({
  nickname: '',
  email: '',
  bio: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const phoneForm = reactive({
  phone: ''
})

const myArticles = ref([])
const myComments = ref([])

const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在2-20个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ]
}

const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function getStatusClass(status) {
  const map = {
    0: 'draft',
    1: 'published',
    2: 'deleted'
  }
  return map[status] || ''
}

function getStatusText(status) {
  const map = {
    0: '草稿',
    1: '已发布',
    2: '已删除'
  }
  return map[status] || '未知'
}

function handleMenuSelect(index) {
  activeMenu.value = index
}

async function loadUserInfo() {
  if (!userStore.user) {
    await userStore.fetchUserInfo()
  }
  if (userStore.user) {
    user.value = { ...userStore.user }
    profileForm.nickname = user.value.nickname
    profileForm.email = user.value.email
    profileForm.bio = user.value.bio || ''
  }
}

async function saveProfile() {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      await updateUser(profileForm)
      ElMessage.success('保存成功')
      userStore.fetchUserInfo()
      loadUserInfo()
    } catch (error) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}

async function loadMyArticles() {
  try {
    if (!userStore.user) {
      await userStore.fetchUserInfo()
    }
    if (userStore.user) {
      const res = await getArticlesByAuthor(userStore.user.id)
      myArticles.value = res.data.list
    }
  } catch (error) {
    console.error('加载文章列表失败:', error)
  }
}

async function loadMyComments() {
  try {
    if (!userStore.user) {
      await userStore.fetchUserInfo()
    }
    if (userStore.user) {
      const res = await getCommentsByUser()
      myComments.value = res.data
    }
  } catch (error) {
    console.error('加载评论列表失败:', error)
  }
}

function editArticle(id) {
  router.push({ name: 'EditArticle', params: { id } })
}

async function deleteArticle(id) {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteArticleApi(id)
    ElMessage.success('删除成功')
    loadMyArticles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

async function changePassword() {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      ElMessage.info('密码修改功能开发中')
    } catch (error) {
      ElMessage.error('修改失败')
    }
  })
}

onMounted(() => {
  loadUserInfo()
  loadMyArticles()
  loadMyComments()
})
</script>

<style scoped lang="scss">
.user-center {
  .user-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 20px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 20px;

      .user-details {
        .user-name {
          font-size: 24px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 4px;
        }

        .user-username {
          color: #909399;
          margin-bottom: 8px;
        }

        .user-bio {
          color: #606266;
          font-size: 14px;
        }
      }
    }

    .user-stats {
      display: flex;
      gap: 40px;

      .stat-item {
        text-align: center;

        .stat-value {
          display: block;
          font-size: 24px;
          font-weight: 600;
          color: #303133;
        }

        .stat-label {
          color: #909399;
          font-size: 14px;
        }
      }
    }
  }

  .user-menu {
    padding: 0;
    margin-bottom: 20px;

    .el-menu {
      border: none;
    }
  }

  .user-content {
    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 20px;
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
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

        &:last-child {
          border-radius: 0 0 8px 8px;
        }

        .article-info {
          .article-title {
            font-size: 16px;
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
            gap: 12px;
            color: #909399;
            font-size: 13px;

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
      }
    }

    .comment-list {
      .comment-item {
        padding: 16px 0;
        border-bottom: 1px solid #ebeef5;

        &:last-child {
          border-bottom: none;
        }

        .comment-content {
          p {
            color: #303133;
            margin-bottom: 8px;
            line-height: 1.6;
          }

          .comment-meta {
            font-size: 13px;
            color: #909399;

            a {
              color: #409EFF;
              margin: 0 4px;
            }
          }
        }
      }
    }

    .empty-state {
      text-align: center;
      padding: 40px;

      .el-icon {
        font-size: 48px;
        color: #c0c4cc;
        margin-bottom: 16px;
      }

      p {
        color: #909399;
        margin-bottom: 16px;
      }
    }
  }
}

@media (max-width: 768px) {
  .user-center {
    .user-header {
      flex-direction: column;
      align-items: flex-start;

      .user-stats {
        width: 100%;
        justify-content: space-around;
      }
    }

    .article-item {
      flex-direction: column;
      align-items: flex-start !important;
      gap: 12px;
    }
  }
}
</style>
