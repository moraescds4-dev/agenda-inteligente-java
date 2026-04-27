package br.com.agendainteligente.repository;

import br.com.agendainteligente.config.DatabaseConfig;
import br.com.agendainteligente.model.ConfiguracaoRotina;

import java.sql.*;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Responsável por salvar e buscar ConfiguracaoRotina no banco SQLite.
 * Cada usuário tem exatamente uma configuração de rotina (relação 1-1).
 */
public class ConfiguracaoRotinaRepository {

    /**
     * Salva a configuração de rotina de um usuário.
     * Se já existir uma configuração para esse usuário, atualiza.
     */
    public ConfiguracaoRotina salvar(ConfiguracaoRotina config,
                                     Long usuarioId) throws SQLException {
        String sql = """
                INSERT INTO configuracoes_rotina
                    (usuario_id, hora_inicio_dia, hora_fim_dia,
                     inicio_almoco, fim_almoco, hora_inicio_descanso)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT(usuario_id) DO UPDATE SET
                    hora_inicio_dia      = excluded.hora_inicio_dia,
                    hora_fim_dia         = excluded.hora_fim_dia,
                    inicio_almoco        = excluded.inicio_almoco,
                    fim_almoco           = excluded.fim_almoco,
                    hora_inicio_descanso = excluded.hora_inicio_descanso
                """;

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, usuarioId);
            stmt.setString(2, config.getHoraInicioDia().toString());
            stmt.setString(3, config.getHoraFimDia().toString());
            stmt.setString(4, config.getInicioAlmoco().toString());
            stmt.setString(5, config.getFimAlmoco().toString());
            stmt.setString(6, config.getHoraInicioDescanso().toString());
            stmt.executeUpdate();

            ResultSet chaves = stmt.getGeneratedKeys();
            if (chaves.next()) {
                config.setId(chaves.getLong(1));
            }
        }
        return config;
    }

    /**
     * Busca a configuração de rotina de um usuário pelo seu id.
     */
    public Optional<ConfiguracaoRotina> buscarPorUsuario(Long usuarioId)
            throws SQLException {

        String sql = """
                SELECT id, hora_inicio_dia, hora_fim_dia,
                       inicio_almoco, fim_almoco, hora_inicio_descanso
                FROM configuracoes_rotina
                WHERE usuario_id = ?
                """;

        Connection conn = DatabaseConfig.getConexao();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    /**
     * Converte uma linha do ResultSet em objeto ConfiguracaoRotina.
     * Os horários são armazenados como texto e convertidos para LocalTime.
     */
    private ConfiguracaoRotina mapear(ResultSet rs) throws SQLException {
        ConfiguracaoRotina config = new ConfiguracaoRotina(
                LocalTime.parse(rs.getString("hora_inicio_dia")),
                LocalTime.parse(rs.getString("hora_fim_dia")),
                LocalTime.parse(rs.getString("inicio_almoco")),
                LocalTime.parse(rs.getString("fim_almoco")),
                LocalTime.parse(rs.getString("hora_inicio_descanso"))
        );
        config.setId(rs.getLong("id"));
        return config;
    }
}