import { computed, reactive } from 'vue'

// Keep authentication only for the current Electron/browser session.
// A fresh desktop launch therefore starts at the login page.
const saved = JSON.parse(sessionStorage.getItem('campus-session') || 'null')

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
  sessionStorage.setItem('campus-session', JSON.stringify({ token: session.token, user: session.user }))
  localStorage.removeItem('campus-session')
}

export function clearSession() {
  session.token = ''
  session.user = null
  sessionStorage.removeItem('campus-session')
  localStorage.removeItem('campus-session')
}

