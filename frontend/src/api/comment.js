import request from './request'

export function getCommentsByArticle(articleId, params) {
  return request.get(`/comments/article/${articleId}`, { params })
}

export function getRepliesByParent(parentId) {
  return request.get(`/comments/replies/${parentId}`)
}

export function createComment(data) {
  return request.post('/comments', data)
}

export function deleteComment(id) {
  return request.delete(`/comments/${id}`)
}

export function getCommentsByUser() {
  return request.get('/comments/user')
}

export function getAllComments(params) {
  return request.get('/comments/admin', { params })
}
