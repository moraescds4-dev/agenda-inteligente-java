package br.com.agendainteligente.repository;

import br.com.agendainteligente.config.DatabaseConfig;
import br.com.agendainteligente.model.Usuario;

import java.sql.*;
import java.util.Optional;

/**
 * Responsável por salvar e buscar Usuario no banco SQLite.
 * No MVP o sistema é mono-usuário — mas o repository já
 * está preparado para multi-usuário no futuro.
 */
public class UsuarioRepository {

    /**
     * Salva um novo usuário e devolve o objeto com o id gerado.
     */
    public Usuario salvar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email) VALUES (?, ?)";

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.executeUpdate();

            ResultSet chaves = stmt.getGeneratedKeys();
            if (chaves.next()) {
                usuario.setId(chaves.getLong(1));
            }
        }
        return usuario;
    }

    /**
     * Busca o primeiro usuário cadastrado (MVP mono-usuário).
     * Retorna Optional.empty() se ainda não houver usuário.
     */
    public Optional<Usuario> buscarPrimeiro() throws SQLException {
        String sql = "SELECT id, nome, email FROM usuarios LIMIT 1";

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    /**
     * Busca um usuário pelo id.
     */
    public Optional<Usuario> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, nome, email FROM usuarios WHERE id = ?";

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    /**
     * Atualiza nome e email de um usuário existente.
     */
    public void atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, email = ? WHERE id = ?";

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setLong(3, usuario.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Converte uma linha do ResultSet em objeto Usuario.
     */
    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getString("email")
        );
    }
}