package com.ecorecicla.service;

import com.ecorecicla.model.RegistroResiduo;
import com.ecorecicla.repository.RegistroResiduoRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RegistroResiduoService {

    private static final Logger logger = LoggerFactory.getLogger(RegistroResiduoService.class);

    // Meta nacional de reciclagem (%) - referência para filtros
    private static final Double META_RECICLAGEM = 20.0;

    @Autowired
    private RegistroResiduoRepository repository;

    // ===================== CRUD =====================

    /**
     * Lista todos os registros.
     */
    public List<RegistroResiduo> listarTodos() {
        return repository.findAll();
    }

    /**
     * Busca por ID usando Optional para evitar NullPointerException.
     * O Optional força o tratamento explícito do caso "não encontrado",
     * ao contrário de retornar null diretamente, que causaria NPE em runtime.
     */
    public Optional<RegistroResiduo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /**
     * Cadastra um novo registro de resíduo.
     */
    @Transactional
    public RegistroResiduo cadastrar(RegistroResiduo registro) {
        logger.info("Cadastrando novo registro: {} - {}", registro.getMunicipio(), registro.getAno());
        return repository.save(registro);
    }

    /**
     * Atualiza um registro existente.
     * Retorna Optional.empty() se o ID não existir.
     */
    @Transactional
    public Optional<RegistroResiduo> atualizar(Long id, RegistroResiduo dadosAtualizados) {
        return repository.findById(id).map(registro -> {
            registro.setMunicipio(dadosAtualizados.getMunicipio());
            registro.setEstado(dadosAtualizados.getEstado());
            registro.setQuantidadeGerada(dadosAtualizados.getQuantidadeGerada());
            registro.setTaxaReciclagem(dadosAtualizados.getTaxaReciclagem());
            registro.setAno(dadosAtualizados.getAno());
            logger.info("Registro id={} atualizado com sucesso.", id);
            return repository.save(registro);
        });
    }

    /**
     * Exclui um registro por ID.
     * Retorna true se deletado, false se não encontrado.
     */
    @Transactional
    public boolean excluir(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            logger.info("Registro id={} excluído.", id);
            return true;
        }
        logger.warn("Tentativa de excluir registro inexistente: id={}", id);
        return false;
    }

    // ===================== CONSULTAS ESPECIALIZADAS =====================

    public List<RegistroResiduo> buscarPorEstado(String estado) {
        return repository.findByEstadoIgnoreCase(estado);
    }

    public List<RegistroResiduo> buscarPorMunicipio(String municipio) {
        return repository.findByMunicipioContainingIgnoreCase(municipio);
    }

    public List<RegistroResiduo> buscarAbaixoDaMeta() {
        return repository.findAbaixoDaMeta(META_RECICLAGEM);
    }

    public List<RegistroResiduo> buscarAbaixoDaMeta(Double meta) {
        return repository.findAbaixoDaMeta(meta);
    }

    public List<RegistroResiduo> buscarAcimaDaTaxa(Double taxa) {
        return repository.findByTaxaReciclagemGreaterThan(taxa);
    }

    public List<RegistroResiduo> buscarPorAno(Integer ano) {
        return repository.findByAno(ano);
    }

    public List<RegistroResiduo> buscarPorEstadoEAno(String estado, Integer ano) {
        return repository.findByEstadoIgnoreCaseAndAno(estado, ano);
    }

    // ===================== INDICADORES / DASHBOARD =====================

    /**
     * Retorna um mapa com indicadores gerais para o dashboard.
     */
    public Map<String, Object> obterIndicadores() {
        List<RegistroResiduo> todos = repository.findAll();
        Map<String, Object> indicadores = new HashMap<>();

        if (todos.isEmpty()) {
            indicadores.put("totalRegistros", 0);
            indicadores.put("mediaTaxaReciclagem", 0.0);
            indicadores.put("totalToneladasGeradas", 0.0);
            indicadores.put("municipiosAbaixoDaMeta", 0);
            return indicadores;
        }

        double mediaTaxa = todos.stream()
                .mapToDouble(RegistroResiduo::getTaxaReciclagem)
                .average()
                .orElse(0.0);

        double totalToneladas = todos.stream()
                .mapToDouble(RegistroResiduo::getQuantidadeGerada)
                .sum();

        long abaixoDaMeta = todos.stream()
                .filter(r -> r.getTaxaReciclagem() < META_RECICLAGEM)
                .count();

        indicadores.put("totalRegistros", todos.size());
        indicadores.put("mediaTaxaReciclagem", Math.round(mediaTaxa * 100.0) / 100.0);
        indicadores.put("totalToneladasGeradas", Math.round(totalToneladas * 100.0) / 100.0);
        indicadores.put("municipiosAbaixoDaMeta", abaixoDaMeta);
        indicadores.put("metaNacional", META_RECICLAGEM);

        return indicadores;
    }

    // ===================== CARGA DE DADOS CSV =====================

    /**
     * Lê o arquivo CSV do classpath e popula o banco de dados.
     * Utiliza OpenCSV para parsing e ignora registros duplicados (mesmo município + ano).
     *
     * @return número de registros importados com sucesso
     */
    @Transactional
    public Map<String, Object> importarCSV() {
        int importados = 0;
        int ignorados = 0;
        int erros = 0;

        InputStream is = getClass().getClassLoader().getResourceAsStream("dados_residuos.csv");

        if (is == null) {
            logger.error("Arquivo CSV não encontrado no classpath.");
            throw new RuntimeException("Arquivo dados_residuos.csv não encontrado.");
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(is))) {
            String[] linha;
            boolean primeiraLinha = true;

            while ((linha = reader.readNext()) != null) {
                // Pula o cabeçalho
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                if (linha.length < 5) {
                    logger.warn("Linha inválida ignorada: {}", String.join(",", linha));
                    erros++;
                    continue;
                }

                try {
                    String municipio = linha[0].trim();
                    String estado = linha[1].trim().toUpperCase();
                    Double qtdGerada = Double.parseDouble(linha[2].trim());
                    Double taxaRec = Double.parseDouble(linha[3].trim());
                    Integer ano = Integer.parseInt(linha[4].trim());

                    // Evita duplicatas
                    if (repository.existsByMunicipioIgnoreCaseAndAno(municipio, ano)) {
                        logger.debug("Registro duplicado ignorado: {} - {}", municipio, ano);
                        ignorados++;
                        continue;
                    }

                    RegistroResiduo registro = new RegistroResiduo(municipio, estado, qtdGerada, taxaRec, ano);
                    repository.save(registro);
                    importados++;

                } catch (NumberFormatException e) {
                    logger.error("Erro ao converter linha: {}", String.join(",", linha));
                    erros++;
                }
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Erro ao ler o CSV: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar CSV: " + e.getMessage());
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("importados", importados);
        resultado.put("ignorados", ignorados);
        resultado.put("erros", erros);
        resultado.put("mensagem", "Importação concluída com sucesso!");
        logger.info("CSV importado: {} registros, {} ignorados, {} erros.", importados, ignorados, erros);
        return resultado;
    }
}
