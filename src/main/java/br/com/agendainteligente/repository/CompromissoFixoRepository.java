package br.com.agendainteligente.repository;

import br.com.agendainteligente.config.DatabaseConfig;
import br.com.agendainteligente.enums.TipoCompromisso;
import br.com.agendainteligente.model.CompromissoFixo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Responsável por salvar, buscar, atualizar e excluir
 * CompromissoFixo no banco SQLite.
 */
public class CompromissoFixoRepository {

    /**
     * Salva um novo compromisso e devolve o objeto com o id gerado.
     */
    public CompromissoFixo salvar(CompromissoFixo compromisso,
                                  Long usuarioId) throws SQLException {
        String sql = """
                INSERT INTO compromissos_fixos
                    (usuario_id, titulo, descricao, data,
                     hora_inicio, hora_fim, tipo)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, usuarioId);
            stmt.setString(2, compromisso.getTitulo());
            stmt.setString(3, compromisso.getDescricao());
            stmt.setString(4, compromisso.getData().toString());
            stmt.setString(5, compromisso.getHoraInicio().toString());
            stmt.setString(6, compromisso.getHoraFim().toString());
            stmt.setString(7, compromisso.getTipo().name());
            stmt.executeUpdate();

            ResultSet chaves = stmt.getGeneratedKeys();
            if (chaves.next()) {
                compromisso.setId(chaves.getLong(1));
            }
        }
        return compromisso;
    }

    /**
     * Busca todos os compromissos de um usuário em uma data específica.
     * Usado pelo AgendadorInteligente para montar a agenda do dia.
     */
    public List<CompromissoFixo> buscarPorData(Long usuarioId,
                                               LocalDate data) throws SQLException {
        String sql = """
                SELECT id, titulo, descricao, data,
                       hora_inicio, hora_fim, tipo
                FROM compromissos_fixos
                WHERE usuario_id = ?
                  AND data = ?
                ORDER BY hora_inicio ASC
                """;

        List<CompromissoFixo> lista = new ArrayList<>();
        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            stmt.setString(2, data.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /**
     * Busca todos os compromissos de um usuário, de todas as datas.
     */
    public List<CompromissoFixo> buscarTodos(Long usuarioId) throws SQLException {
        String sql = """
                SELECT id, titulo, descricao, data,
                       hora_inicio, hora_fim, tipo
                FROM compromissos_fixos
                WHERE usuario_id = ?
                ORDER BY data ASC, hora_inicio ASC
                """;

        List<CompromissoFixo> lista = new ArrayList<>();
        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /**
     * Busca um compromisso pelo id.
     */
    public Optional<CompromissoFixo> buscarPorId(Long id) throws SQLException {
        String sql = """
                SELECT id, titulo, descricao, data,
                       hora_inicio, hora_fim, tipo
                FROM compromissos_fixos WHERE id = ?
                """;

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
     * Atualiza todos os campos de um compromisso existente.
     */
    public void atualizar(CompromissoFixo compromisso) throws SQLException {
        String sql = """
                UPDATE compromissos_fixos SET
                    titulo      = ?,
                    descricao   = ?,
                    data        = ?,
                    hora_inicio = ?,
                    hora_fim    = ?,
                    tipo        = ?
                WHERE id = ?
                """;

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, compromisso.getTitulo());
            stmt.setString(2, compromisso.getDescricao());
            stmt.setString(3, compromisso.getData().toString());
            stmt.setString(4, compromisso.getHoraInicio().toString());
            stmt.setString(5, compromisso.getHoraFim().toString());
            stmt.setString(6, compromisso.getTipo().name());
            stmt.setLong(7, compromisso.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Exclui um compromisso pelo id.
     */
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM compromissos_fixos WHERE id = ?";

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Converte uma linha do ResultSet em objeto CompromissoFixo.
     */
    private CompromissoFixo mapear(ResultSet rs) throws SQLException {
        CompromissoFixo compromisso = new CompromissoFixo();
        compromisso.setId(rs.getLong("id"));
        compromisso.setTitulo(rs.getString("titulo"));
        compromisso.setDescricao(rs.getString("descricao"));
        compromisso.setData(LocalDate.parse(rs.getString("data")));
        compromisso.setHoraInicio(LocalTime.parse(rs.getString("hora_inicio")));
        compromisso.setHoraFim(LocalTime.parse(rs.getString("hora_fim")));
        compromisso.setTipo(TipoCompromisso.valueOf(rs.getString("tipo")));
        return compromisso;
    }
}