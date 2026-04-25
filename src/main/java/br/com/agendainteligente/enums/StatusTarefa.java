package br.com.agendainteligente.enums;

/**
 * Estado atual de uma tarefa no ciclo de vida do sistema.
 *
 * PENDENTE  → Tarefa cadastrada, ainda não foi alocada na agenda.
 * PLANEJADA → Tarefa alocada em um bloco de tempo da agenda diária.
 * CONCLUIDA → Tarefa marcada como feita pelo usuário.
 * ATRASADA  → Tarefa cujo prazo já passou e ainda não foi concluída.
 */
public enum StatusTarefa {
    PENDENTE("Pendente"),
    PLANEJADA("Planejada"),
    CONCLUIDA("Concluída"),
    ATRASADA("Atrasada");

    private final String descricao;

    StatusTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}