import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000,
  headers: { 'Content-Type': 'application/json' },
})

export async function fetchFriends() {
  const res = await api.get('/friends')
  return res.data
}

export async function addFriend(payload) {
  const res = await api.post('/friends', payload)
  return res.data
}

export async function deleteFriend(id) {
  const res = await api.delete(`/friends/${id}`)
  return res.data
}

export async function fetchExpenses() {
  const res = await api.get('/expenses')
  return res.data
}

export async function addExpense(payload) {
  const res = await api.post('/expenses', payload)
  return res.data
}

export async function fetchBalances() {
  const res = await api.get('/balances')
  return res.data
}

export async function fetchSettlements() {
  const res = await api.get('/settlements')
  return res.data
}

export async function resetAll() {
  const res = await api.post('/admin/reset')
  return res.data
}

export default api
