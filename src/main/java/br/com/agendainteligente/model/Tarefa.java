package br.com.agendainteligente.model;

import br.com.agendainteligente.enums.Prioridade;
import br.com.agendainteligente.enums.StatusTarefa;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa uma tarefa flexível que ainda não tem horário definido.
 * O AgendadorInteligente encaixa essas tarefas nos blocos livres do dia,
 * respeitando prioridade, prazo e duração estimada.
 */
public class Tarefa {

    private Long id;
    private String titulo;
    private String descricao;
    private int duracaoEstimadaMinutos;
    private Prioridade prioridade;
    private LocalDate prazo;
    private StatusTarefa status;
    private String categoria;

    // ===== Construtores =====

    public Tarefa() {}

    public Tarefa(String titulo, int duracaoEstimadaMinutos,
                  Prioridade prioridade, LocalDate prazo) {
        this.titulo = titulo;
        this.duracaoEstimadaMinutos = duracaoEstimadaMinutos;
        this.prioridade = prioridade;
        this.prazo = prazo;
        this.status = StatusTarefa.PENDENTE;
    }

    // ===== Comportamentos do domínio =====

    /**
     * Marca a tarefa como concluída.
     */
    public void marcarComoConcluida() {
        this.status = StatusTarefa.CONCLUIDA;
    }

    /**
     * Reabre uma tarefa concluída, voltando para PENDENTE.
     */
    public void reabrir() {
        this.status = StatusTarefa.PENDENTE;
    }

    /**
     * Marca a tarefa como planejada (alocada num bloco de tempo).
     */
    public void marcarComoPlanejada() {
        this.status = StatusTarefa.PLANEJADA;
    }

    /**
     * Marca a tarefa como atrasada (prazo passou e não foi concluída).
     */
    public void marcarComoAtrasada() {
        this.status = StatusTarefa.ATRASADA;
    }

    /**
     * Calcula uma pontuação de urgência para ordenar tarefas no agendador.
     * Quanto maior o número, mais urgente.
     * Leva em conta prioridade e proximidade do prazo.
     */
    public int calcularUrgencia() {
        int pontos = prioridade != null ? prioridade.getPeso() * 10 : 0;

        if (prazo != null) {
            long diasRestantes = LocalDate.now().until(prazo).getDays();
            if (diasRestantes <= 0) pontos += 50;       // já atrasada
            else if (diasRestantes <= 1) pontos += 30;  // vence amanhã
            else if (diasRestantes <= 3) pontos += 20;  // vence em breve
            else if (diasRestantes <= 7) pontos += 10;  // vence essa semana
        }

        return pontos;
    }

    public void editar(String novoTitulo, String novaDescricao,
                       int novaDuracao, Prioridade novaPrioridade,
                       LocalDate novoPrazo, String novaCategoria) {
        this.titulo = novoTitulo;
        this.descricao = novaDescricao;
        this.duracaoEstimadaMinutos = novaDuracao;
        this.prioridade = novaPrioridade;
        this.prazo = novoPrazo;
        this.categoria = novaCategoria;
    }

    // ===== Getters e Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getDuracaoEstimadaMinutos() { return duracaoEstimadaMinutos; }
    public void setDuracaoEstimadaMinutos(int d) { this.duracaoEstimadaMinutos = d; }

    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }

    public LocalDate getPrazo() { return prazo; }
    public void setPrazo(LocalDate prazo) { this.prazo = prazo; }

    public StatusTarefa getStatus() { return status; }
    public void setStatus(StatusTarefa status) { this.status = status; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    // ===== equals, hashCode e toString =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tarefa t)) return false;
        return Objects.equals(id, t.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Tarefa{titulo='%s', duracao=%dmin, prioridade=%s, status=%s, prazo=%s}"
                .formatted(titulo, duracaoEstimadaMinutos, prioridade, status, prazo);
    }
}