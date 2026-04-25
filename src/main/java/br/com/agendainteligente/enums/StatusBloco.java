package br.com.agendainteligente.enums;

/**
 * Estado de ocupação de um bloco de tempo na agenda.
 *
 * DISPONIVEL → Bloco livre, ainda pode receber uma tarefa ou compromisso.
 * OCUPADO    → Bloco já alocado para uma tarefa, compromisso, almoço ou descanso.
 * FINALIZADO → Bloco cujo horário já passou e foi efetivamente cumprido.
 */
public enum StatusBloco {
    DISPONIVEL("Disponível"),
    OCUPADO("Ocupado"),
    FINALIZADO("Finalizado");

    private final String descricao;

    StatusBloco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}