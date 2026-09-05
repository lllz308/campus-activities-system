<script setup>
import { onMounted, ref } from 'vue'
import { Edit3, Plus, RefreshCw, Trash2 } from '@lucide/vue'
import { api } from '../api'
import EmptyState from '../components/EmptyState.vue'
import StatusBadge from '../components/StatusBadge.vue'

const activities = ref([])
const loading = ref(true)
const error = ref('')

function formatDate(value) {
  return new Intl.DateTimeFormat('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }).format(new Date(value))
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    activities.value = await api('/api/teacher/activities')
  } catch (exception) {
    error.value = exception.message
  } finally {
    loading.value = false
  }
}

async function remove(activity) {
  if (!window.confirm(`确定删除“${activity.title}”吗？此操作不可撤销。`)) return
  try {
    await api(`/api/teacher/activities/${activity.id}`, { method: 'DELETE' })
    activities.value = activities.value.filter((item) => item.id !== activity.id)
  } catch (exception) {
    error.value = exception.message
  }
}

onMounted(load)
</script>

<template>
  <section class="workspace page-width">
    <div class="workspace-heading">
      <div><span class="eyebrow dark">教师工作台</span><h1>活动管理</h1><p>创建、发布并维护你负责的校园活动。</p></div>
      <div class="heading-actions"><button class="icon-button" title="刷新" @click="load"><RefreshCw :size="19" /></button><RouterLink class="button button-primary" to="/teacher/activities/new"><Plus :size="18" />新建活动</RouterLink></div>
    </div>
    <div v-if="error" class="alert alert-error">{{ error }}</div>
    <div v-if="loading" class="detail-loading">正在加载活动...</div>
    <EmptyState v-else-if="!activities.length" title="还没有创建活动" description="创建第一场活动并选择何时发布。"><RouterLink class="button button-primary button-small" to="/teacher/activities/new"><Plus :size="17" />新建活动</RouterLink></EmptyState>
    <div v-else class="table-wrap">
      <table class="data-table">
        <thead><tr><th>活动</th><th>状态</th><th>开始时间</th><th>报名人数</th><th class="align-right">操作</th></tr></thead>
        <tbody>
          <tr v-for="activity in activities" :key="activity.id">
            <td><strong>{{ activity.title }}</strong><span>{{ activity.location }}</span></td>
            <td><StatusBadge :status="activity.status" /></td>
            <td>{{ formatDate(activity.startTime) }}</td>
            <td>{{ activity.registeredCount }} / {{ activity.capacity }}</td>
            <td class="table-actions"><RouterLink class="icon-button" :to="`/teacher/activities/${activity.id}/edit`" title="编辑"><Edit3 :size="18" /></RouterLink><button class="icon-button danger" title="删除" @click="remove(activity)"><Trash2 :size="18" /></button></td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
