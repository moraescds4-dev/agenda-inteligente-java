package br.com.agendainteligente.enums;

/**
 * Natureza de um bloco de tempo dentro da agenda diária.
 *
 * LIVRE       → Espaço aberto, disponível para encaixar tarefas.
 * COMPROMISSO → Bloco ocupado por um compromisso fixo (aula, reunião).
 * TAREFA      → Bloco ocupado por uma tarefa flexível alocada pelo agendador.
 * ALMOCO      → Bloco reservado para a refeição.
 * DESCANSO    → Bloco reservado para descanso noturno.
 */
public enum TipoBloco {
    LIVRE("Livre"),
    COMPROMISSO("Compromisso"),
    TAREFA("Tarefa"),
    ALMOCO("Almoço"),
    DESCANSO("Descanso");

    private final String descricao;

    TipoBloco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}