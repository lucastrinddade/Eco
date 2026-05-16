import React, { useState, useEffect, useCallback } from 'react';
import { ResiduoService } from './services/api';
import './App.css';

// ── Ícones SVG inline ──────────────────────────────────────────────────────
const IconRecycle = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="20" height="20">
    <path d="M7 19H4.815a1.83 1.83 0 0 1-1.57-.881 1.785 1.785 0 0 1-.004-1.784L7.196 9.5"/><path d="M11 19h8.203a1.83 1.83 0 0 0 1.556-.89 1.784 1.784 0 0 0 0-1.775l-1.226-2.12"/><path d="m14 16-3 3 3 3"/><path d="M8.293 13.596 7.196 9.5 3.1 10.598"/><path d="m9.344 5.811 1.093-1.892A1.83 1.83 0 0 1 11.985 3a1.784 1.784 0 0 1 1.546.888l3.943 6.843"/><path d="m13.378 9.633 4.096 1.098 1.097-4.096"/>
  </svg>
);
const IconFilter = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16">
    <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/>
  </svg>
);
const IconPlus = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16">
    <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
  </svg>
);
const IconTrash = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="14" height="14">
    <polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14H6L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/><path d="M9 6V4h6v2"/>
  </svg>
);
const IconEdit = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="14" height="14">
    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
  </svg>
);
const IconUpload = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16">
    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/>
  </svg>
);

// ── Componente de Card de Indicador ───────────────────────────────────────
function StatCard({ label, value, unit, accent, icon }) {
  return (
    <div className={`stat-card stat-card--${accent}`}>
      <div className="stat-icon">{icon}</div>
      <div className="stat-body">
        <span className="stat-value">{value}<span className="stat-unit">{unit}</span></span>
        <span className="stat-label">{label}</span>
      </div>
    </div>
  );
}

// ── Barra de taxa de reciclagem ────────────────────────────────────────────
function TaxaBar({ taxa }) {
  const cor = taxa >= 30 ? '#22c55e' : taxa >= 20 ? '#f59e0b' : '#ef4444';
  return (
    <div className="taxa-bar-wrap">
      <div className="taxa-bar-bg">
        <div className="taxa-bar-fill" style={{ width: `${Math.min(taxa, 100)}%`, background: cor }} />
      </div>
      <span className="taxa-value" style={{ color: cor }}>{taxa.toFixed(1)}%</span>
    </div>
  );
}

