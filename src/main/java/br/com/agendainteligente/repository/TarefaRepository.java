package br.com.agendainteligente.repository;

import br.com.agendainteligente.config.DatabaseConfig;
import br.com.agendainteligente.enums.Prioridade;
import br.com.agendainteligente.enums.StatusTarefa;
import br.com.agendainteligente.model.Tarefa;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Responsável por salvar, buscar, atualizar e excluir
 * Tarefas no banco SQLite.
 */
public class TarefaRepository {

    /**
     * Salva uma nova tarefa e devolve o objeto com o id gerado.
     */
    public Tarefa salvar(Tarefa tarefa, Long usuarioId) throws SQLException {
        String sql = """
                INSERT INTO tarefas
                    (usuario_id, titulo, descricao, duracao_estimada_minutos,
                     prioridade, prazo, status, categoria)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, usuarioId);
            stmt.setString(2, tarefa.getTitulo());
            stmt.setString(3, tarefa.getDescricao());
            stmt.setInt(4, tarefa.getDuracaoEstimadaMinutos());
            stmt.setString(5, tarefa.getPrioridade().name());
            stmt.setString(6, tarefa.getPrazo() != null
                    ? tarefa.getPrazo().toString() : null);
            stmt.setString(7, tarefa.getStatus().name());
            stmt.setString(8, tarefa.getCategoria());
            stmt.executeUpdate();

            ResultSet chaves = stmt.getGeneratedKeys();
            if (chaves.next()) {
                tarefa.setId(chaves.getLong(1));
            }
        }
        return tarefa;
    }

    /**
     * Busca todas as tarefas pendentes ou atrasadas de um usuário.
     * São exatamente essas que o AgendadorInteligente vai encaixar.
     */
    public List<Tarefa> buscarPendentes(Long usuarioId) throws SQLException {
        String sql = """
                SELECT id, titulo, descricao, duracao_estimada_minutos,
                       prioridade, prazo, status, categoria
                FROM tarefas
                WHERE usuario_id = ?
                  AND status IN ('PENDENTE', 'ATRASADA')
                ORDER BY prioridade DESC, prazo ASC
                """;

        return executarConsulta(sql, usuarioId);
    }

    /**
     * Busca todas as tarefas de um usuário, independente do status.
     */
    public List<Tarefa> buscarTodas(Long usuarioId) throws SQLException {
        String sql = """
                SELECT id, titulo, descricao, duracao_estimada_minutos,
                       prioridade, prazo, status, categoria
                FROM tarefas
                WHERE usuario_id = ?
                ORDER BY status, prioridade DESC
                """;

        return executarConsulta(sql, usuarioId);
    }

    /**
     * Busca uma tarefa pelo id.
     */
    public Optional<Tarefa> buscarPorId(Long id) throws SQLException {
        String sql = """
                SELECT id, titulo, descricao, duracao_estimada_minutos,
                       prioridade, prazo, status, categoria
                FROM tarefas WHERE id = ?
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
     * Atualiza todos os campos de uma tarefa existente.
     */
    public void atualizar(Tarefa tarefa) throws SQLException {
        String sql = """
                UPDATE tarefas SET
                    titulo                   = ?,
                    descricao                = ?,
                    duracao_estimada_minutos = ?,
                    prioridade               = ?,
                    prazo                    = ?,
                    status                   = ?,
                    categoria                = ?
                WHERE id = ?
                """;

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, tarefa.getDuracaoEstimadaMinutos());
            stmt.setString(4, tarefa.getPrioridade().name());
            stmt.setString(5, tarefa.getPrazo() != null
                    ? tarefa.getPrazo().toString() : null);
            stmt.setString(6, tarefa.getStatus().name());
            stmt.setString(7, tarefa.getCategoria());
            stmt.setLong(8, tarefa.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Atualiza apenas o status de uma tarefa.
     * Usado pelo agendador após encaixar ou concluir uma tarefa.
     */
    public void atualizarStatus(Long id, StatusTarefa novoStatus)
            throws SQLException {
        String sql = "UPDATE tarefas SET status = ? WHERE id = ?";

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoStatus.name());
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Exclui uma tarefa pelo id.
     */
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM tarefas WHERE id = ?";

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    // ===== Métodos auxiliares =====

    private List<Tarefa> executarConsulta(String sql,
                                          Long usuarioId) throws SQLException {
        List<Tarefa> lista = new ArrayList<>();
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
     * Converte uma linha do ResultSet em objeto Tarefa.
     */
    private Tarefa mapear(ResultSet rs) throws SQLException {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(rs.getLong("id"));
        tarefa.setTitulo(rs.getString("titulo"));
        tarefa.setDescricao(rs.getString("descricao"));
        tarefa.setDuracaoEstimadaMinutos(rs.getInt("duracao_estimada_minutos"));
        tarefa.setPrioridade(Prioridade.valueOf(rs.getString("prioridade")));
        tarefa.setStatus(StatusTarefa.valueOf(rs.getString("status")));
        tarefa.setCategoria(rs.getString("categoria"));

        String prazo = rs.getString("prazo");
        if (prazo != null) {
            tarefa.setPrazo(LocalDate.parse(prazo));
        }

        return tarefa;
    }
}