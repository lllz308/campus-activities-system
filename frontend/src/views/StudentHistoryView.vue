<script setup>
import { onMounted, ref } from 'vue'
import { CalendarClock, History, MapPin, RefreshCw } from '@lucide/vue'
import { api } from '../api'
import EmptyState from '../components/EmptyState.vue'

const records = ref([])
const loading = ref(true)
const error = ref('')

function formatDate(value) {
  return new Intl.DateTimeFormat('zh-CN', { month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' }).format(new Date(value))
}

function formatRange(start, end) {
  return `${formatDate(start)} - ${new Intl.DateTimeFormat('zh-CN', { hour: '2-digit', minute: '2-digit' }).format(new Date(end))}`
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    records.value = await api('/api/student/registrations/history')
  } catch (exception) {
    error.value = exception.message
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="workspace page-width">
    <div class="workspace-heading">
      <div>
        <span class="eyebrow dark">学生中心</span>
        <h1>历史参加记录</h1>
        <p>回顾你已经结束的校园活动经历。</p>
      </div>
      <button class="icon-button" title="刷新" @click="load"><RefreshCw :size="19" /></button>
    </div>
    <div v-if="error" class="alert alert-error">{{ error }}</div>
    <div v-if="loading" class="detail-loading">正在加载历史记录...</div>
    <EmptyState v-else-if="!records.length" title="暂无历史参加记录" description="参加过的活动结束后会出现在这里。"><RouterLink class="button button-primary button-small" to="/">浏览活动</RouterLink></EmptyState>
    <div v-else class="registration-list">
      <article v-for="item in records" :key="item.registrationId" class="registration-row history-row">
        <div class="registration-icon"><History :size="22" /></div>
        <div class="registration-copy">
          <strong>{{ item.activityTitle }}</strong>
          <span><CalendarClock :size="15" />{{ formatRange(item.startTime, item.endTime) }}</span>
          <span><MapPin :size="15" />{{ item.location }}</span>
        </div>
        <span class="registration-time">报名于 {{ formatDate(item.registeredAt) }}</span>
        <RouterLink class="button button-secondary button-small" :to="`/activities/${item.activityId}`">详情</RouterLink>
      </article>
    </div>
  </section>
</template>
