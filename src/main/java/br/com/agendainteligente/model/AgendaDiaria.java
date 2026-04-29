package br.com.agendainteligente.model;

import br.com.agendainteligente.enums.TipoBloco;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa a agenda organizada de um dia específico.
 * É formada por uma lista de BlocoDeTempo que cobre todo o período
 * ativo do dia: compromissos, tarefas, almoço, descanso e blocos livres.
 */
public class AgendaDiaria {

    private Long id;
    private LocalDate data;
    private final List<BlocoDeTempo> blocos = new ArrayList<>();

    // ===== Construtores =====

    public AgendaDiaria() {}

    public AgendaDiaria(LocalDate data) {
        this.data = data;
    }

    // ===== Comportamentos do domínio =====

    /**
     * Adiciona um bloco de tempo à agenda do dia.
     */
    public void adicionarBloco(BlocoDeTempo bloco) {
        Objects.requireNonNull(bloco, "Bloco não pode ser nulo");
        blocos.add(bloco);
    }

    /**
     * Remove um bloco de tempo da agenda.
     */
    public void removerBloco(BlocoDeTempo bloco) {
        blocos.remove(bloco);
    }

    public void limparBlocos() {
        blocos.clear();
    }

    /**
     * Retorna apenas os blocos que estão livres para receber tarefas.
     */
    public List<BlocoDeTempo> listarBlocosLivres() {
        return blocos.stream()
                .filter(BlocoDeTempo::estaLivre)
                .toList();
    }

    /**
     * Retorna apenas os blocos que já estão ocupados.
     */
    public List<BlocoDeTempo> listarBlocosOcupados() {
        return blocos.stream()
                .filter(BlocoDeTempo::estaOcupado)
                .toList();
    }

    /**
     * Retorna os blocos de um tipo específico (ex: só ALMOCO, só COMPROMISSO).
     */
    public List<BlocoDeTempo> listarBlocosPorTipo(TipoBloco tipo) {
        return blocos.stream()
                .filter(b -> b.getTipoBloco() == tipo)
                .toList();
    }

    /**
     * Retorna o próximo bloco livre disponível, ou null se não houver.
     * Usado pelo agendador para encaixar a próxima tarefa.
     */
    public BlocoDeTempo obterProximoBlocoLivre() {
        return blocos.stream()
                .filter(BlocoDeTempo::estaLivre)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retorna a próxima atividade do dia (bloco ocupado mais próximo do horário atual).
     */
    public BlocoDeTempo obterProximaAtividade() {
        return blocos.stream()
                .filter(BlocoDeTempo::estaOcupado)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retorna true se ainda há blocos livres na agenda.
     */
    public boolean temBlocosLivres() {
        return blocos.stream().anyMatch(BlocoDeTempo::estaLivre);
    }

    // ===== Getters e Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public List<BlocoDeTempo> getBlocos() {
        return Collections.unmodifiableList(blocos);
    }

    // ===== equals, hashCode e toString =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgendaDiaria a)) return false;
        return Objects.equals(id, a.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "AgendaDiaria{data=%s, blocos=%d}"
                .formatted(data, blocos.size());
    }
}