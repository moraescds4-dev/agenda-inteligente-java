package br.com.agendainteligente.service;

import br.com.agendainteligente.enums.StatusTarefa;
import br.com.agendainteligente.enums.StatusBloco;
import br.com.agendainteligente.enums.TipoBloco;
import br.com.agendainteligente.model.AgendaDiaria;
import br.com.agendainteligente.model.BlocoDeTempo;
import br.com.agendainteligente.model.CompromissoFixo;
import br.com.agendainteligente.model.ConfiguracaoRotina;
import br.com.agendainteligente.model.Tarefa;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Serviço principal do sistema.
 * Recebe compromissos fixos, tarefas pendentes e configuração da rotina,
 * e monta automaticamente a AgendaDiaria organizada.
 */
public class AgendadorInteligenteService {

    private final ConfiguracaoRotina configuracaoRotina;

    public AgendadorInteligenteService(ConfiguracaoRotina configuracaoRotina) {
        this.configuracaoRotina = configuracaoRotina;
    }
    /**
     * PASSO 1: Cria uma AgendaDiaria vazia para a data informada.
     * É o ponto de partida — os outros métodos vão preenchendo ela.
     */
    public AgendaDiaria criarAgendaBase(LocalDate data) {
        return new AgendaDiaria(data);
    }
    /**
     * PASSO 2: Recebe a lista de compromissos do dia e cria um BlocoDeTempo
     * ocupado para cada um, adicionando na agenda.
     * Esses blocos são intocáveis — nenhuma tarefa pode ser encaixada neles.
     */
    public void bloquearCompromissos(AgendaDiaria agenda,
                                     List<CompromissoFixo> compromissos) {
        System.out.println(">>> bloquearCompromissos chamado com: " + compromissos.size());
        for (CompromissoFixo compromisso : compromissos) {
            System.out.println("    processando: " + compromisso.getTitulo()
                    + " inicio=" + compromisso.getHoraInicio()
                    + " fim=" + compromisso.getHoraFim()
                    + " valido=" + compromisso.validarHorario());

            if (!compromisso.validarHorario()) {
                System.out.println("    IGNORADO — horário inválido!");
                continue;
            }

            BlocoDeTempo bloco = new BlocoDeTempo(
                    compromisso.getHoraInicio(),
                    compromisso.getHoraFim(),
                    TipoBloco.COMPROMISSO
            );
            bloco.associarCompromisso(compromisso);
            agenda.adicionarBloco(bloco);
            System.out.println("    ADICIONADO ao bloco.");
        }
    }
    /**
     * PASSO 3: Bloqueia o horário de almoço configurado pelo usuário.
     * Cria um bloco do tipo ALMOCO que o agendador nunca vai sobrescrever.
     */
    public void bloquearAlmoco(AgendaDiaria agenda) {
        LocalTime inicio = configuracaoRotina.getInicioAlmoco();
        LocalTime fim = configuracaoRotina.getFimAlmoco();

        if (inicio == null || fim == null) return;

        BlocoDeTempo bloco = new BlocoDeTempo(inicio, fim, TipoBloco.ALMOCO);
        bloco.setStatus(StatusBloco.OCUPADO);
        agenda.adicionarBloco(bloco);
    }
    /**
     * PASSO 4: Bloqueia o horário de descanso noturno.
     * A partir desse horário, o dia "acabou" para o agendador.
     * Nenhuma tarefa será encaixada depois desse bloco.
     */
    public void bloquearDescanso(AgendaDiaria agenda) {
        LocalTime inicioDescanso = configuracaoRotina.getHoraInicioDescanso();
        LocalTime fimDia = configuracaoRotina.getHoraFimDia();

        if (inicioDescanso == null || fimDia == null) return;

        BlocoDeTempo bloco = new BlocoDeTempo(
                inicioDescanso, fimDia, TipoBloco.DESCANSO
        );
        bloco.setStatus(StatusBloco.OCUPADO);
        agenda.adicionarBloco(bloco);
    }
    /**
     * PASSO 5: Varre o período ativo do dia e cria blocos LIVRES
     * nos espaços que não estão ocupados por compromissos, almoço ou descanso.
     * Esses são os espaços onde as tarefas vão ser encaixadas.
     */
    public void identificarBlocosLivres(AgendaDiaria agenda) {
        LocalTime agora = LocalTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
        LocalTime inicioDia = configuracaoRotina.getHoraInicioDia();
        LocalTime cursor = agenda.getData().equals(java.time.LocalDate.now())
                ? (agora.isAfter(inicioDia) ? agora : inicioDia)
                : inicioDia;

        LocalTime fimDia = configuracaoRotina.getHoraInicioDescanso();
        if (cursor == null || fimDia == null) return;

        List<BlocoDeTempo> ocupados = agenda.getBlocos().stream()
                .filter(b -> !b.estaLivre())
                .sorted(Comparator.comparing(BlocoDeTempo::getHoraInicio))
                .toList();

        List<BlocoDeTempo> livres = new java.util.ArrayList<>();

        for (BlocoDeTempo ocupado : ocupados) {
            if (!ocupado.getHoraFim().isAfter(cursor)) continue;
            if (cursor.isBefore(ocupado.getHoraInicio())) {
                livres.add(new BlocoDeTempo(cursor, ocupado.getHoraInicio(), TipoBloco.LIVRE));
            }
            if (ocupado.getHoraFim().isAfter(cursor)) {
                cursor = ocupado.getHoraFim();
            }
        }

        if (cursor.isBefore(fimDia)) {
            livres.add(new BlocoDeTempo(cursor, fimDia, TipoBloco.LIVRE));
        }

        livres.forEach(agenda::adicionarBloco);
    }
    /**
     * PASSO 6: Ordena as tarefas pendentes por urgência.
     * A urgência leva em conta prioridade + proximidade do prazo.
     * Tarefas mais urgentes entram primeiro na fila de encaixe.
     */
    public List<Tarefa> ordenarTarefas(List<Tarefa> tarefas) {
        return tarefas.stream()
                .filter(t -> t.getStatus() == StatusTarefa.PENDENTE
                        || t.getStatus() == StatusTarefa.ATRASADA)
                .sorted(Comparator.comparingInt(Tarefa::calcularUrgencia).reversed())
                .toList();
    }
    /**
     * PASSO 7: Distribui as tarefas nos blocos livres da agenda.
     * Para cada tarefa, procura um bloco livre que comporte sua duração.
     * Se encontrar, aloca. Se não encontrar, deixa a tarefa como PENDENTE.
     * É o loop central do diagrama de atividades.
     */
    public void distribuirTarefas(AgendaDiaria agenda, List<Tarefa> tarefasOrdenadas) {
        for (Tarefa tarefa : tarefasOrdenadas) {
            // Busca blocos livres atualizados a cada iteração
            BlocoDeTempo blocoEscolhido = agenda.getBlocos().stream()
                    .filter(b -> b.comportaTarefa(tarefa.getDuracaoEstimadaMinutos()))
                    .min(Comparator.comparing(BlocoDeTempo::getHoraInicio))
                    .orElse(null);

            if (blocoEscolhido != null) {
                alocarTarefaNoBloco(agenda, blocoEscolhido, tarefa);
            }
        }
    }

