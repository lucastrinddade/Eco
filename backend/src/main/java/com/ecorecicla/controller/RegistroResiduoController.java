package com.ecorecicla.controller;

import com.ecorecicla.model.RegistroResiduo;
import com.ecorecicla.service.RegistroResiduoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/residuos")
@CrossOrigin(origins = "*") // Permite requisições do frontend React
public class RegistroResiduoController {

    @Autowired
    private RegistroResiduoService service;

    // ===================== CRUD =====================

    /**
     * GET /api/residuos
     * Lista todos os registros. Aceita filtros opcionais via query params.
     */
    @GetMapping
    public ResponseEntity<List<RegistroResiduo>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String municipio,
            @RequestParam(required = false) Integer ano) {

        List<RegistroResiduo> resultado;

        if (estado != null && ano != null) {
            resultado = service.buscarPorEstadoEAno(estado, ano);
        } else if (estado != null) {
            resultado = service.buscarPorEstado(estado);
        } else if (municipio != null) {
            resultado = service.buscarPorMunicipio(municipio);
        } else if (ano != null) {
            resultado = service.buscarPorAno(ano);
        } else {
            resultado = service.listarTodos();
        }

        return ResponseEntity.ok(resultado); // 200 OK
    }

    /**
     * GET /api/residuos/{id}
     * Busca um registro por ID. Retorna 404 se não encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegistroResiduo> buscarPorId(@PathVariable Long id) {
        /*
         * Uso do Optional: ao invés de retornar null e causar NullPointerException,
         * o Optional força o tratamento explícito. O .map() transforma o valor se presente,
         * e o .orElse() define o comportamento quando ausente — retornando 404 Not Found.
         */
        return service.buscarPorId(id)
                .map(registro -> ResponseEntity.ok(registro))           // 200 OK
                .orElse(ResponseEntity.notFound().build());             // 404 Not Found
    }

    /**
     * POST /api/residuos
     * Cadastra um novo registro. Retorna 201 Created com o objeto criado.
     */
    @PostMapping
    public ResponseEntity<RegistroResiduo> cadastrar(@Valid @RequestBody RegistroResiduo registro) {
        RegistroResiduo salvo = service.cadastrar(registro);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo); // 201 Created
    }

    /**
     * PUT /api/residuos/{id}
     * Atualiza um registro existente. Retorna 404 se não encontrado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegistroResiduo> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RegistroResiduo dadosAtualizados) {

        return service.atualizar(id, dadosAtualizados)
                .map(atualizado -> ResponseEntity.ok(atualizado))       // 200 OK
                .orElse(ResponseEntity.notFound().build());             // 404 Not Found
    }

    /**
     * DELETE /api/residuos/{id}
     * Exclui um registro. Retorna 204 No Content ou 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        boolean excluido = service.excluir(id);
        if (excluido) {
            return ResponseEntity.noContent().build();   // 204 No Content
        }
        return ResponseEntity.notFound().build();        // 404 Not Found
    }

    // ===================== CONSULTAS ESPECIALIZADAS =====================

    /**
     * GET /api/residuos/abaixo-da-meta
     * Retorna municípios com taxa de reciclagem abaixo da meta.
     * Aceita parâmetro opcional ?meta=20.0 (padrão: 20%)
     */
    @GetMapping("/abaixo-da-meta")
    public ResponseEntity<List<RegistroResiduo>> abaixoDaMeta(
            @RequestParam(defaultValue = "20.0") Double meta) {
        List<RegistroResiduo> resultado = service.buscarAbaixoDaMeta(meta);
        return ResponseEntity.ok(resultado); // 200 OK
    }

    /**
     * GET /api/residuos/acima-da-taxa?taxa=30.0
     * Retorna municípios com taxa acima do valor informado.
     */
    @GetMapping("/acima-da-taxa")
    public ResponseEntity<List<RegistroResiduo>> acimaDaTaxa(
            @RequestParam(defaultValue = "30.0") Double taxa) {
        List<RegistroResiduo> resultado = service.buscarAcimaDaTaxa(taxa);
        return ResponseEntity.ok(resultado); // 200 OK
    }

    // ===================== DASHBOARD / INDICADORES =====================

    /**
     * GET /api/residuos/indicadores
     * Retorna indicadores gerais para o dashboard.
     */
    @GetMapping("/indicadores")
    public ResponseEntity<Map<String, Object>> indicadores() {
        Map<String, Object> dados = service.obterIndicadores();
        return ResponseEntity.ok(dados); // 200 OK
    }

    // ===================== CARGA DE DADOS CSV =====================

    /**
     * POST /api/residuos/importar-csv
     * Dispara a importação do arquivo CSV para popular o banco.
     */
    @PostMapping("/importar-csv")
    public ResponseEntity<Map<String, Object>> importarCSV() {
        Map<String, Object> resultado = service.importarCSV();
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado); // 201 Created
    }
}
