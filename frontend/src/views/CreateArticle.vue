<template>
  <div class="create-article">
    <div class="container">
      <div class="page-header">
        <h2>发布文章</h2>
      </div>

      <div class="editor-container card">
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
          <el-form-item label="标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入文章标题" size="large" maxlength="100" show-word-limit />
          </el-form-item>

          <el-form-item label="分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择分类" size="large" style="width: 200px">
              <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
            </el-select>
          </el-form-item>

          <el-form-item label="封面图">
            <el-upload
              class="cover-uploader"
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="handleCoverSuccess"
              :before-upload="beforeCoverUpload"
            >
              <img v-if="form.coverImage" :src="form.coverImage" class="cover-image" />
              <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">建议尺寸: 800x400，支持 jpg、png 格式</div>
          </el-form-item>

          <el-form-item label="摘要">
            <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="可选填写文章摘要" maxlength="200" show-word-limit />
          </el-form-item>

          <el-form-item label="内容" prop="content">
            <div class="editor-wrapper">
              <div class="editor-toolbar">
                <el-button-group>
                  <el-button size="small" @click="insertMarkdown('**', '**')"><b>B</b></el-button>
                  <el-button size="small" @click="insertMarkdown('*', '*')"><i>I</i></el-button>
                  <el-button size="small" @click="insertMarkdown('# ' )">H1</el-button>
                  <el-button size="small" @click="insertMarkdown('## ' )">H2</el-button>
                  <el-button size="small" @click="insertMarkdown('`', '`')"><code></code></el-button>
                  <el-button size="small" @click="insertMarkdown('```\n', '\n```')">{ }</el-button>
                  <el-button size="small" @click="insertMarkdown('[' , '](url)')">链接</el-button>
                  <el-button size="small" @click="insertMarkdown('![' , '](image)')">图片</el-button>
                </el-button-group>
              </div>
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="15"
                placeholder="请输入文章内容，支持 Markdown 语法"
                class="content-editor"
              />
            </div>
          </el-form-item>

          <el-form-item label="标签">
            <el-tag v-for="tag in form.tags" :key="tag" closable @close="removeTag(tag)" style="margin-right: 8px">
              {{ tag }}
            </el-tag>
            <el-input
              v-if="inputTagVisible"
              ref="tagInputRef"
              v-model="inputTagValue"
              size="small"
              style="width: 100px"
              @keyup.enter="handleTagConfirm"
              @blur="handleTagConfirm"
            />
            <el-button v-else size="small" @click="showTagInput">+ 添加标签</el-button>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSave" :loading="saving">保存草稿</el-button>
            <el-button type="success" @click="handlePublish" :loading="publishing">发布文章</el-button>
            <el-button @click="goBack">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { createArticle, publishArticle } from '@/api/article'
import { getCategoryList } from '@/api/category'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const tagInputRef = ref(null)
const saving = ref(false)
const publishing = ref(false)
const categories = ref([])
const inputTagVisible = ref(false)
const inputTagValue = ref('')

const uploadUrl = '/api/upload/image'

const form = reactive({
  title: '',
  categoryId: null,
  coverImage: '',
  summary: '',
  content: '',
  tags: []
})

const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在2-100个字符之间', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' },
    { min: 10, message: '内容至少需要10个字符', trigger: 'blur' }
  ]
}

async function loadCategories() {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

function handleCoverSuccess(res) {
  if (res.code === 200) {
    form.coverImage = res.data.url
  } else {
    ElMessage.error('上传失败')
  }
}

function beforeCoverUpload(file) {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

function insertMarkdown(prefix, suffix = '') {
  const textarea = document.querySelector('.content-editor textarea')
  if (textarea) {
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    const text = form.content
    const before = text.substring(0, start)
    const selected = text.substring(start, end)
    const after = text.substring(end)
    form.content = before + prefix + selected + suffix + after
    nextTick(() => {
      textarea.focus()
      textarea.setSelectionRange(start + prefix.length, start + prefix.length + selected.length)
    })
  }
}

function removeTag(tag) {
  form.tags = form.tags.filter(t => t !== tag)
}

function showTagInput() {
  inputTagVisible.value = true
  nextTick(() => {
    tagInputRef.value?.focus()
  })
}

function handleTagConfirm() {
  if (inputTagValue.value) {
    if (!form.tags.includes(inputTagValue.value)) {
      form.tags.push(inputTagValue.value)
    }
  }
  inputTagVisible.value = false
  inputTagValue.value = ''
}

async function handleSave() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true
    try {
      await createArticle({ ...form, status: 0 })
      ElMessage.success('保存草稿成功')
      router.push({ name: 'MyArticles' })
    } catch (error) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}

async function handlePublish() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    publishing.value = true
    try {
      await createArticle({ ...form, status: 1 })
      ElMessage.success('发布成功')
      router.push({ name: 'MyArticles' })
    } catch (error) {
      ElMessage.error('发布失败')
    } finally {
      publishing.value = false
    }
  })
}

function goBack() {
  router.back()
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped lang="scss">
.create-article {
  .page-header {
    margin-bottom: 20px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }
  }

  .editor-container {
    padding: 30px;
  }

  .cover-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      width: 200px;
      height: 120px;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        border-color: #409EFF;
      }
    }

    .cover-uploader-icon {
      font-size: 28px;
      color: #8c939d;
    }

    .cover-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 8px;
  }

  .editor-wrapper {
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    overflow: hidden;

    .editor-toolbar {
      padding: 8px;
      background: #fafafa;
      border-bottom: 1px solid #dcdfe6;
    }

    .content-editor {
      :deep(textarea) {
        border: none;
        resize: vertical;
        font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
        font-size: 14px;
        line-height: 1.6;
      }
    }
  }
}
</style>
