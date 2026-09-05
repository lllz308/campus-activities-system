import { computed, reactive } from 'vue'

const saved = JSON.parse(localStorage.getItem('campus-session') || 'null')

export const session = reactive({
  token: saved?.token || '',
  user: saved?.user || null,
})

export const isLoggedIn = computed(() => Boolean(session.token && session.user))
export const isStudent = computed(() => session.user?.role === 'STUDENT')
export const isTeacher = computed(() => session.user?.role === 'TEACHER')

export function setSession(auth) {
  session.token = auth.token
  session.user = {
    id: auth.userId,
    username: auth.username,
    displayName: auth.displayName,
    role: auth.role,
  }
  localStorage.setItem('campus-session', JSON.stringify({ token: session.token, user: session.user }))
}

export function clearSession() {
  session.token = ''
  session.user = null
  localStorage.removeItem('campus-session')
}

