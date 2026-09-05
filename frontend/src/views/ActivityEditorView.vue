<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Save } from '@lucide/vue'
import { api } from '../api'

const route = useRoute()
const router = useRouter()
const editing = computed(() => Boolean(route.params.id))
const loading = ref(editing.value)
const submitting = ref(false)
const error = ref('')
const form = ref({ title: '', description: '', location: '', startTime: '', endTime: '', registrationDeadline: '', capacity: 30, status: 'DRAFT' })

function toInput(value) {
  const date = new Date(value)
  const offset = date.getTimezoneOffset() * 60000
  return new Date(date - offset).toISOString().slice(0, 16)
}

async function load() {
  if (!editing.value) return
  try {
    const records = await api('/api/teacher/activities')
    const activity = records.find((item) => item.id === Number(route.params.id))
    if (!activity) throw new Error('活动不存在或无权编辑')
    form.value = {
      title: activity.title,
      description: activity.description,
      location: activity.location,
      startTime: toInput(activity.startTime),
      endTime: toInput(activity.endTime),
      registrationDeadline: toInput(activity.registrationDeadline),
      capacity: activity.capacity,
      status: activity.status,
    }
  } catch (exception) {
    error.value = exception.message
  } finally {
    loading.value = false
  }
}

async function submit() {
  submitting.value = true
  error.value = ''
  const body = {
    ...form.value,
    capacity: Number(form.value.capacity),
    startTime: `${form.value.startTime}:00`,
    endTime: `${form.value.endTime}:00`,
    registrationDeadline: `${form.value.registrationDeadline}:00`,
  }
  try {
    const path = editing.value ? `/api/teacher/activities/${route.params.id}` : '/api/teacher/activities'
    await api(path, { method: editing.value ? 'PUT' : 'POST', body: JSON.stringify(body) })
    router.push('/teacher/activities')
  } catch (exception) {
    error.value = exception.message
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="editor-page page-width">
    <button class="back-link" type="button" @click="router.back()"><ArrowLeft :size="17" />返回活动管理</button>
    <div class="editor-heading"><span class="eyebrow dark">教师工作台</span><h1>{{ editing ? '编辑活动' : '新建活动' }}</h1><p>填写活动信息，并选择保存为草稿或直接发布。</p></div>
    <div v-if="loading" class="detail-loading">正在读取活动信息...</div>
    <form v-else class="activity-form" @submit.prevent="submit">
      <div class="form-section"><h2>基本信息</h2><div class="form-grid"><label class="span-2"><span>活动名称</span><input v-model="form.title" maxlength="100" required /></label><label class="span-2"><span>活动介绍</span><textarea v-model="form.description" rows="5" maxlength="2000" required></textarea></label><label class="span-2"><span>活动地点</span><input v-model="form.location" maxlength="120" required /></label></div></div>
      <div class="form-section"><h2>时间与名额</h2><div class="form-grid"><label><span>报名截止</span><input v-model="form.registrationDeadline" type="datetime-local" required /></label><label><span>活动开始</span><input v-model="form.startTime" type="datetime-local" required /></label><label><span>活动结束</span><input v-model="form.endTime" type="datetime-local" required /></label><label><span>活动容量</span><input v-model="form.capacity" type="number" min="1" required /></label></div></div>
      <div class="form-section"><h2>发布状态</h2><div class="segmented"><label :class="{ active: form.status === 'DRAFT' }"><input v-model="form.status" type="radio" value="DRAFT" />草稿</label><label :class="{ active: form.status === 'PUBLISHED' }"><input v-model="form.status" type="radio" value="PUBLISHED" />发布</label><label v-if="editing" :class="{ active: form.status === 'CANCELLED' }"><input v-model="form.status" type="radio" value="CANCELLED" />取消活动</label></div></div>
      <div v-if="error" class="alert alert-error">{{ error }}</div>
      <div class="form-actions"><button class="button button-secondary" type="button" @click="router.back()">取消</button><button class="button button-primary" type="submit" :disabled="submitting"><Save :size="18" />{{ submitting ? '保存中...' : '保存活动' }}</button></div>
    </form>
  </section>
</template>
