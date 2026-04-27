-- =====================================================
-- AGENDA INTELIGENTE — Script de criação do banco
-- =====================================================

-- Tabela de usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    nome    TEXT    NOT NULL,
    email   TEXT
);

-- Tabela de configuração da rotina
CREATE TABLE IF NOT EXISTS configuracoes_rotina (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id           INTEGER NOT NULL UNIQUE,
    hora_inicio_dia      TEXT    NOT NULL,
    hora_fim_dia         TEXT    NOT NULL,
    inicio_almoco        TEXT    NOT NULL,
    fim_almoco           TEXT    NOT NULL,
    hora_inicio_descanso TEXT    NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabela de compromissos fixos
CREATE TABLE IF NOT EXISTS compromissos_fixos (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id  INTEGER NOT NULL,
    titulo      TEXT    NOT NULL,
    descricao   TEXT,
    data        TEXT    NOT NULL,
    hora_inicio TEXT    NOT NULL,
    hora_fim    TEXT    NOT NULL,
    tipo        TEXT    NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabela de tarefas flexíveis
CREATE TABLE IF NOT EXISTS tarefas (
    id                       INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario_id               INTEGER NOT NULL,
    titulo                   TEXT    NOT NULL,
    descricao                TEXT,
    duracao_estimada_minutos INTEGER NOT NULL,
    prioridade               TEXT    NOT NULL,
    prazo                    TEXT,
    status                   TEXT    NOT NULL DEFAULT 'PENDENTE',
    categoria                TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabela de lembretes
CREATE TABLE IF NOT EXISTS lembretes (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    mensagem       TEXT    NOT NULL,
    horario        TEXT    NOT NULL,
    ativo          INTEGER NOT NULL DEFAULT 1,
    tarefa_id      INTEGER,
    compromisso_id INTEGER,
    FOREIGN KEY (tarefa_id)      REFERENCES tarefas(id),
    FOREIGN KEY (compromisso_id) REFERENCES compromissos_fixos(id)
);