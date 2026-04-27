package br.com.agendainteligente.controller;

import br.com.agendainteligente.Main;
import br.com.agendainteligente.enums.TipoBloco;
import br.com.agendainteligente.model.AgendaDiaria;
import br.com.agendainteligente.model.BlocoDeTempo;
import br.com.agendainteligente.model.Tarefa;
import br.com.agendainteligente.service.AgendaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class TelaAgendaController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private Label lblSugestao;
    @FXML private VBox boxSugestao;
    @FXML private TableView<BlocoDeTempo> tabelaAgenda;
    @FXML private TableColumn<BlocoDeTempo, String> colHorario;
    @FXML private TableColumn<BlocoDeTempo, String> colTipo;
    @FXML private TableColumn<BlocoDeTempo, String> colAtividade;
    @FXML private TableColumn<BlocoDeTempo, String> colStatus;
    @FXML private TableColumn<BlocoDeTempo, String> colDuracao;

    private AgendaService agendaService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        agendaService = Main.getAgendaService();
        datePicker.setValue(LocalDate.now());
        configurarTabela();
        gerarAgenda();
    }

    @FXML
    public void gerarAgenda() {
        try {
            LocalDate data = datePicker.getValue();
            if (data == null) return;

            AgendaDiaria agenda = agendaService.gerarAgenda(data);

            // Ordena blocos por hora de início
            List<BlocoDeTempo> blocos = agenda.getBlocos()
                    .stream()
                    .sorted(Comparator.comparing(BlocoDeTempo::getHoraInicio))
                    .toList();

            tabelaAgenda.setItems(FXCollections.observableArrayList(blocos));

            // Atualiza sugestão
            Tarefa proxima = agendaService.sugerirProximaTarefa(data);
            if (proxima != null) {
                lblSugestao.setText(proxima.getTitulo()
                        + " (" + proxima.getDuracaoEstimadaMinutos() + " min)");
            } else {
                lblSugestao.setText("Nenhuma tarefa pendente para hoje 🎉");
            }

        } catch (IllegalStateException e) {
            lblSugestao.setText("⚠️ Configure sua rotina primeiro!");
        } catch (Exception e) {
            lblSugestao.setText("Erro ao gerar agenda: " + e.getMessage());
        }
    }

    private void configurarTabela() {
        colHorario.setCellValueFactory(c -> {
            BlocoDeTempo b = c.getValue();
            return new SimpleStringProperty(
                    b.getHoraInicio() + " - " + b.getHoraFim());
        });

        colTipo.setCellValueFactory(c ->
                new SimpleStringProperty(
                        traduzirTipo(c.getValue().getTipoBloco())));

        colAtividade.setCellValueFactory(c -> {
            BlocoDeTempo b = c.getValue();
            if (b.getTarefa() != null)
                return new SimpleStringProperty(b.getTarefa().getTitulo());
            if (b.getCompromisso() != null)
                return new SimpleStringProperty(b.getCompromisso().getTitulo());
            return new SimpleStringProperty("—");
        });

        colStatus.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getStatus().getDescricao()));

        colDuracao.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().calcularDuracaoMinutos() + " min"));
    }

    private String traduzirTipo(TipoBloco tipo) {
        return switch (tipo) {
            case LIVRE       -> "🟢 Livre";
            case COMPROMISSO -> "🔵 Compromisso";
            case TAREFA      -> "🟡 Tarefa";
            case ALMOCO      -> "🍽️ Almoço";
            case DESCANSO    -> "🌙 Descanso";
        };
    }
}