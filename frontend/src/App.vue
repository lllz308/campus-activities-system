<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { CalendarDays, ClipboardList, LogIn, LogOut, Menu, School, Settings2, X } from '@lucide/vue'
import { api } from './api'
import { clearSession, isStudent, isTeacher, session } from './session'

const router = useRouter()
const menuOpen = ref(false)
const toast = ref('')

const initials = computed(() => session.user?.displayName?.slice(0, 1) || '')

async function logout() {
  try {
    await api('/api/auth/logout', { method: 'POST' })
  } finally {
    clearSession()
    menuOpen.value = false
    toast.value = '已安全退出'
    router.push({ name: 'activities' })
    window.setTimeout(() => (toast.value = ''), 2200)
  }
}
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/" @click="menuOpen = false">
        <span class="brand-mark"><School :size="21" /></span>
        <span>青禾校园活动中心</span>
      </RouterLink>

      <button class="icon-button mobile-menu" type="button" aria-label="打开导航" @click="menuOpen = !menuOpen">
        <X v-if="menuOpen" :size="22" />
        <Menu v-else :size="22" />
      </button>

      <nav :class="['main-nav', { open: menuOpen }]">
        <RouterLink to="/" @click="menuOpen = false">
          <CalendarDays :size="17" />活动大厅
        </RouterLink>
        <RouterLink v-if="isStudent" to="/my-registrations" @click="menuOpen = false">
          <ClipboardList :size="17" />我的报名
        </RouterLink>
        <RouterLink v-if="isTeacher" to="/teacher/activities" @click="menuOpen = false">
          <Settings2 :size="17" />活动管理
        </RouterLink>
      </nav>

      <div class="account-actions">
        <template v-if="session.user">
          <span class="avatar" :title="session.user.displayName">{{ initials }}</span>
          <div class="account-copy">
            <strong>{{ session.user.displayName }}</strong>
            <span>{{ session.user.role === 'TEACHER' ? '活动组织教师' : '学生' }}</span>
          </div>
          <button class="icon-button" type="button" title="退出登录" aria-label="退出登录" @click="logout">
            <LogOut :size="19" />
          </button>
        </template>
        <RouterLink v-else class="button button-secondary button-small" to="/login">
          <LogIn :size="17" />登录
        </RouterLink>
      </div>
    </header>

    <main>
      <RouterView />
    </main>

    <Transition name="toast">
      <div v-if="toast" class="toast" role="status">{{ toast }}</div>
    </Transition>
  </div>
</template>
