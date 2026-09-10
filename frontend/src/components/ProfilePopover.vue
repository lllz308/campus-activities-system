<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import {
  ChevronDown,
  GraduationCap,
  Hash,
  KeyRound,
  LockKeyhole,
  LogOut,
  Mail,
  Pencil,
  UserRound,
} from '@lucide/vue'
import { api } from '../api'
import { session, updateSessionUser } from '../session'

const emit = defineEmits(['logout'])

const open = ref(false)
const profile = ref(null)
const loading = ref(false)
const loadError = ref('')

const activeView = ref('profile')
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const passwordError = ref('')
const passwordSuccess = ref('')
const submittingPassword = ref(false)

const editForm = ref({ displayName: '', studentNo: '', major: '', email: '' })
const editError = ref('')
const editSuccess = ref('')
const submittingEdit = ref(false)

const initials = computed(() => session.user?.displayName?.slice(0, 1) || '')
const roleLabel = computed(() => (session.user?.role === 'TEACHER' ? '活动组织教师' : '学生'))
const studentNoLabel = computed(() => (session.user?.role === 'TEACHER' ? '工号' : '学号'))
const majorLabel = computed(() => (session.user?.role === 'TEACHER' ? '学院' : '专业'))

const closeTimer = ref(null)
// 视图切换（尤其是“返回”回到较矮的资料视图）会改变弹窗高度，使鼠标下的按钮被移除、
// 鼠标短暂落到弹窗边界外并误触发 mouseleave。此时短暂抑制关闭，避免点击“返回”后弹窗自行关闭。
let closeSuppressed = false
let suppressTimer = null

function suppressCloseBriefly() {
  closeSuppressed = true
  clearTimeout(suppressTimer)
  suppressTimer = setTimeout(() => {
    closeSuppressed = false
  }, 300)
}

function openPopover() {
  clearTimeout(closeTimer.value)
  open.value = true
  if (!profile.value && !loading.value) loadProfile()
}

function resetForms() {
  activeView.value = 'profile'
  passwordError.value = ''
  passwordSuccess.value = ''
  passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  editError.value = ''
  editSuccess.value = ''
}

function scheduleClose() {
  // 表单工作态（编辑资料 / 修改密码）不因鼠标移出而关闭，避免视图切换导致弹窗
  // 高度变化、鼠标短暂落到弹窗外而误关闭；此时只能点击弹窗外部或“返回”关闭。
  if (closeSuppressed) return
  if (activeView.value !== 'profile') return
  clearTimeout(closeTimer.value)
  closeTimer.value = setTimeout(() => {
    // 兜底：视图切换会移除鼠标下的按钮并误触发 mouseleave，此时鼠标其实仍在弹窗内
    const trigger = document.querySelector('.profile-trigger')
    if (trigger && trigger.matches(':hover')) return
    open.value = false
    resetForms()
  }, 140)
}

function togglePopover() {
  if (open.value) {
    open.value = false
  } else {
    openPopover()
  }
}

async function loadProfile() {
  loading.value = true
  loadError.value = ''
  try {
    profile.value = await api('/api/user/me')
  } catch (exception) {
    loadError.value = exception.message
  } finally {
    loading.value = false
  }
}

function openPasswordForm() {
  activeView.value = 'password'
  passwordError.value = ''
  passwordSuccess.value = ''
  passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
}

function openEditForm() {
  activeView.value = 'edit'
  editError.value = ''
  editSuccess.value = ''
  editForm.value = {
    displayName: profile.value?.displayName || session.user.displayName || '',
    studentNo: profile.value?.studentNo || '',
    major: profile.value?.major || '',
    email: profile.value?.email || '',
  }
}

function backToProfile() {
  activeView.value = 'profile'
  passwordError.value = ''
  passwordSuccess.value = ''
  editError.value = ''
  editSuccess.value = ''
  suppressCloseBriefly()
}

async function submitPassword() {
  passwordError.value = ''
  passwordSuccess.value = ''
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordError.value = '两次输入的新密码不一致'
    return
  }
  submittingPassword.value = true
  try {
    await api('/api/user/change-password', {
      method: 'POST',
      body: JSON.stringify({
        oldPassword: passwordForm.value.oldPassword,
        newPassword: passwordForm.value.newPassword,
      }),
    })
    passwordSuccess.value = '密码已修改'
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (exception) {
    passwordError.value = exception.message
  } finally {
    submittingPassword.value = false
  }
}

async function submitEdit() {
  editError.value = ''
  editSuccess.value = ''
  submittingEdit.value = true
  try {
    const updated = await api('/api/user/me', {
      method: 'PUT',
      body: JSON.stringify({
        displayName: editForm.value.displayName,
        studentNo: editForm.value.studentNo || null,
        major: editForm.value.major || null,
        email: editForm.value.email || null,
      }),
    })
    profile.value = updated
    updateSessionUser({ displayName: updated.displayName })
    editSuccess.value = '资料已保存'
    editForm.value = {
      displayName: updated.displayName,
      studentNo: updated.studentNo || '',
      major: updated.major || '',
      email: updated.email || '',
    }
  } catch (exception) {
    editError.value = exception.message
  } finally {
    submittingEdit.value = false
  }
}

function onDocumentClick(event) {
  const root = document.querySelector('.profile-trigger')
  // 使用 composedPath 而非 root.contains(event.target)：点击“编辑资料/修改密码”时，
  // Vue 会在事件冒泡到 document 前移除被点击的按钮，导致 contains 误判为点击弹窗外部。
  const path = event.composedPath()
  if (root && !path.includes(root)) {
    open.value = false
    resetForms()
  }
}

