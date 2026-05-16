package com.ecorecicla.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "registro_residuo")
public class RegistroResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Município é obrigatório")
    @Column(nullable = false)
    private String municipio;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (sigla)")
    @Column(nullable = false, length = 2)
    private String estado;

    @NotNull(message = "Quantidade gerada é obrigatória")
    @Positive(message = "Quantidade gerada deve ser positiva")
    @Column(name = "quantidade_gerada", nullable = false)
    private Double quantidadeGerada; // em toneladas

    @NotNull(message = "Taxa de reciclagem é obrigatória")
    @DecimalMin(value = "0.0", message = "Taxa de reciclagem não pode ser negativa")
    @DecimalMax(value = "100.0", message = "Taxa de reciclagem não pode ultrapassar 100%")
    @Column(name = "taxa_reciclagem", nullable = false)
    private Double taxaReciclagem; // percentual

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 2000, message = "Ano deve ser a partir de 2000")
    @Max(value = 2100, message = "Ano inválido")
    @Column(nullable = false)
    private Integer ano;

    // Construtores
    public RegistroResiduo() {}

    public RegistroResiduo(String municipio, String estado, Double quantidadeGerada,
                           Double taxaReciclagem, Integer ano) {
        this.municipio = municipio;
        this.estado = estado;
        this.quantidadeGerada = quantidadeGerada;
        this.taxaReciclagem = taxaReciclagem;
        this.ano = ano;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getQuantidadeGerada() { return quantidadeGerada; }
    public void setQuantidadeGerada(Double quantidadeGerada) { this.quantidadeGerada = quantidadeGerada; }

    public Double getTaxaReciclagem() { return taxaReciclagem; }
    public void setTaxaReciclagem(Double taxaReciclagem) { this.taxaReciclagem = taxaReciclagem; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    @Override
    public String toString() {
        return "RegistroResiduo{" +
                "id=" + id +
                ", municipio='" + municipio + '\'' +
                ", estado='" + estado + '\'' +
                ", quantidadeGerada=" + quantidadeGerada +
                ", taxaReciclagem=" + taxaReciclagem +
                ", ano=" + ano +
                '}';
    }
}
