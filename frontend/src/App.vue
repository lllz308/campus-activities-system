<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { CalendarDays, ClipboardList, History, LogIn, Menu, School, Settings2, X } from '@lucide/vue'
import { api } from './api'
import { clearSession, isStudent, isTeacher, session } from './session'
import ProfilePopover from './components/ProfilePopover.vue'

const router = useRouter()
const menuOpen = ref(false)
const toast = ref('')

async function logout() {
  try {
    await api('/api/auth/logout', { method: 'POST' })
  } finally {
    clearSession()
    menuOpen.value = false
    toast.value = '已安全退出'
    await router.replace({ name: 'login' })
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
        <RouterLink v-if="isStudent" to="/my-history" @click="menuOpen = false">
          <History :size="17" />历史记录
        </RouterLink>
        <RouterLink v-if="isTeacher" to="/teacher/activities" @click="menuOpen = false">
          <Settings2 :size="17" />活动管理
        </RouterLink>
      </nav>

      <div class="account-actions">
        <ProfilePopover v-if="session.user" @logout="logout" />
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
