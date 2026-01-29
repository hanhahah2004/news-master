<template>
  <header class="header">
    <div class="container">
      <div class="header-content">
        <router-link :to="{ name: 'Home' }" class="logo">
          <el-icon><Reading /></el-icon>
          <span>{{ appStore.siteConfig.site_name }}</span>
        </router-link>

        <nav class="nav-menu">
          <router-link :to="{ name: 'Home' }" class="nav-item">首页</router-link>
          <div class="category-dropdown" @mouseenter="showCategories" @mouseleave="hideCategories">
            <span class="nav-item">
              分类 <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </span>
            <div class="dropdown-menu" v-show="categoryMenuVisible">
              <router-link
                v-for="cat in appStore.categories"
                :key="cat.id"
                :to="{ name: 'Category', params: { id: cat.id } }"
                class="dropdown-item"
              >
                <span class="category-name">{{ cat.name }}</span>
                <span class="category-count">{{ cat.articleCount || cat.count || cat.articles || 0 }}</span>
              </router-link>
            </div>
          </div>
        </nav>

        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文章..."
            size="small"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <div class="header-actions">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click" @command="handleCommand">
              <div class="user-info">
                <el-avatar :src="userStore.user?.avatar" :size="32">
                  {{ userStore.user?.nickname?.charAt(0) }}
                </el-avatar>
                <span class="username">{{ userStore.user?.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="myArticles">
                    <el-icon><Document /></el-icon>
                    我的文章
                  </el-dropdown-item>
                  <el-dropdown-item command="createArticle">
                    <el-icon><Plus /></el-icon>
                    发布文章
                  </el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" command="admin">
                    <el-icon><Setting /></el-icon>
                    管理后台
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><Switch /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link :to="{ name: 'Login' }">
              <el-button text>登录</el-button>
            </router-link>
            <router-link :to="{ name: 'Register' }">
              <el-button type="primary">注册</el-button>
            </router-link>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Reading, ArrowDown, Search, User, Document, Plus, Setting, Switch } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const searchKeyword = ref('')
const categoryMenuVisible = ref(false)

function showCategories() {
  categoryMenuVisible.value = true
}

function hideCategories() {
  categoryMenuVisible.value = false
}

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ name: 'Search', query: { keyword: searchKeyword.value } })
  }
}

function handleCommand(command) {
  switch (command) {
    case 'profile':
      router.push({ name: 'UserCenter' })
      break
    case 'myArticles':
      router.push({ name: 'MyArticles' })
      break
    case 'createArticle':
      router.push({ name: 'CreateArticle' })
      break
    case 'admin':
      router.push({ name: 'AdminDashboard' })
      break
    case 'logout':
      userStore.logoutAction()
      break
  }
}
</script>

<style scoped lang="scss">
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;

  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 64px;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: 700;
    color: #303133;
    text-decoration: none;

    .el-icon {
      font-size: 28px;
      color: #409EFF;
    }
  }

  .nav-menu {
    display: flex;
    align-items: center;
    gap: 8px;

    .nav-item {
      padding: 8px 16px;
      color: #606266;
      text-decoration: none;
      border-radius: 4px;
      transition: all 0.3s;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 4px;

      .arrow-icon {
        font-size: 12px;
      }

      &:hover {
        color: #409EFF;
        background: #ecf5ff;
      }

      &.router-link-active {
        color: #409EFF;
        background: #ecf5ff;
      }
    }

    .category-dropdown {
      position: relative;

      .dropdown-menu {
          position: absolute;
          top: 100%;
          left: 50%;
          transform: translateX(-50%);
          background: #fff;
          border-radius: 8px;
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
          padding: 8px 0;
          min-width: 180px;
          display: flex;
          flex-direction: column;

          .dropdown-item {
            padding: 10px 20px;
            color: #606266;
            text-decoration: none;
            transition: all 0.3s;
            display: flex;
            justify-content: space-between;
            align-items: center;

            &:hover {
              background: #ecf5ff;
              color: #409EFF;
            }

            .category-name {
              flex: 1;
            }

            .category-count {
              font-size: 12px;
              color: #909399;
              background: #f4f4f5;
              padding: 2px 8px;
              border-radius: 10px;
            }
          }
        }
    }
  }

  .search-box {
    width: 240px;

    :deep(.el-input__wrapper) {
      border-radius: 20px;
    }
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 12px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 4px;
      transition: background 0.3s;

      &:hover {
        background: #f5f7fa;
      }

      .username {
        color: #606266;
        font-size: 14px;
        max-width: 100px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .el-icon {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

@media (max-width: 992px) {
  .header {
    .search-box {
      display: none;
    }
  }
}

@media (max-width: 768px) {
  .header {
    .nav-menu {
      display: none;
    }

    .header-actions {
      .user-info {
        .username {
          display: none;
        }
      }
    }
  }
}
</style>