    private void alocarTarefaNoBloco(AgendaDiaria agenda,
                                     BlocoDeTempo bloco,
                                     Tarefa tarefa) {
        LocalTime inicioTarefa = bloco.getHoraInicio();
        LocalTime fimTarefa = inicioTarefa.plusMinutes(tarefa.getDuracaoEstimadaMinutos());
        LocalTime fimBlocoOriginal = bloco.getHoraFim();

        // Remove o bloco livre original
        agenda.removerBloco(bloco);

        // Cria o bloco ocupado com a tarefa
        BlocoDeTempo blocoTarefa = new BlocoDeTempo(
                inicioTarefa, fimTarefa, TipoBloco.TAREFA
        );
        blocoTarefa.associarTarefa(tarefa);
        agenda.adicionarBloco(blocoTarefa);

        // Se sobrou tempo, cria novo bloco LIVRE com a sobra
        if (fimTarefa.isBefore(fimBlocoOriginal)) {
            BlocoDeTempo sobra = new BlocoDeTempo(
                    fimTarefa, fimBlocoOriginal, TipoBloco.LIVRE
            );
            agenda.adicionarBloco(sobra);
        }

        tarefa.marcarComoPlanejada();
    }
    /**
     * PASSO 8: Sugere a próxima tarefa a ser executada agora.
     * Olha a agenda e retorna o primeiro bloco de TAREFA que ainda
     * não passou — é o "o que eu faço agora?" do usuário.
     * Retorna null se não houver nenhuma tarefa planejada à frente.
     */
    public Tarefa sugerirProximaTarefa(AgendaDiaria agenda) {
        LocalTime agora = LocalTime.now();

        return agenda.getBlocos().stream()
                .filter(b -> b.getTipoBloco() == TipoBloco.TAREFA)
                .filter(b -> b.getHoraFim().isAfter(agora))
                .sorted(Comparator.comparing(BlocoDeTempo::getHoraInicio))
                .map(BlocoDeTempo::getTarefa)
                .filter(t -> t != null)
                .findFirst()
                .orElse(null);
    }
    /**
     * MÉTODO PRINCIPAL — Orquestra todos os passos e gera a agenda do dia.
     *
     * Recebe a data, os compromissos fixos do dia e as tarefas pendentes.
     * Devolve uma AgendaDiaria completamente organizada, com:
     * - compromissos bloqueados
     * - almoço bloqueado
     * - descanso bloqueado
     * - tarefas encaixadas nos blocos livres por ordem de urgência
     *
     * É o único método que o Controller vai precisar chamar.
     */
    public AgendaDiaria gerarAgendaDiaria(LocalDate data,
                                          List<CompromissoFixo> compromissos,
                                          List<Tarefa> tarefas) {
        AgendaDiaria agenda = criarAgendaBase(data);

        bloquearCompromissos(agenda, compromissos);
        bloquearAlmoco(agenda);
        bloquearDescanso(agenda);
        identificarBlocosLivres(agenda);

        List<Tarefa> tarefasOrdenadas = ordenarTarefas(tarefas);
        distribuirTarefas(agenda, tarefasOrdenadas);

        return agenda;
    }
}