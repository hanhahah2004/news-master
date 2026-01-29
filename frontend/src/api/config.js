import request from './request'

export function getSiteConfig() {
  return request.get('/config/site')
}

export function getConfig(key) {
  return request.get(`/config/${key}`)
}

export function updateConfig(key, value) {
  return request.put(`/config/${key}`, { value })
}
