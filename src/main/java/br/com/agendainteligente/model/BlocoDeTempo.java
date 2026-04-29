package br.com.agendainteligente.model;

import br.com.agendainteligente.enums.StatusBloco;
import br.com.agendainteligente.enums.TipoBloco;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representa uma fatia de tempo dentro da agenda diária.
 * É a unidade básica que o AgendadorInteligente usa para
 * encaixar tarefas e marcar compromissos, almoço e descanso.
 */
public class BlocoDeTempo {

    private Long id;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private TipoBloco tipoBloco;
    private StatusBloco status;
    private Tarefa tarefa;
    private CompromissoFixo compromisso;

    // ===== Construtores =====

    public BlocoDeTempo() {}

    public BlocoDeTempo(LocalTime horaInicio, LocalTime horaFim, TipoBloco tipoBloco) {
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.tipoBloco = tipoBloco;
        this.status = StatusBloco.DISPONIVEL;
    }

    // ===== Comportamentos do domínio =====

    /**
     * Calcula a duração do bloco em minutos.
     */
    public long calcularDuracaoMinutos() {
        if (horaInicio == null || horaFim == null) return 0;
        return Duration.between(horaInicio, horaFim).toMinutes();
    }

    /**
     * Retorna true se o bloco está livre para receber uma tarefa.
     */
    public boolean estaLivre() {
        return tipoBloco == TipoBloco.LIVRE && status == StatusBloco.DISPONIVEL;
    }

    /**
     * Retorna true se o bloco já está ocupado.
     */
    public boolean estaOcupado() {
        return status == StatusBloco.OCUPADO;
    }

    /**
     * Associa uma tarefa a este bloco e o marca como ocupado.
     */
    public void associarTarefa(Tarefa tarefa) {
        Objects.requireNonNull(tarefa, "Tarefa não pode ser nula");
        this.tarefa = tarefa;
        this.compromisso = null;
        this.tipoBloco = TipoBloco.TAREFA;
        this.status = StatusBloco.OCUPADO;
    }

    /**
     * Associa um compromisso fixo a este bloco e o marca como ocupado.
     */
    public void associarCompromisso(CompromissoFixo compromisso) {
        Objects.requireNonNull(compromisso, "Compromisso não pode ser nulo");
        this.compromisso = compromisso;
        this.tarefa = null;
        this.tipoBloco = TipoBloco.COMPROMISSO;
        this.status = StatusBloco.OCUPADO;
    }

    /**
     * Verifica se uma tarefa de X minutos cabe neste bloco.
     */
    public boolean comportaTarefa(int duracaoMinutos) {
        return estaLivre() && calcularDuracaoMinutos() >= duracaoMinutos;
    }

    // ===== Getters e Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }

    public TipoBloco getTipoBloco() { return tipoBloco; }
    public void setTipoBloco(TipoBloco tipoBloco) { this.tipoBloco = tipoBloco; }

    public StatusBloco getStatus() { return status; }
    public void setStatus(StatusBloco status) { this.status = status; }

    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }

    public CompromissoFixo getCompromisso() { return compromisso; }
    public void setCompromisso(CompromissoFixo compromisso) { this.compromisso = compromisso; }

    // ===== equals, hashCode e toString =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlocoDeTempo b)) return false;
        // Se ambos têm id, compara por id (blocos persistidos)
        if (id != null && b.id != null) return id.equals(b.id);
        // Senão, compara por identidade de referência (blocos em memória)
        return false;
    }

    @Override
    public int hashCode() {
        // Usa identidade do objeto quando não há id, evitando colisão entre blocos novos
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "BlocoDeTempo{%s-%s, tipo=%s, status=%s}"
                .formatted(horaInicio, horaFim, tipoBloco, status);
    }
}