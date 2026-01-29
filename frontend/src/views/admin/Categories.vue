<template>
  <div class="categories-management">
    <div class="page-header">
      <h2>分类管理</h2>
      <el-button type="primary" @click="showDialog">
        <el-icon><Plus /></el-icon>
        添加分类
      </el-button>
    </div>

    <div class="category-table card">
      <el-table :data="categories" v-loading="loading" row-key="id">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" size="small" @click="editCategory(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="deleteCategory(row)">删除</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '添加分类'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入分类描述" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">显示</el-radio>
            <el-radio :label="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

dayjs.locale('zh-cn')

const loading = ref(false)
const saving = ref(false)
const categories = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  description: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在1-20个字符之间', trigger: 'blur' }
  ]
}

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

async function loadCategories() {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    categories.value = [
      { id: 1, name: '新闻资讯', description: '最新的新闻资讯内容', sort: 1, status: 1, createdAt: new Date() },
      { id: 2, name: '技术分享', description: '技术文章和教程分享', sort: 2, status: 1, createdAt: new Date() },
      { id: 3, name: '生活感悟', description: '生活随笔和感悟', sort: 3, status: 1, createdAt: new Date() },
      { id: 4, name: '其他', description: '其他类型内容', sort: 4, status: 1, createdAt: new Date() }
    ]
  } catch (error) {
    ElMessage.error('加载分类列表失败')
  } finally {
    loading.value = false
  }
}

function showDialog() {
  isEdit.value = false
  form.id = null
  form.name = ''
  form.description = ''
  form.sort = 0
  form.status = 1
  dialogVisible.value = true
}

function editCategory(category) {
  isEdit.value = true
  Object.assign(form, category)
  dialogVisible.value = true
}

async function saveCategory() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadCategories()
    } catch (error) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}

async function deleteCategory(category) {
  try {
    await ElMessageBox.confirm(`确定要删除分类 "${category.name}" 吗?`, '提示', {
      type: 'warning'
    })
    ElMessage.success('删除成功')
    loadCategories()
  } catch {
    ElMessage.info('已取消')
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped lang="scss">
.categories-management {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }
  }

  .category-table {
    .el-table {
      :deep(th) {
        background: #fafafa;
      }
    }
  }
}
</style>
