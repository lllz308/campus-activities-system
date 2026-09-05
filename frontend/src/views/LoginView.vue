<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowRight, LockKeyhole, UserRound } from '@lucide/vue'
import { api } from '../api'
import { setSession } from '../session'

const route = useRoute()
const router = useRouter()
const username = ref('student01')
const password = ref('student123')
const error = ref('')
const submitting = ref(false)

async function submit() {
  submitting.value = true
  error.value = ''
  try {
    const auth = await api('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username: username.value, password: password.value }),
    })
    setSession(auth)
    router.push(String(route.query.redirect || '/'))
  } catch (exception) {
    error.value = exception.message
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="auth-page">
    <div class="auth-visual">
      <img src="/campus-hero.png" alt="校园活动中心" />
      <div><span>青禾校园活动中心</span><strong>让每一次参与都有记录</strong></div>
    </div>
    <div class="auth-form-wrap">
      <form class="auth-form" @submit.prevent="submit">
        <div><span class="eyebrow dark">欢迎回来</span><h1>登录账号</h1><p>登录后继续管理活动或查看报名记录。</p></div>
        <label><span>用户名</span><div class="input-with-icon"><UserRound :size="18" /><input v-model="username" autocomplete="username" required /></div></label>
        <label><span>密码</span><div class="input-with-icon"><LockKeyhole :size="18" /><input v-model="password" type="password" autocomplete="current-password" required /></div></label>
        <div v-if="error" class="inline-error">{{ error }}</div>
        <button class="button button-primary button-block" type="submit" :disabled="submitting">{{ submitting ? '登录中...' : '登录' }}<ArrowRight :size="17" /></button>
        <p class="auth-switch">还没有账号？<RouterLink to="/register">立即注册</RouterLink></p>
        <div class="demo-hint"><strong>演示账号</strong><span>学生 student01 / student123</span><span>教师 teacher01 / teacher123</span></div>
      </form>
    </div>
  </section>
</template>
