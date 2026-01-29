<template>
  <div class="friend-links-management">
    <div class="page-header">
      <h2>友情链接</h2>
      <el-button type="primary" @click="showDialog">
        <el-icon><Plus /></el-icon>
        添加友链
      </el-button>
    </div>

    <div class="link-table card">
      <el-table :data="friendLinks" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="网站名称" />
        <el-table-column prop="url" label="网站地址" show-overflow-tooltip />
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" size="small" @click="editLink(row)">编辑</el-button>
              <el-switch
                v-model="row.status"
                :active-value="1"
                :inactive-value="0"
                @change="(value) => handleStatusChange(row.id, value)"
              />
              <el-button type="danger" size="small" @click="deleteLink(row)">删除</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑友链' : '添加友链'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入网站名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="地址" prop="url">
          <el-input v-model="form.url" placeholder="请输入网站地址" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入网站描述" maxlength="200" show-word-limit />
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
        <el-button type="primary" @click="saveLink" :loading="saving">保存</el-button>
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
import { getAllFriendLinks, createFriendLink, updateFriendLink, deleteFriendLink } from '@/api/friendLink'

dayjs.locale('zh-cn')

const loading = ref(false)
const saving = ref(false)
const friendLinks = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  url: '',
  description: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [
    { required: true, message: '请输入网站名称', trigger: 'blur' }
  ],
  url: [
    { required: true, message: '请输入网站地址', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
  ]
}

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

async function loadFriendLinks() {
  loading.value = true
  try {
    const res = await getAllFriendLinks()
    friendLinks.value = res.data
    total.value = res.data.length
  } catch (error) {
    ElMessage.error('加载友链列表失败')
  } finally {
    loading.value = false
  }
}

const total = ref(0)

function showDialog() {
  isEdit.value = false
  form.id = null
  form.name = ''
  form.url = ''
  form.description = ''
  form.sort = 0
  form.status = 1
  dialogVisible.value = true
}

function editLink(link) {
  isEdit.value = true
  Object.assign(form, link)
  dialogVisible.value = true
}

async function saveLink() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      if (isEdit.value) {
        await updateFriendLink(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await createFriendLink(form)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadFriendLinks()
    } catch (error) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}

async function deleteLink(link) {
  try {
    await ElMessageBox.confirm(`确定要删除友链 "${link.name}" 吗?`, '提示', {
      type: 'warning'
    })
    await deleteFriendLink(link.id)
    ElMessage.success('删除成功')
    loadFriendLinks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    } else {
      ElMessage.info('已取消')
    }
  }
}

async function handleStatusChange(id, status) {
  try {
    await updateFriendLink(id, { status })
    ElMessage.success(status === 1 ? '已设为显示' : '已设为隐藏')
  } catch (error) {
    ElMessage.error('状态更新失败')
    // 恢复原状态
    loadFriendLinks()
  }
}

onMounted(() => {
  loadFriendLinks()
})
</script>

<style scoped lang="scss">
.friend-links-management {
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

  .link-table {
    .el-table {
      :deep(th) {
        background: #fafafa;
      }
    }
  }
}
</style>
