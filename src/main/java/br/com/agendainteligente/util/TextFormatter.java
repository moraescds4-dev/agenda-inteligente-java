package br.com.agendainteligente.util;

import java.util.Set;

/**
 * Utilitário para padronizar textos exibidos na interface.
 * Converte qualquer entrada do usuário para Title Case profissional.
 */
public final class TextFormatter {

    // Palavras que ficam minúsculas no meio do título (estilo jornal/livro)
    private static final Set<String> PALAVRAS_MINUSCULAS = Set.of(
            "de", "da", "do", "das", "dos",
            "e", "ou",
            "a", "o", "as", "os",
            "para", "com", "em", "na", "no", "nas", "nos",
            "à", "às", "ao", "aos"
    );

    private TextFormatter() {
        // utility class — não instanciar
    }

    /**
     * Converte texto para Title Case respeitando preposições e artigos.
     * Exemplos:
     *   "MERCADO"              → "Mercado"
     *   "REVISÃO DE ESTUDOS"   → "Revisão de Estudos"
     *   "trabalhar projeto 3"  → "Trabalhar Projeto 3"
     *   "Aula de JAVA"         → "Aula de Java"
     */
    public static String titulo(String texto) {
        if (texto == null || texto.isBlank()) return "";

        String[] palavras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < palavras.length; i++) {
            String p = palavras[i];
            if (p.isEmpty()) continue;

            // Primeira palavra sempre capitalizada;
            // demais ficam minúsculas se forem preposição/artigo
            if (i > 0 && PALAVRAS_MINUSCULAS.contains(p)) {
                resultado.append(p);
            } else {
                resultado.append(Character.toUpperCase(p.charAt(0)));
                if (p.length() > 1) resultado.append(p.substring(1));
            }

            if (i < palavras.length - 1) resultado.append(' ');
        }

        return resultado.toString();
    }

    /**
     * Categoria curta de tarefa: tudo MAIÚSCULO, sem acento mexido.
     * Exemplos: "trabalho" → "TRABALHO", "Estudos" → "ESTUDOS"
     */
    public static String categoria(String texto) {
        if (texto == null || texto.isBlank()) return "";
        return texto.trim().toUpperCase();
    }
}