package br.com.agendainteligente.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um lembrete do sistema.
 * Pode estar associado a uma Tarefa ou a um CompromissoFixo.
 * Responsável por avisar o usuário antes de uma atividade acontecer.
 */
public class Lembrete {

    private Long id;
    private String mensagem;
    private LocalDateTime horario;
    private boolean ativo;
    private Tarefa tarefa;
    private CompromissoFixo compromisso;

    // ===== Construtores =====

    public Lembrete() {}

    public Lembrete(String mensagem, LocalDateTime horario) {
        this.mensagem = mensagem;
        this.horario = horario;
        this.ativo = true;
    }

    // ===== Comportamentos do domínio =====

    /**
     * Ativa o lembrete para ser disparado no horário definido.
     */
    public void ativar() {
        this.ativo = true;
    }

    /**
     * Desativa o lembrete sem excluí-lo.
     */
    public void desativar() {
        this.ativo = false;
    }

    /**
     * Simula o envio do lembrete (a integração real será feita na camada de serviço).
     */
    public void enviar() {
        if (!ativo) return;
        System.out.println("🔔 Lembrete: " + mensagem + " às " + horario);
    }

    /**
     * Associa este lembrete a uma tarefa.
     */
    public void associarTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
        this.compromisso = null;
    }

    /**
     * Associa este lembrete a um compromisso fixo.
     */
    public void associarCompromisso(CompromissoFixo compromisso) {
        this.compromisso = compromisso;
        this.tarefa = null;
    }

    // ===== Getters e Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getHorario() { return horario; }
    public void setHorario(LocalDateTime horario) { this.horario = horario; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Tarefa getTarefa() { return tarefa; }
    public void setTarefa(Tarefa tarefa) { this.tarefa = tarefa; }

    public CompromissoFixo getCompromisso() { return compromisso; }
    public void setCompromisso(CompromissoFixo compromisso) { this.compromisso = compromisso; }

    // ===== equals, hashCode e toString =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lembrete l)) return false;
        return Objects.equals(id, l.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Lembrete{mensagem='%s', horario=%s, ativo=%b}"
                .formatted(mensagem, horario, ativo);
    }
}