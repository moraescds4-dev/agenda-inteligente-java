package br.com.agendainteligente.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Responsável por inicializar o banco de dados na primeira execução.
 * Lê o arquivo schema.sql e executa cada comando CREATE TABLE.
 * Seguro para chamar múltiplas vezes — usa IF NOT EXISTS.
 */
public class DatabaseInitializer {

    private DatabaseInitializer() {}

    /**
     * Lê o schema.sql e cria as tabelas se ainda não existirem.
     * Deve ser chamado uma vez só, na inicialização do app.
     */
    public static void inicializar() {
        try {
            String sql = lerScript();
            executarScript(sql);
            System.out.println("✅ Banco inicializado em: " + DatabaseConfig.getCaminhoDb());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar banco de dados: " + e.getMessage(), e);
        }
    }

    /**
     * Lê o conteúdo do schema.sql dentro de resources/db/.
     */
    private static String lerScript() throws IOException {
        InputStream stream = DatabaseInitializer.class
                .getResourceAsStream("/db/schema.sql");

        if (stream == null) {
            throw new IOException("Arquivo schema.sql não encontrado em resources/db/");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * Divide o script em comandos individuais e executa cada um.
     */
    private static void executarScript(String sql) throws SQLException {
        Connection conn = DatabaseConfig.getConexao();

        // Remove comentários e divide por ";"
        String[] comandos = sql.split(";");

        try (Statement stmt = conn.createStatement()) {
            for (String comando : comandos) {
                // Remove comentários linha a linha
                String limpo = java.util.Arrays.stream(comando.split("\n"))
                        .filter(linha -> !linha.strip().startsWith("--"))
                        .collect(java.util.stream.Collectors.joining("\n"))
                        .strip();

                if (!limpo.isEmpty()) {
                    stmt.execute(limpo);
                }
            }
        }
    }
}