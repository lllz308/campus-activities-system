<script setup>
import { onMounted, ref } from 'vue'
import { CalendarClock, MapPin, RefreshCw, Trash2 } from '@lucide/vue'
import { api } from '../api'
import EmptyState from '../components/EmptyState.vue'

const registrations = ref([])
const loading = ref(true)
const error = ref('')
const cancelling = ref(null)

function formatDate(value) {
  return new Intl.DateTimeFormat('zh-CN', { month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' }).format(new Date(value))
}

async function load() {
  loading.value = true
  try {
    registrations.value = await api('/api/student/registrations')
  } catch (exception) {
    error.value = exception.message
  } finally {
    loading.value = false
  }
}

async function cancel(item) {
  if (!window.confirm(`确定取消“${item.activityTitle}”的报名吗？`)) return
  cancelling.value = item.activityId
  try {
    await api(`/api/student/activities/${item.activityId}/registrations`, { method: 'DELETE' })
    registrations.value = registrations.value.filter((record) => record.registrationId !== item.registrationId)
  } catch (exception) {
    error.value = exception.message
  } finally {
    cancelling.value = null
  }
}

onMounted(load)
</script>

<template>
  <section class="workspace page-width">
    <div class="workspace-heading"><div><span class="eyebrow dark">学生中心</span><h1>我的报名</h1><p>集中查看并管理你参加的校园活动。</p></div><button class="icon-button" title="刷新" @click="load"><RefreshCw :size="19" /></button></div>
    <div v-if="error" class="alert alert-error">{{ error }}</div>
    <div v-if="loading" class="detail-loading">正在加载报名记录...</div>
    <EmptyState v-else-if="!registrations.length" title="还没有报名记录" description="去活动大厅看看最近有哪些活动。"><RouterLink class="button button-primary button-small" to="/">浏览活动</RouterLink></EmptyState>
    <div v-else class="registration-list">
      <article v-for="item in registrations" :key="item.registrationId" class="registration-row">
        <div class="registration-icon"><CalendarClock :size="22" /></div>
        <div class="registration-copy"><strong>{{ item.activityTitle }}</strong><span><CalendarClock :size="15" />{{ formatDate(item.startTime) }}</span><span><MapPin :size="15" />活动详情中查看地点</span></div>
        <span class="registration-time">报名于 {{ formatDate(item.registeredAt) }}</span>
        <RouterLink class="button button-secondary button-small" :to="`/activities/${item.activityId}`">详情</RouterLink>
        <button class="icon-button danger" title="取消报名" :disabled="cancelling === item.activityId" @click="cancel(item)"><Trash2 :size="18" /></button>
      </article>
    </div>
  </section>
</template>
