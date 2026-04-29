package br.com.agendainteligente.service;

import br.com.agendainteligente.model.*;
import br.com.agendainteligente.repository.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Serviço principal de integração.
 * Une o banco de dados com o AgendadorInteligenteService.
 * É a única classe que os Controllers vão precisar conhecer.
 */
public class AgendaService {

    private final UsuarioRepository usuarioRepository;
    private final ConfiguracaoRotinaRepository rotinaRepository;
    private final TarefaRepository tarefaRepository;
    private final CompromissoFixoRepository compromissoRepository;

    private Usuario usuarioAtivo;

    public AgendaService() {
        this.usuarioRepository       = new UsuarioRepository();
        this.rotinaRepository        = new ConfiguracaoRotinaRepository();
        this.tarefaRepository        = new TarefaRepository();
        this.compromissoRepository   = new CompromissoFixoRepository();
    }

    // ===== Inicialização =====

    /**
     * Carrega o usuário ativo do banco.
     * Se não existir nenhum, cria um usuário padrão automaticamente.
     * Deve ser chamado uma vez na inicialização do app.
     */
    public Usuario inicializar(String nomepadrao) throws SQLException {
        Optional<Usuario> encontrado = usuarioRepository.buscarPrimeiro();

        if (encontrado.isPresent()) {
            usuarioAtivo = encontrado.get();
        } else {
            usuarioAtivo = usuarioRepository.salvar(new Usuario(nomepadrao, ""));
        }

        return usuarioAtivo;
    }

    // ===== Agenda do dia =====

    /**
     * Gera a agenda inteligente para uma data.
     * Busca compromissos e tarefas do banco e monta tudo automaticamente.
     */
    public AgendaDiaria gerarAgenda(LocalDate data) throws SQLException {
        validarUsuarioAtivo();

        ConfiguracaoRotina rotina = rotinaRepository
                .buscarPorUsuario(usuarioAtivo.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Configure sua rotina antes de gerar a agenda."));

        List<CompromissoFixo> compromissos = compromissoRepository
                .buscarPorData(usuarioAtivo.getId(), data);

        // LOG TEMPORÁRIO — mostra no console o que foi encontrado
        System.out.println(">>> Data buscada: " + data);
        System.out.println(">>> Compromissos encontrados: " + compromissos.size());
        compromissos.forEach(c -> System.out.println("    - " + c.getTitulo()
                + " | " + c.getData() + " | " + c.getHoraInicio()));

        List<Tarefa> tarefas = tarefaRepository
                .buscarPendentes(usuarioAtivo.getId());

        AgendadorInteligenteService agendador =
                new AgendadorInteligenteService(rotina);

        return agendador.gerarAgendaDiaria(data, compromissos, tarefas);
    }

    /**
     * Sugere o que o usuário deve fazer agora.
     */
    public Tarefa sugerirProximaTarefa(LocalDate data) throws SQLException {
        AgendaDiaria agenda = gerarAgenda(data);
        ConfiguracaoRotina rotina = rotinaRepository
                .buscarPorUsuario(usuarioAtivo.getId())
                .orElseThrow();

        AgendadorInteligenteService agendador =
                new AgendadorInteligenteService(rotina);

        return agendador.sugerirProximaTarefa(agenda);
    }

    // ===== Tarefas =====

    /**
     * Cadastra uma nova tarefa no banco.
     */
    public Tarefa cadastrarTarefa(Tarefa tarefa) throws SQLException {
        validarUsuarioAtivo();
        return tarefaRepository.salvar(tarefa, usuarioAtivo.getId());
    }

    /**
     * Retorna todas as tarefas do usuário.
     */
    public List<Tarefa> listarTarefas() throws SQLException {
        validarUsuarioAtivo();
        return tarefaRepository.buscarTodas(usuarioAtivo.getId());
    }

    /**
     * Retorna apenas as tarefas pendentes e atrasadas.
     */
    public List<Tarefa> listarTarefasPendentes() throws SQLException {
        validarUsuarioAtivo();
        return tarefaRepository.buscarPendentes(usuarioAtivo.getId());
    }

    /**
     * Marca uma tarefa como concluída no banco.
     */
    public void concluirTarefa(Tarefa tarefa) throws SQLException {
        tarefa.marcarComoConcluida();
        tarefaRepository.atualizar(tarefa);
    }

    /**
     * Atualiza os dados de uma tarefa existente.
     */
    public void atualizarTarefa(Tarefa tarefa) throws SQLException {
        tarefaRepository.atualizar(tarefa);
    }

    /**
     * Exclui uma tarefa pelo id.
     */
    public void excluirTarefa(Long id) throws SQLException {
        tarefaRepository.excluir(id);
    }

    // ===== Compromissos =====

    /**
     * Cadastra um novo compromisso fixo no banco.
     */
    public CompromissoFixo cadastrarCompromisso(CompromissoFixo compromisso)
            throws SQLException {
        validarUsuarioAtivo();
        return compromissoRepository.salvar(compromisso, usuarioAtivo.getId());
    }

    /**
     * Retorna todos os compromissos do usuário.
     */
    public List<CompromissoFixo> listarCompromissos() throws SQLException {
        validarUsuarioAtivo();
        return compromissoRepository.buscarTodos(usuarioAtivo.getId());
    }

    /**
     * Retorna os compromissos de uma data específica.
     */
    public List<CompromissoFixo> listarCompromissosPorData(LocalDate data)
            throws SQLException {
        validarUsuarioAtivo();
        return compromissoRepository.buscarPorData(usuarioAtivo.getId(), data);
    }

    /**
     * Atualiza os dados de um compromisso existente.
     */
    public void atualizarCompromisso(CompromissoFixo compromisso)
            throws SQLException {
        compromissoRepository.atualizar(compromisso);
    }

    /**
     * Exclui um compromisso pelo id.
     */
    public void excluirCompromisso(Long id) throws SQLException {
        compromissoRepository.excluir(id);
    }

    // ===== Configuração da Rotina =====

    /**
     * Salva ou atualiza a configuração de rotina do usuário.
     */
    public ConfiguracaoRotina salvarRotina(ConfiguracaoRotina rotina)
            throws SQLException {
        validarUsuarioAtivo();
        return rotinaRepository.salvar(rotina, usuarioAtivo.getId());
    }

    /**
     * Retorna a configuração de rotina do usuário.
     */
    public Optional<ConfiguracaoRotina> buscarRotina() throws SQLException {
        validarUsuarioAtivo();
        return rotinaRepository.buscarPorUsuario(usuarioAtivo.getId());
    }

    /**
     * Retorna true se o usuário já configurou a rotina.
     */
    public boolean rotinaCofigurada() throws SQLException {
        return buscarRotina().isPresent();
    }

    // ===== Usuário =====

    public Usuario getUsuarioAtivo() {
        return usuarioAtivo;
    }

    public void atualizarUsuario(Usuario usuario) throws SQLException {
        usuarioRepository.atualizar(usuario);
        this.usuarioAtivo = usuario;
    }

    // ===== Validação interna =====

    private void validarUsuarioAtivo() {
        if (usuarioAtivo == null) {
            throw new IllegalStateException(
                    "Nenhum usuário ativo. Chame inicializar() primeiro.");
        }
    }
}