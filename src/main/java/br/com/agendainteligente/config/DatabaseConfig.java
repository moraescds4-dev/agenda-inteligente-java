package br.com.agendainteligente.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gerencia a conexão com o banco de dados SQLite.
 * Cria o arquivo .db na pasta do usuário automaticamente
 * se ainda não existir.
 */
public class DatabaseConfig {

    private static final String PASTA_DB = System.getProperty("user.home")
            + "/agenda-inteligente";
    private static final String CAMINHO_DB = PASTA_DB + "/agenda.db";
    private static final String URL = "jdbc:sqlite:" + CAMINHO_DB;

    private static Connection conexao;

    // Construtor privado — ninguém instancia essa classe
    private DatabaseConfig() {}

    /**
     * Retorna a conexão ativa com o banco.
     * Se não houver conexão aberta, cria uma nova.
     */
    public static Connection getConexao() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            criarPastaSeNecessario();
            conexao = DriverManager.getConnection(URL);
            configurarPragmas(conexao);
        }
        return conexao;
    }

    /**
     * Fecha a conexão com o banco.
     */
    public static void fecharConexao() {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    /**
     * Cria a pasta do banco se ainda não existir.
     */
    private static void criarPastaSeNecessario() {
        try {
            Path pasta = Paths.get(PASTA_DB);
            if (!Files.exists(pasta)) {
                Files.createDirectories(pasta);
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar a pasta do banco: " + e.getMessage());
        }
    }

    /**
     * Configura pragmas do SQLite para melhor performance e integridade.
     */
    private static void configurarPragmas(Connection conn) throws SQLException {
        try (var stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute("PRAGMA journal_mode = WAL");
        }
    }

    public static String getCaminhoDb() {
        return CAMINHO_DB;
    }
}