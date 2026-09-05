import { clearSession, session } from './session'

export class ApiError extends Error {
  constructor(message, status) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

export async function api(path, options = {}) {
  const headers = new Headers(options.headers || {})
  if (options.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }
  if (session.token) {
    headers.set('Authorization', `Bearer ${session.token}`)
  }

  let response
  try {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || ''
    response = await fetch(`${baseUrl}${path}`, { ...options, headers })
  } catch {
    throw new ApiError('无法连接服务器，请确认后端已在 8080 端口启动', 0)
  }

  const payload = await response.json().catch(() => null)
  if (!response.ok || payload?.code === 0) {
    if (response.status === 401) clearSession()
    throw new ApiError(payload?.message || `请求失败（${response.status}）`, response.status)
  }
  return payload?.data
}
