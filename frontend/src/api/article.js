import request from './request'

export function getArticleList(params) {
  return request.get('/articles', { params })
}

export function getArticleDetail(id) {
  return request.get(`/articles/${id}`)
}

export function getArticlesByCategory(categoryId, params) {
  return request.get(`/articles/category/${categoryId}`, { params })
}

export function searchArticles(keyword, params) {
  return request.get('/articles/search', { params: { keyword, ...params } })
}

export function getHotArticles(limit = 10) {
  return request.get('/articles/hot', { params: { limit } })
}

export function getTopArticles() {
  return request.get('/articles/top')
}

export function createArticle(data) {
  return request.post('/articles', data)
}

export function updateArticle(id, data) {
  return request.put(`/articles/${id}`, data)
}

export function deleteArticle(id) {
  return request.delete(`/articles/${id}`)
}

export function publishArticle(id) {
  return request.post(`/articles/${id}/publish`)
}

export function unpublishArticle(id) {
  return request.post(`/articles/${id}/unpublish`)
}

export function likeArticle(id) {
  return request.post(`/articles/${id}/like`)
}

export function getArticlesByAuthor(authorId, params) {
  return request.get(`/articles/author/${authorId}`, { params })
}
