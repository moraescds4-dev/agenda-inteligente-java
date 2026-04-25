package br.com.agendainteligente;

import br.com.agendainteligente.enums.Prioridade;
import br.com.agendainteligente.enums.StatusTarefa;
import br.com.agendainteligente.enums.TipoBloco;
import br.com.agendainteligente.model.*;
import br.com.agendainteligente.service.AgendadorInteligenteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgendadorInteligenteServiceTest {

    private AgendadorInteligenteService service;
    private ConfiguracaoRotina rotina;

    @BeforeEach
    void setUp() {
        // Configura uma rotina padrão antes de cada teste:
        // Dia: 07:00 - 23:00
        // Almoço: 12:00 - 13:00
        // Descanso: 22:00
        rotina = new ConfiguracaoRotina(
                LocalTime.of(7, 0),   // início do dia
                LocalTime.of(23, 0),  // fim do dia
                LocalTime.of(12, 0),  // início almoço
                LocalTime.of(13, 0),  // fim almoço
                LocalTime.of(22, 0)   // início descanso
        );
        service = new AgendadorInteligenteService(rotina);
    }

    // ===== TESTE 1 =====
    @Test
    void deveCriarAgendaComDataCorreta() {
        LocalDate hoje = LocalDate.now();
        AgendaDiaria agenda = service.criarAgendaBase(hoje);

        assertNotNull(agenda);
        assertEquals(hoje, agenda.getData());
    }

    // ===== TESTE 2 =====
    @Test
    void deveBloquearAlmocoNaAgenda() {
        AgendaDiaria agenda = service.criarAgendaBase(LocalDate.now());
        service.bloquearAlmoco(agenda);

        List<BlocoDeTempo> blocos = agenda.listarBlocosPorTipo(TipoBloco.ALMOCO);

        assertEquals(1, blocos.size());
        assertEquals(LocalTime.of(12, 0), blocos.get(0).getHoraInicio());
        assertEquals(LocalTime.of(13, 0), blocos.get(0).getHoraFim());
    }

    // ===== TESTE 3 =====
    @Test
    void deveBloquearDescansoNaAgenda() {
        AgendaDiaria agenda = service.criarAgendaBase(LocalDate.now());
        service.bloquearDescanso(agenda);

        List<BlocoDeTempo> blocos = agenda.listarBlocosPorTipo(TipoBloco.DESCANSO);

        assertEquals(1, blocos.size());
        assertEquals(LocalTime.of(22, 0), blocos.get(0).getHoraInicio());
    }

    // ===== TESTE 4 =====
    @Test
    void deveEncaixarTarefaEmBlocoLivre() {
        Tarefa tarefa = new Tarefa("Estudar Java", 60, Prioridade.ALTA, LocalDate.now());

        AgendaDiaria agenda = service.gerarAgendaDiaria(
                LocalDate.now(),
                List.of(),        // sem compromissos
                List.of(tarefa)   // uma tarefa de 60 minutos
        );

        long tarefasEncaixadas = agenda.getBlocos().stream()
                .filter(b -> b.getTipoBloco() == TipoBloco.TAREFA)
                .count();

        assertEquals(1, tarefasEncaixadas);
        assertEquals(StatusTarefa.PLANEJADA, tarefa.getStatus());
    }

    // ===== TESTE 5 =====
    @Test
    void deveDeixarTarefaPendenteSeNaoHouverBlocoSuficiente() {
        // Tarefa de 900 minutos (15 horas) — impossível de encaixar
        Tarefa tarefa = new Tarefa("Tarefa gigante", 900, Prioridade.ALTA, LocalDate.now());

        AgendaDiaria agenda = service.gerarAgendaDiaria(
                LocalDate.now(),
                List.of(),
                List.of(tarefa)
        );

        assertEquals(StatusTarefa.PENDENTE, tarefa.getStatus());
    }

    // ===== TESTE 6 =====
    @Test
    void deveRespeitarCompromissoFixo() {
        CompromissoFixo aula = new CompromissoFixo(
                "Aula de Java",
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                br.com.agendainteligente.enums.TipoCompromisso.AULA
        );

        AgendaDiaria agenda = service.gerarAgendaDiaria(
                LocalDate.now(),
                List.of(aula),
                List.of()
        );

        long compromissosNaAgenda = agenda.getBlocos().stream()
                .filter(b -> b.getTipoBloco() == TipoBloco.COMPROMISSO)
                .count();

        assertEquals(1, compromissosNaAgenda);
    }

    // ===== TESTE 7 =====
    @Test
    void deveOrdenarTarefasPorUrgencia() {
        Tarefa baixa = new Tarefa("Tarefa baixa", 30, Prioridade.BAIXA, null);
        Tarefa alta  = new Tarefa("Tarefa alta",  30, Prioridade.ALTA,  LocalDate.now());

        List<Tarefa> ordenadas = service.ordenarTarefas(List.of(baixa, alta));

        // A tarefa de ALTA prioridade com prazo hoje deve vir primeiro
        assertEquals("Tarefa alta", ordenadas.get(0).getTitulo());
    }
}