package br.com.agendainteligente.model;

import br.com.agendainteligente.enums.TipoCompromisso;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.Objects;

/**
 * Representa um evento com horário fixo e definido (aula, reunião, consulta).
 * Esses compromissos são bloqueados na agenda antes de qualquer tarefa ser encaixada.
 */
public class CompromissoFixo {

    private Long id;
    private String titulo;
    private String descricao;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private TipoCompromisso tipo;

    // ===== Construtores =====

    public CompromissoFixo() {}

    public CompromissoFixo(String titulo, LocalDate data,
                           LocalTime horaInicio, LocalTime horaFim,
                           TipoCompromisso tipo) {
        this.titulo = titulo;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.tipo = tipo;
    }

    // ===== Comportamentos do domínio =====

    /**
     * Valida se o horário do compromisso é coerente (início antes do fim).
     */
    public boolean validarHorario() {
        if (horaInicio == null || horaFim == null) return false;
        return horaInicio.isBefore(horaFim);
    }

    /**
     * Calcula a duração do compromisso em minutos.
     */
    public long calcularDuracaoMinutos() {
        if (!validarHorario()) return 0;
        return Duration.between(horaInicio, horaFim).toMinutes();
    }

    public void editar(String novoTitulo, String novaDescricao,
                       LocalTime novaHoraInicio, LocalTime novaHoraFim) {
        this.titulo = novoTitulo;
        this.descricao = novaDescricao;
        this.horaInicio = novaHoraInicio;
        this.horaFim = novaHoraFim;
    }

    // ===== Getters e Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }

    public TipoCompromisso getTipo() { return tipo; }
    public void setTipo(TipoCompromisso tipo) { this.tipo = tipo; }

    // ===== equals, hashCode e toString =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompromissoFixo c)) return false;
        return Objects.equals(id, c.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "CompromissoFixo{titulo='%s', data=%s, %s-%s, tipo=%s}"
                .formatted(titulo, data, horaInicio, horaFim, tipo);
    }
}