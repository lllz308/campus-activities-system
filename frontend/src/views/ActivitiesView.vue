<script setup>
import { computed, onMounted, ref } from 'vue'
import { ArrowRight, CalendarClock, MapPin, RefreshCw, Search, Users } from '@lucide/vue'
import { api } from '../api'
import EmptyState from '../components/EmptyState.vue'

const activities = ref([])
const loading = ref(true)
const error = ref('')
const keyword = ref('')
const onlyAvailable = ref(false)

const filtered = computed(() => {
  const query = keyword.value.trim().toLowerCase()
  return activities.value.filter((activity) => {
    const matches = !query || [activity.title, activity.description, activity.location, activity.organizerName]
      .some((value) => value.toLowerCase().includes(query))
    return matches && (!onlyAvailable.value || activity.registeredCount < activity.capacity)
  })
})

const totalSeats = computed(() => activities.value.reduce((sum, item) => sum + item.capacity - item.registeredCount, 0))

function formatDate(value) {
  return new Intl.DateTimeFormat('zh-CN', {
    month: 'short', day: 'numeric', weekday: 'short', hour: '2-digit', minute: '2-digit',
  }).format(new Date(value))
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    activities.value = await api('/api/activities')
  } catch (exception) {
    error.value = exception.message
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="catalog-hero">
    <img src="/campus-hero.png" alt="学生在校园活动中心交流" />
    <div class="hero-scrim"></div>
    <div class="hero-content page-width">
      <span class="eyebrow">2026 秋季学期</span>
      <h1>校园活动</h1>
      <p>发现正在招募的志愿服务、学术分享与校园社群活动。</p>
      <div class="hero-metrics">
        <span><strong>{{ activities.length }}</strong> 场活动</span>
        <span><strong>{{ totalSeats }}</strong> 个剩余名额</span>
      </div>
    </div>
  </section>

  <section class="catalog page-width">
    <div class="section-heading">
      <div>
        <h2>近期活动</h2>
        <p>按开始时间排序，仅展示已发布且尚未结束的活动。</p>
      </div>
      <button class="icon-button" type="button" title="刷新活动" aria-label="刷新活动" @click="load">
        <RefreshCw :size="19" :class="{ spin: loading }" />
      </button>
    </div>

    <div class="filter-bar">
      <label class="search-field">
        <Search :size="18" />
        <input v-model="keyword" type="search" placeholder="搜索活动、地点或组织者" />
      </label>
      <label class="check-control">
        <input v-model="onlyAvailable" type="checkbox" />
        <span>仅看有名额</span>
      </label>
      <span class="result-count">{{ filtered.length }} 条结果</span>
    </div>

    <div v-if="error" class="alert alert-error">
      <span>{{ error }}</span>
      <button type="button" @click="load">重试</button>
    </div>

    <div v-if="loading" class="activity-grid" aria-label="正在加载">
      <div v-for="item in 3" :key="item" class="activity-card skeleton-card"></div>
    </div>

    <EmptyState
      v-else-if="!filtered.length"
      title="没有找到符合条件的活动"
      description="调整搜索条件，或稍后回来看看。"
    />

    <div v-else class="activity-grid">
      <article v-for="activity in filtered" :key="activity.id" class="activity-card">
        <div class="activity-date">
          <span>{{ new Date(activity.startTime).getMonth() + 1 }}月</span>
          <strong>{{ new Date(activity.startTime).getDate() }}</strong>
        </div>
        <div class="activity-body">
          <div class="activity-topline">
            <span class="category-label">公开活动</span>
            <span :class="['seat-state', { full: activity.registeredCount >= activity.capacity }]">
              {{ activity.registeredCount >= activity.capacity ? '名额已满' : '开放报名' }}
            </span>
          </div>
          <h3>{{ activity.title }}</h3>
          <p>{{ activity.description }}</p>
          <dl class="activity-meta">
            <div><CalendarClock :size="16" /><dt>时间</dt><dd>{{ formatDate(activity.startTime) }}</dd></div>
            <div><MapPin :size="16" /><dt>地点</dt><dd>{{ activity.location }}</dd></div>
            <div><Users :size="16" /><dt>名额</dt><dd>{{ activity.registeredCount }} / {{ activity.capacity }}</dd></div>
          </dl>
          <div class="capacity-track"><span :style="{ width: `${Math.min(100, activity.registeredCount / activity.capacity * 100)}%` }"></span></div>
          <div class="activity-footer">
            <span>组织者：{{ activity.organizerName }}</span>
            <RouterLink :to="`/activities/${activity.id}`" class="text-link">查看详情 <ArrowRight :size="16" /></RouterLink>
          </div>
        </div>
      </article>
    </div>
  </section>
</template>
