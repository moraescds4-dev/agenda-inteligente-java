package br.com.agendainteligente.enums;

/**
 * Categoria de um compromisso fixo (com hora marcada).
 * Usado para classificar e filtrar compromissos na agenda.
 */
public enum TipoCompromisso {
    AULA("Aula"),
    REUNIAO("Reunião"),
    CONSULTA("Consulta"),
    OUTRO("Outro");

    private final String descricao;

    TipoCompromisso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}