<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, BadgeCheck, LockKeyhole, UserRound } from '@lucide/vue'
import { api } from '../api'
import { setSession } from '../session'

const router = useRouter()
const form = ref({ username: '', displayName: '', password: '', role: 'STUDENT' })
const error = ref('')
const submitting = ref(false)

async function submit() {
  submitting.value = true
  error.value = ''
  try {
    const auth = await api('/api/auth/register', { method: 'POST', body: JSON.stringify(form.value) })
    setSession(auth)
    router.push(auth.role === 'TEACHER' ? '/teacher/activities' : '/')
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
      <div><span>加入校园活动社区</span><strong>从一场活动认识更多同伴</strong></div>
    </div>
    <div class="auth-form-wrap">
      <form class="auth-form" @submit.prevent="submit">
        <div><span class="eyebrow dark">创建账号</span><h1>注册</h1><p>选择你的身份，进入对应的工作空间。</p></div>
        <div class="role-selector">
          <label :class="{ selected: form.role === 'STUDENT' }"><input v-model="form.role" type="radio" value="STUDENT" /><UserRound :size="20" /><span><strong>学生</strong><small>浏览与报名活动</small></span></label>
          <label :class="{ selected: form.role === 'TEACHER' }"><input v-model="form.role" type="radio" value="TEACHER" /><BadgeCheck :size="20" /><span><strong>教师</strong><small>发布与管理活动</small></span></label>
        </div>
        <label><span>用户名</span><div class="input-with-icon"><UserRound :size="18" /><input v-model="form.username" pattern="[A-Za-z0-9_]{4,20}" autocomplete="username" placeholder="4-20 位字母、数字或下划线" required /></div></label>
        <label><span>显示名称</span><input v-model="form.displayName" maxlength="40" placeholder="如：王同学" required /></label>
        <label><span>密码</span><div class="input-with-icon"><LockKeyhole :size="18" /><input v-model="form.password" type="password" minlength="8" maxlength="72" autocomplete="new-password" placeholder="至少 8 位" required /></div></label>
        <div v-if="error" class="inline-error">{{ error }}</div>
        <button class="button button-primary button-block" type="submit" :disabled="submitting">{{ submitting ? '创建中...' : '创建账号' }}<ArrowRight :size="17" /></button>
        <p class="auth-switch">已有账号？<RouterLink to="/login">返回登录</RouterLink></p>
      </form>
    </div>
  </section>
</template>