// ── Formulário de Cadastro / Edição ───────────────────────────────────────
function FormResiduo({ inicial, onSalvar, onCancelar }) {
  const [form, setForm] = useState(inicial || {
    municipio: '', estado: '', quantidadeGerada: '', taxaReciclagem: '', ano: new Date().getFullYear()
  });
  const [erro, setErro] = useState('');

  const estados = ['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG',
                   'PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'];

  const handle = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const enviar = async (e) => {
    e.preventDefault();
    setErro('');
    try {
      await onSalvar({
        ...form,
        quantidadeGerada: parseFloat(form.quantidadeGerada),
        taxaReciclagem: parseFloat(form.taxaReciclagem),
        ano: parseInt(form.ano),
        estado: form.estado.toUpperCase(),
      });
    } catch (err) {
      const msg = err.response?.data?.campos
        ? Object.values(err.response.data.campos).join(' | ')
        : err.response?.data?.mensagem || 'Erro ao salvar registro.';
      setErro(msg);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <h2 className="modal-title">{inicial ? '✏️ Editar Registro' : '➕ Novo Registro'}</h2>
        {erro && <div className="alert alert--error">{erro}</div>}
        <form onSubmit={enviar} className="form-grid">
          <div className="form-field">
            <label>Município *</label>
            <input name="municipio" value={form.municipio} onChange={handle} placeholder="Ex: São Paulo" required />
          </div>
          <div className="form-field">
            <label>Estado *</label>
            <select name="estado" value={form.estado} onChange={handle} required>
              <option value="">Selecione</option>
              {estados.map(uf => <option key={uf} value={uf}>{uf}</option>)}
            </select>
          </div>
          <div className="form-field">
            <label>Qtd. Gerada (ton.) *</label>
            <input type="number" step="0.01" name="quantidadeGerada" value={form.quantidadeGerada}
              onChange={handle} placeholder="Ex: 1200.50" required min="0" />
          </div>
          <div className="form-field">
            <label>Taxa Reciclagem (%) *</label>
            <input type="number" step="0.1" name="taxaReciclagem" value={form.taxaReciclagem}
              onChange={handle} placeholder="Ex: 22.5" required min="0" max="100" />
          </div>
          <div className="form-field">
            <label>Ano *</label>
            <input type="number" name="ano" value={form.ano} onChange={handle}
              placeholder="Ex: 2023" required min="2000" max="2099" />
          </div>
          <div className="form-actions">
            <button type="button" className="btn btn--ghost" onClick={onCancelar}>Cancelar</button>
            <button type="submit" className="btn btn--primary">
              {inicial ? 'Salvar Alterações' : 'Cadastrar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// ── App Principal ──────────────────────────────────────────────────────────
export default function App() {
  const [registros, setRegistros] = useState([]);
  const [indicadores, setIndicadores] = useState({});
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState(null);
  const [modalForm, setModalForm] = useState(null); // null | 'novo' | { registro }
  const [filtros, setFiltros] = useState({ estado: '', municipio: '', ano: '', metaFilter: false });
  const [importando, setImportando] = useState(false);

  const showToast = (msg, tipo = 'success') => {
    setToast({ msg, tipo });
    setTimeout(() => setToast(null), 3500);
  };

  const carregarDados = useCallback(async () => {
    setLoading(true);
    try {
      const [resRegs, resInd] = await Promise.all([
        filtros.metaFilter
          ? ResiduoService.buscarAbaixoDaMeta(20)
          : filtros.estado
            ? ResiduoService.buscarPorEstado(filtros.estado)
            : filtros.municipio
              ? ResiduoService.buscarPorMunicipio(filtros.municipio)
              : filtros.ano
                ? ResiduoService.buscarPorAno(filtros.ano)
                : ResiduoService.listarTodos(),
        ResiduoService.obterIndicadores(),
      ]);
      setRegistros(resRegs.data);
      setIndicadores(resInd.data);
    } catch {
      showToast('Erro ao conectar com a API. Verifique se o backend está rodando.', 'error');
    } finally {
      setLoading(false);
    }
  }, [filtros]);

  useEffect(() => { carregarDados(); }, [carregarDados]);

  const handleImportar = async () => {
    setImportando(true);
    try {
      const res = await ResiduoService.importarCSV();
      showToast(`✅ ${res.data.importados} registros importados do CSV!`);
      carregarDados();
    } catch {
      showToast('Erro ao importar CSV.', 'error');
    } finally {
      setImportando(false);
    }
  };

  const handleSalvar = async (dados) => {
    if (modalForm?.id) {
      await ResiduoService.atualizar(modalForm.id, dados);
      showToast('Registro atualizado com sucesso!');
    } else {
      await ResiduoService.cadastrar(dados);
      showToast('Registro cadastrado com sucesso!');
    }
    setModalForm(null);
    carregarDados();
  };

  const handleExcluir = async (id, municipio) => {
    if (!window.confirm(`Excluir o registro de "${municipio}"?`)) return;
    try {
      await ResiduoService.excluir(id);
      showToast('Registro excluído.');
      carregarDados();
    } catch {
      showToast('Erro ao excluir registro.', 'error');
    }
  };

  const limparFiltros = () => setFiltros({ estado: '', municipio: '', ano: '', metaFilter: false });

  const estados = ['','AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG',
                   'PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'];

  return (
    <div className="app">
      {/* Toast */}
      {toast && <div className={`toast toast--${toast.tipo}`}>{toast.msg}</div>}

      {/* Modal Formulário */}
      {modalForm !== null && (
        <FormResiduo
          inicial={modalForm === 'novo' ? null : modalForm}
          onSalvar={handleSalvar}
          onCancelar={() => setModalForm(null)}
        />
      )}

      {/* Header */}
      <header className="header">
        <div className="header-brand">
          <div className="header-logo"><IconRecycle /></div>
          <div>
            <h1 className="header-title">EcoRecicla</h1>
            <p className="header-sub">Sistema de Monitoramento de Resíduos e Reciclagem</p>
          </div>
        </div>
        <div className="header-actions">
          <button className="btn btn--outline" onClick={handleImportar} disabled={importando}>
            <IconUpload /> {importando ? 'Importando...' : 'Importar CSV'}
          </button>
          <button className="btn btn--primary" onClick={() => setModalForm('novo')}>
            <IconPlus /> Novo Registro
          </button>
        </div>
      </header>

      <main className="main">
        {/* Cards de Indicadores */}
        <section className="stats-grid">
          <StatCard label="Total de Registros" value={indicadores.totalRegistros ?? '—'} unit="" accent="blue" />
          <StatCard label="Média de Reciclagem" value={indicadores.mediaTaxaReciclagem ?? '—'} unit="%" accent="green" />
          <StatCard label="Total Gerado" value={indicadores.totalToneladasGeradas?.toLocaleString('pt-BR') ?? '—'} unit=" ton" accent="amber" />
          <StatCard label="Abaixo da Meta (20%)" value={indicadores.municipiosAbaixoDaMeta ?? '—'} unit=" mun." accent="red" />
        </section>

        {/* Filtros */}
        <section className="filters-bar">
          <div className="filters-left">
            <IconFilter />
            <span className="filters-label">Filtros:</span>
            <select value={filtros.estado}
              onChange={e => setFiltros({ ...filtros, estado: e.target.value, municipio: '', ano: '', metaFilter: false })}>
              {estados.map(uf => <option key={uf} value={uf}>{uf || 'Todos os estados'}</option>)}
            </select>
            <input placeholder="Buscar município..." value={filtros.municipio}
              onChange={e => setFiltros({ ...filtros, municipio: e.target.value, estado: '', ano: '', metaFilter: false })} />
            <input type="number" placeholder="Ano" value={filtros.ano}
              onChange={e => setFiltros({ ...filtros, ano: e.target.value, estado: '', municipio: '', metaFilter: false })}
              style={{ width: 80 }} />
            <label className="toggle-label">
              <input type="checkbox" checked={filtros.metaFilter}
                onChange={e => setFiltros({ estado: '', municipio: '', ano: '', metaFilter: e.target.checked })} />
              &nbsp;Abaixo da meta
            </label>
          </div>
          {(filtros.estado || filtros.municipio || filtros.ano || filtros.metaFilter) && (
            <button className="btn btn--ghost btn--sm" onClick={limparFiltros}>✕ Limpar</button>
          )}
        </section>

        {/* Tabela */}
        <section className="table-section">
          <div className="table-header">
            <h2 className="table-title">
              Registros <span className="badge">{registros.length}</span>
            </h2>
          </div>

          {loading ? (
            <div className="loading">
              <div className="spinner" />
              <p>Carregando dados...</p>
            </div>
          ) : registros.length === 0 ? (
            <div className="empty-state">
              <span className="empty-icon">🌱</span>
              <p>Nenhum registro encontrado.</p>
              <button className="btn btn--primary btn--sm" onClick={handleImportar}>
                Importar dados do CSV
              </button>
            </div>
          ) : (
            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Município</th>
                    <th>UF</th>
                    <th>Qtd. Gerada (ton.)</th>
                    <th>Taxa Reciclagem</th>
                    <th>Ano</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {registros.map(r => (
                    <tr key={r.id} className={r.taxaReciclagem < 20 ? 'row-alert' : ''}>
                      <td><span className="id-badge">#{r.id}</span></td>
                      <td className="municipio-cell">{r.municipio}</td>
                      <td><span className="uf-tag">{r.estado}</span></td>
                      <td className="num-cell">{r.quantidadeGerada.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</td>
                      <td><TaxaBar taxa={r.taxaReciclagem} /></td>
                      <td className="num-cell">{r.ano}</td>
                      <td>
                        <div className="action-btns">
                          <button className="btn-icon btn-icon--edit" title="Editar"
                            onClick={() => setModalForm(r)}><IconEdit /></button>
                          <button className="btn-icon btn-icon--delete" title="Excluir"
                            onClick={() => handleExcluir(r.id, r.municipio)}><IconTrash /></button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        {/* Rodapé de info */}
        <footer className="info-footer">
          <span>💡 Fonte: SNIS — Sistema Nacional de Informações sobre Saneamento</span>
          <span>Meta nacional de reciclagem: <strong>20%</strong></span>
          <a href="http://localhost:8080/h2-console" target="_blank" rel="noreferrer">🗄️ H2 Console</a>
        </footer>
      </main>
    </div>
  );
}
