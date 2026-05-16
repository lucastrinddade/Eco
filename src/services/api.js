import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/residuos';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

export const ResiduoService = {
  listarTodos: () => api.get(''),
  buscarPorId: (id) => api.get(`/${id}`),
  buscarPorEstado: (estado) => api.get('', { params: { estado } }),
  buscarPorMunicipio: (municipio) => api.get('', { params: { municipio } }),
  buscarPorAno: (ano) => api.get('', { params: { ano } }),
  buscarAbaixoDaMeta: (meta = 20) => api.get('/abaixo-da-meta', { params: { meta } }),
  obterIndicadores: () => api.get('/indicadores'),
  cadastrar: (dados) => api.post('', dados),
  atualizar: (id, dados) => api.put(`/${id}`, dados),
  excluir: (id) => api.delete(`/${id}`),
  importarCSV: () => api.post('/importar-csv'),
};