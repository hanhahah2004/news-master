<template>
  <div class="not-found">
    <div class="container">
      <div class="content">
        <h1>404</h1>
        <p class="message">页面不存在</p>
        <p class="tip">您访问的页面不存在或已被删除</p>
        <div class="actions">
          <router-link :to="{ name: 'Home' }">
            <el-button type="primary">返回首页</el-button>
          </router-link>
          <el-button @click="goBack">返回上一页</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
function goBack() {
  window.history.back()
}
</script>

<style scoped lang="scss">
.not-found {
  min-height: calc(100vh - 160px);
  display: flex;
  align-items: center;
  justify-content: center;

  .content {
    text-align: center;

    h1 {
      font-size: 120px;
      font-weight: 700;
      color: #409EFF;
      margin-bottom: 16px;
      line-height: 1;
    }

    .message {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
    }

    .tip {
      color: #909399;
      margin-bottom: 32px;
    }

    .actions {
      display: flex;
      gap: 16px;
      justify-content: center;
    }
  }
}

@media (max-width: 768px) {
  .not-found {
    .content {
      h1 {
        font-size: 80px;
      }

      .message {
        font-size: 20px;
      }
    }
  }
}
</style>
