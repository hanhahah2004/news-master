<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section about">
          <h3>{{ appStore.siteConfig.site_name }}</h3>
          <p>{{ appStore.siteConfig.site_description }}</p>
        </div>

        <div class="footer-section links">
          <h4>快速链接</h4>
          <ul>
            <li><router-link :to="{ name: 'Home' }">首页</router-link></li>
            <li><router-link :to="{ name: 'Search' }">搜索</router-link></li>
          </ul>
        </div>

        <div class="footer-section categories">
          <h4>文章分类</h4>
          <ul>
            <li v-for="cat in appStore.categories" :key="cat.id">
              <router-link :to="{ name: 'Category', params: { id: cat.id } }">
                {{ cat.name }}
              </router-link>
            </li>
          </ul>
        </div>

        <div class="footer-section contact">
          <h4>联系我们</h4>
          <p>如有建议或合作，请联系我们</p>
        </div>

        <div class="footer-section friend-links">
          <h4>友情链接</h4>
          <ul>
            <li v-for="link in appStore.friendLinks" :key="link.id">
              <a :href="link.url" target="_blank" rel="noopener noreferrer">
                {{ link.name }}
              </a>
            </li>
          </ul>
        </div>
      </div>

      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} {{ appStore.siteConfig.site_name }}. All rights reserved.</p>
        <p v-if="appStore.siteConfig.icp_number">
          <a :href="'https://beian.miit.gov.cn'" target="_blank">{{ appStore.siteConfig.icp_number }}</a>
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()
const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped lang="scss">
.footer {
  background: #303133;
  color: #909399;
  margin-top: auto;

  .footer-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 40px;
    padding: 40px 0;
  }

  .footer-section {
    h3 {
      font-size: 18px;
      font-weight: 600;
      color: #fff;
      margin-bottom: 16px;
    }

    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #fff;
      margin-bottom: 16px;
    }

    p {
      font-size: 14px;
      line-height: 1.8;
    }

    ul {
      list-style: none;

      li {
        margin-bottom: 8px;

        a {
          color: #909399;
          text-decoration: none;
          transition: color 0.3s;

          &:hover {
            color: #409EFF;
          }
        }
      }
    }
  }

  .footer-bottom {
    border-top: 1px solid #414243;
    padding: 20px 0;
    text-align: center;
    font-size: 14px;

    p {
      margin-bottom: 4px;

      a {
        color: #909399;

        &:hover {
          color: #409EFF;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .footer {
    .footer-content {
      grid-template-columns: 1fr 1fr;
    }
  }
}

@media (max-width: 480px) {
  .footer {
    .footer-content {
      grid-template-columns: 1fr;
    }
  }
}
</style>
