package br.com.agendainteligente.model;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Guarda as preferências de rotina do usuário.
 * O AgendadorInteligente consulta essa classe para saber
 * onde pode e onde não pode encaixar tarefas no dia.
 */
public class ConfiguracaoRotina {

    private Long id;
    private LocalTime horaInicioDia;
    private LocalTime horaFimDia;
    private LocalTime inicioAlmoco;
    private LocalTime fimAlmoco;
    private LocalTime horaInicioDescanso;

    // ===== Construtores =====

    public ConfiguracaoRotina() {}

    public ConfiguracaoRotina(LocalTime horaInicioDia, LocalTime horaFimDia,
                              LocalTime inicioAlmoco, LocalTime fimAlmoco,
                              LocalTime horaInicioDescanso) {
        this.horaInicioDia = horaInicioDia;
        this.horaFimDia = horaFimDia;
        this.inicioAlmoco = inicioAlmoco;
        this.fimAlmoco = fimAlmoco;
        this.horaInicioDescanso = horaInicioDescanso;
    }

    // ===== Comportamentos do domínio =====

    public void definirHorarioAlmoco(LocalTime inicio, LocalTime fim) {
        Objects.requireNonNull(inicio, "Início do almoço não pode ser nulo");
        Objects.requireNonNull(fim, "Fim do almoço não pode ser nulo");
        if (!inicio.isBefore(fim)) {
            throw new IllegalArgumentException("Início do almoço deve ser antes do fim");
        }
        this.inicioAlmoco = inicio;
        this.fimAlmoco = fim;
    }

    public void definirHorarioDescanso(LocalTime horaInicio) {
        Objects.requireNonNull(horaInicio, "Hora de descanso não pode ser nula");
        this.horaInicioDescanso = horaInicio;
    }

    public void definirHorarioInicioDia(LocalTime hora) {
        Objects.requireNonNull(hora, "Hora de início do dia não pode ser nula");
        this.horaInicioDia = hora;
    }

    public void definirHorarioFimDia(LocalTime hora) {
        Objects.requireNonNull(hora, "Hora de fim do dia não pode ser nula");
        this.horaFimDia = hora;
    }

    // ===== Getters e Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalTime getHoraInicioDia() { return horaInicioDia; }
    public void setHoraInicioDia(LocalTime horaInicioDia) { this.horaInicioDia = horaInicioDia; }

    public LocalTime getHoraFimDia() { return horaFimDia; }
    public void setHoraFimDia(LocalTime horaFimDia) { this.horaFimDia = horaFimDia; }

    public LocalTime getInicioAlmoco() { return inicioAlmoco; }
    public void setInicioAlmoco(LocalTime inicioAlmoco) { this.inicioAlmoco = inicioAlmoco; }

    public LocalTime getFimAlmoco() { return fimAlmoco; }
    public void setFimAlmoco(LocalTime fimAlmoco) { this.fimAlmoco = fimAlmoco; }

    public LocalTime getHoraInicioDescanso() { return horaInicioDescanso; }
    public void setHoraInicioDescanso(LocalTime h) { this.horaInicioDescanso = h; }

    // ===== toString =====

    @Override
    public String toString() {
        return "ConfiguracaoRotina{dia=%s-%s, almoco=%s-%s, descanso=%s}"
                .formatted(horaInicioDia, horaFimDia, inicioAlmoco, fimAlmoco, horaInicioDescanso);
    }
}