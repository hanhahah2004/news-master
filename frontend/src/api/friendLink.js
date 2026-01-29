import request from './request'

export function getFriendLinks() {
  return request.get('/friend-links')
}

export function getAllFriendLinks() {
  return request.get('/friend-links/all')
}

export function getFriendLinkById(id) {
  return request.get(`/friend-links/${id}`)
}

export function createFriendLink(data) {
  return request.post('/friend-links', data)
}

export function updateFriendLink(id, data) {
  return request.put(`/friend-links/${id}`, data)
}

export function deleteFriendLink(id) {
  return request.delete(`/friend-links/${id}`)
}
