const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  })

  if (!response.ok) {
    const body = await response.json().catch(() => null)
    throw new Error(body?.detail ?? body?.message ?? 'Não foi possível concluir a operação')
  }

  if (response.status === 204) return null
  return response.json()
}

export const api = {
  getPolls: () => request('/polls'),
  getPoll: (id) => request(`/poll/${id}`),
  getResults: (id) => request(`/polls/${id}/results`),
  createPoll: (userId, data) => request(`/poll?userId=${userId}`, {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  vote: (pollId, userId, optionId) => request(`/polls/${pollId}/vote`, {
    method: 'POST',
    body: JSON.stringify({ userId, optionId }),
  }),
  register: (data) => request('/users', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
  login: (data) => request('/users/login', {
    method: 'POST',
    body: JSON.stringify(data),
  }),
}
