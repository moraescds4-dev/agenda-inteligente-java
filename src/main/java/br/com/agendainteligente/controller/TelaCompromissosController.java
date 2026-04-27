package br.com.agendainteligente.controller;

import br.com.agendainteligente.Main;
import br.com.agendainteligente.enums.TipoCompromisso;
import br.com.agendainteligente.model.CompromissoFixo;
import br.com.agendainteligente.service.AgendaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class TelaCompromissosController implements Initializable {

    @FXML private VBox painelForm;
    @FXML private TextField txtTitulo;
    @FXML private ComboBox<TipoCompromisso> cbTipo;
    @FXML private DatePicker dateData;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFim;
    @FXML private TextArea txtDescricao;
    @FXML private TableView<CompromissoFixo> tabelaCompromissos;
    @FXML private TableColumn<CompromissoFixo, String> colTitulo;
    @FXML private TableColumn<CompromissoFixo, String> colTipo;
    @FXML private TableColumn<CompromissoFixo, String> colData;
    @FXML private TableColumn<CompromissoFixo, String> colInicio;
    @FXML private TableColumn<CompromissoFixo, String> colFim;
    @FXML private TableColumn<CompromissoFixo, String> colDuracao;
    @FXML private TableColumn<CompromissoFixo, String> colAcoes;

    private AgendaService agendaService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        agendaService = Main.getAgendaService();
        cbTipo.setItems(FXCollections.observableArrayList(
                TipoCompromisso.values()));
        cbTipo.setValue(TipoCompromisso.AULA);
        configurarTabela();
        carregarCompromissos();
    }

    @FXML
    public void abrirFormNovoCompromisso() {
        limparForm();
        painelForm.setVisible(true);
        painelForm.setManaged(true);
    }

    @FXML
    public void fecharForm() {
        painelForm.setVisible(false);
        painelForm.setManaged(false);
    }

    @FXML
    public void salvarCompromisso() {
        if (txtTitulo.getText().isBlank()) {
            mostrarAlerta("Título é obrigatório.");
            return;
        }
        if (dateData.getValue() == null) {
            mostrarAlerta("Data é obrigatória.");
            return;
        }
        if (txtHoraInicio.getText().isBlank() || txtHoraFim.getText().isBlank()) {
            mostrarAlerta("Horário de início e fim são obrigatórios.");
            return;
        }

        try {
            LocalTime inicio = LocalTime.parse(txtHoraInicio.getText().trim());
            LocalTime fim    = LocalTime.parse(txtHoraFim.getText().trim());

            if (!inicio.isBefore(fim)) {
                mostrarAlerta("Hora de início deve ser antes da hora de fim.");
                return;
            }

            CompromissoFixo compromisso = new CompromissoFixo(
                    txtTitulo.getText().trim(),
                    dateData.getValue(),
                    inicio,
                    fim,
                    cbTipo.getValue()
            );
            compromisso.setDescricao(txtDescricao.getText().trim());

            agendaService.cadastrarCompromisso(compromisso);
            fecharForm();
            carregarCompromissos();

        } catch (DateTimeParseException e) {
            mostrarAlerta("Horário inválido. Use o formato HH:mm (ex: 08:30)");
        } catch (Exception e) {
            mostrarAlerta("Erro ao salvar: " + e.getMessage());
        }
    }

    private void carregarCompromissos() {
        try {
            tabelaCompromissos.setItems(FXCollections.observableArrayList(
                    agendaService.listarCompromissos()));
        } catch (Exception e) {
            mostrarAlerta("Erro ao carregar compromissos: " + e.getMessage());
        }
    }

    private void configurarTabela() {
        colTitulo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitulo()));

        colTipo.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getTipo().getDescricao()));

        colData.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getData().toString()));

        colInicio.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getHoraInicio().toString()));

        colFim.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getHoraFim().toString()));

        colDuracao.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().calcularDuracaoMinutos() + " min"));

        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnExcluir = new Button("🗑");

            {
                btnExcluir.setStyle(
                        "-fx-background-color:#e74c3c;-fx-text-fill:white;" +
                                "-fx-background-radius:4;-fx-cursor:hand;");

                btnExcluir.setOnAction(e -> {
                    CompromissoFixo c = getTableView()
                            .getItems().get(getIndex());
                    try {
                        agendaService.excluirCompromisso(c.getId());
                        carregarCompromissos();
                    } catch (Exception ex) {
                        mostrarAlerta("Erro: " + ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnExcluir);
            }
        });
    }

    private void limparForm() {
        txtTitulo.clear();
        txtHoraInicio.clear();
        txtHoraFim.clear();
        txtDescricao.clear();
        dateData.setValue(null);
        cbTipo.setValue(TipoCompromisso.AULA);
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}