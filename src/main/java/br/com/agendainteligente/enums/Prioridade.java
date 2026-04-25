package br.com.agendainteligente.enums;

/**
 * Nível de prioridade de uma tarefa flexível.
 * Usado pelo AgendadorInteligente para decidir a ordem de encaixe
 * das tarefas nos blocos livres do dia.
 */
public enum Prioridade {
    BAIXA(1, "Baixa"),
    MEDIA(2, "Média"),
    ALTA(3, "Alta");

    private final int peso;
    private final String descricao;

    Prioridade(int peso, String descricao) {
        this.peso = peso;
        this.descricao = descricao;
    }

    public int getPeso() {
        return peso;
    }

    public String getDescricao() {
        return descricao;
    }
}