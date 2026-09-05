<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, CalendarClock, CheckCircle2, Clock3, MapPin, UserRound, Users } from '@lucide/vue'
import { api } from '../api'
import { isStudent, session } from '../session'

const route = useRoute()
const router = useRouter()
const activity = ref(null)
const loading = ref(true)
const submitting = ref(false)
const message = ref('')
const error = ref('')

const isFull = computed(() => activity.value && activity.value.registeredCount >= activity.value.capacity)
const isClosed = computed(() => activity.value && new Date(activity.value.registrationDeadline) <= new Date())

function formatDate(value) {
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric', month: 'long', day: 'numeric', weekday: 'short', hour: '2-digit', minute: '2-digit',
  }).format(new Date(value))
}

async function load() {
  try {
    activity.value = await api(`/api/activities/${route.params.id}`)
  } catch (exception) {
    error.value = exception.message
  } finally {
    loading.value = false
  }
}

async function register() {
  if (!session.user) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  submitting.value = true
  error.value = ''
  try {
    await api(`/api/student/activities/${activity.value.id}/registrations`, { method: 'POST' })
    message.value = '报名成功，可在“我的报名”中查看。'
    activity.value.registeredCount += 1
  } catch (exception) {
    error.value = exception.message
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="detail-page page-width">
    <button class="back-link" type="button" @click="router.back()"><ArrowLeft :size="17" />返回</button>
    <div v-if="loading" class="detail-loading">正在加载活动信息...</div>
    <div v-else-if="error && !activity" class="alert alert-error">{{ error }}</div>
    <template v-else>
      <div class="detail-layout">
        <article class="detail-main">
          <span class="eyebrow dark">校园公开活动</span>
          <h1>{{ activity.title }}</h1>
          <p class="detail-lead">{{ activity.description }}</p>
          <div class="detail-facts">
            <div><CalendarClock :size="21" /><span><small>活动开始</small>{{ formatDate(activity.startTime) }}</span></div>
            <div><Clock3 :size="21" /><span><small>活动结束</small>{{ formatDate(activity.endTime) }}</span></div>
            <div><MapPin :size="21" /><span><small>活动地点</small>{{ activity.location }}</span></div>
            <div><UserRound :size="21" /><span><small>组织教师</small>{{ activity.organizerName }}</span></div>
          </div>
        </article>

        <aside class="registration-panel">
          <h2>报名信息</h2>
          <div class="seat-numbers"><strong>{{ activity.registeredCount }}</strong><span>/ {{ activity.capacity }} 人</span></div>
          <div class="capacity-track large"><span :style="{ width: `${Math.min(100, activity.registeredCount / activity.capacity * 100)}%` }"></span></div>
          <p><Users :size="16" />还剩 {{ Math.max(0, activity.capacity - activity.registeredCount) }} 个名额</p>
          <p><Clock3 :size="16" />截止 {{ formatDate(activity.registrationDeadline) }}</p>
          <div v-if="message" class="inline-success"><CheckCircle2 :size="18" />{{ message }}</div>
          <div v-if="error" class="inline-error">{{ error }}</div>
          <button
            class="button button-primary button-block"
            type="button"
            :disabled="submitting || isFull || isClosed || Boolean(message) || (session.user && !isStudent)"
            @click="register"
          >
            {{ message ? '已报名' : isFull ? '名额已满' : isClosed ? '报名已截止' : submitting ? '提交中...' : session.user ? '立即报名' : '登录后报名' }}
          </button>
          <small v-if="session.user && !isStudent" class="panel-note">教师账号不能报名学生活动。</small>
        </aside>
      </div>
    </template>
  </section>
</template>
