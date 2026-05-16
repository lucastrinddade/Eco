package com.ecorecicla.repository;

import com.ecorecicla.model.RegistroResiduo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroResiduoRepository extends JpaRepository<RegistroResiduo, Long> {

    // Busca por estado (ex: "SP", "RJ")
    List<RegistroResiduo> findByEstadoIgnoreCase(String estado);

    // Busca por município (busca parcial, case-insensitive)
    List<RegistroResiduo> findByMunicipioContainingIgnoreCase(String municipio);

    // Municípios com taxa de reciclagem acima de um valor
    List<RegistroResiduo> findByTaxaReciclagemGreaterThan(Double taxa);

    // Municípios com taxa de reciclagem abaixo da meta
    List<RegistroResiduo> findByTaxaReciclagemLessThan(Double meta);

    // Registros por ano
    List<RegistroResiduo> findByAno(Integer ano);

    // Busca por estado e ano
    List<RegistroResiduo> findByEstadoIgnoreCaseAndAno(String estado, Integer ano);

    // JPQL: municípios com taxa abaixo da meta, ordenados por taxa crescente
    @Query("SELECT r FROM RegistroResiduo r WHERE r.taxaReciclagem < :meta ORDER BY r.taxaReciclagem ASC")
    List<RegistroResiduo> findAbaixoDaMeta(@Param("meta") Double meta);

    // JPQL: total de resíduos gerados por estado
    @Query("SELECT r.estado, SUM(r.quantidadeGerada) FROM RegistroResiduo r WHERE r.ano = :ano GROUP BY r.estado ORDER BY SUM(r.quantidadeGerada) DESC")
    List<Object[]> totalPorEstadoNoAno(@Param("ano") Integer ano);

    // Verifica se já existe registro para o município e ano
    boolean existsByMunicipioIgnoreCaseAndAno(String municipio, Integer ano);
}
