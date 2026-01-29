import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const pokemonApi = {
  list: (page = 0, size = 20) =>
    api.get(`/pokemon?page=${page}&size=${size}`),

  getDetail: (id) =>
    api.get(`/pokemon/${id}`),

  getLocalList: () =>
    api.get('/local-pokemon'),

  getLocal: (id) =>
    api.get(`/local-pokemon/${id}`),

  copyToLocal: (id, attributes = {}) =>
    api.post(`/local-pokemon/${id}`, { attributes }),

  updateLocal: (id, data) =>
    api.put(`/local-pokemon/${id}`, data),
};

export default api;