onMounted(() => document.addEventListener('click', onDocumentClick))
onBeforeUnmount(() => {
  document.removeEventListener('click', onDocumentClick)
  clearTimeout(suppressTimer)
  clearTimeout(closeTimer.value)
})
</script>

<template>
  <div class="profile-trigger" @mouseenter="openPopover" @mouseleave="scheduleClose">
    <button class="profile-button" type="button" @click="togglePopover">
      <span class="avatar">{{ initials }}</span>
      <span class="account-copy">
        <strong>{{ session.user.displayName }}</strong>
        <span>{{ roleLabel }}</span>
      </span>
      <ChevronDown class="profile-caret" :class="{ flipped: open }" :size="16" />
    </button>

    <Transition name="pop">
      <section v-if="open" class="profile-popover" role="dialog" aria-label="个人主页">
        <template v-if="activeView === 'profile'">
          <div class="profile-head">
            <span class="profile-avatar">{{ initials }}</span>
            <div class="profile-head-copy">
              <strong>{{ profile?.displayName || session.user.displayName }}</strong>
              <span>{{ profile?.username || session.user.username }}</span>
            </div>
            <span class="profile-role" :class="session.user.role === 'TEACHER' ? 'status-published' : 'seat-state'">
              {{ roleLabel }}
            </span>
          </div>

          <div v-if="loadError" class="inline-error profile-note">{{ loadError }}</div>

          <dl class="profile-list">
            <div>
              <dt><UserRound :size="16" />姓名</dt>
              <dd>{{ profile?.displayName || '—' }}</dd>
            </div>
            <div>
              <dt><Hash :size="16" />{{ studentNoLabel }}</dt>
              <dd>{{ profile?.studentNo || '—' }}</dd>
            </div>
            <div>
              <dt><GraduationCap :size="16" />{{ majorLabel }}</dt>
              <dd>{{ profile?.major || '—' }}</dd>
            </div>
            <div>
              <dt><Mail :size="16" />邮箱</dt>
              <dd>{{ profile?.email || '—' }}</dd>
            </div>
          </dl>

          <div class="profile-password-row">
            <div class="profile-password">
              <LockKeyhole :size="16" />
              <span class="profile-password-value">••••••••</span>
            </div>
          </div>

          <div class="profile-actions">
            <button class="button button-secondary button-small" type="button" @click="openEditForm">
              <Pencil :size="15" />编辑资料
            </button>
            <button class="button button-secondary button-small" type="button" @click="openPasswordForm">
              <KeyRound :size="15" />修改密码
            </button>
          </div>
        </template>

        <template v-else-if="activeView === 'password'">
          <div class="profile-head">
            <span class="profile-head-title"><KeyRound :size="18" />修改密码</span>
          </div>

          <form class="password-form" @submit.prevent="submitPassword">
            <label>
              <span>旧密码</span>
              <div class="input-with-icon">
                <LockKeyhole :size="17" />
                <input v-model="passwordForm.oldPassword" type="password" autocomplete="current-password" required />
              </div>
            </label>
            <label>
              <span>新密码</span>
              <div class="input-with-icon">
                <LockKeyhole :size="17" />
                <input v-model="passwordForm.newPassword" type="password" minlength="8" maxlength="72" autocomplete="new-password" placeholder="至少 8 位" required />
              </div>
            </label>
            <label>
              <span>确认新密码</span>
              <div class="input-with-icon">
                <LockKeyhole :size="17" />
                <input v-model="passwordForm.confirmPassword" type="password" minlength="8" maxlength="72" autocomplete="new-password" placeholder="再次输入新密码" required />
              </div>
            </label>

            <div v-if="passwordError" class="inline-error">{{ passwordError }}</div>
            <div v-if="passwordSuccess" class="inline-success">{{ passwordSuccess }}</div>

            <div class="password-form-actions">
              <button class="button button-secondary button-small" type="button" @click="backToProfile">返回</button>
              <button class="button button-primary button-small" type="submit" :disabled="submittingPassword">
                {{ submittingPassword ? '提交中...' : '确认修改' }}
              </button>
            </div>
          </form>
        </template>

        <template v-else>
          <div class="profile-head">
            <span class="profile-head-title"><Pencil :size="18" />编辑资料</span>
          </div>

          <form class="profile-form" @submit.prevent="submitEdit">
            <label>
              <span>姓名</span>
              <input v-model="editForm.displayName" type="text" maxlength="40" autocomplete="name" required />
            </label>
            <label>
              <span>{{ studentNoLabel }}</span>
              <input v-model="editForm.studentNo" type="text" maxlength="32" />
            </label>
            <label>
              <span>{{ majorLabel }}</span>
              <input v-model="editForm.major" type="text" maxlength="64" />
            </label>
            <label>
              <span>邮箱</span>
              <input v-model="editForm.email" type="email" maxlength="128" autocomplete="email" />
            </label>

            <div v-if="editError" class="inline-error">{{ editError }}</div>
            <div v-if="editSuccess" class="inline-success">{{ editSuccess }}</div>

            <div class="profile-form-actions">
              <button class="button button-secondary button-small" type="button" @click="backToProfile">返回</button>
              <button class="button button-primary button-small" type="submit" :disabled="submittingEdit">
                {{ submittingEdit ? '保存中...' : '保存' }}
              </button>
            </div>
          </form>
        </template>

        <button class="profile-logout" type="button" @click="emit('logout')">
          <LogOut :size="17" />退出账号
        </button>
      </section>
    </Transition>
  </div>
</template>
