package br.com.agendainteligente.controller;

import br.com.agendainteligente.Main;
import br.com.agendainteligente.enums.Prioridade;
import br.com.agendainteligente.enums.StatusTarefa;
import br.com.agendainteligente.model.Tarefa;
import br.com.agendainteligente.service.AgendaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TelaTarefasController implements Initializable {

    @FXML private VBox painelForm;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtDuracao;
    @FXML private TextField txtCategoria;
    @FXML private ComboBox<Prioridade> cbPrioridade;
    @FXML private DatePicker datePrazo;
    @FXML private TextArea txtDescricao;
    @FXML private TableView<Tarefa> tabelaTarefas;
    @FXML private TableColumn<Tarefa, String> colTitulo;
    @FXML private TableColumn<Tarefa, String> colCategoria;
    @FXML private TableColumn<Tarefa, String> colPrioridade;
    @FXML private TableColumn<Tarefa, String> colDuracao;
    @FXML private TableColumn<Tarefa, String> colPrazo;
    @FXML private TableColumn<Tarefa, String> colStatusT;
    @FXML private TableColumn<Tarefa, String> colAcoes;

    private AgendaService agendaService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        agendaService = Main.getAgendaService();
        cbPrioridade.setItems(FXCollections.observableArrayList(Prioridade.values()));
        cbPrioridade.setValue(Prioridade.MEDIA);
        configurarTabela();
        carregarTarefas();
    }

    @FXML
    public void abrirFormNovaTarefa() {
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
    public void salvarTarefa() {
        if (txtTitulo.getText().isBlank()) {
            mostrarAlerta("Título é obrigatório.");
            return;
        }
        if (txtDuracao.getText().isBlank()) {
            mostrarAlerta("Duração é obrigatória.");
            return;
        }

        try {
            int duracao = Integer.parseInt(txtDuracao.getText().trim());

            Tarefa tarefa = new Tarefa(
                    txtTitulo.getText().trim(),
                    duracao,
                    cbPrioridade.getValue(),
                    datePrazo.getValue()
            );
            tarefa.setDescricao(txtDescricao.getText().trim());
            tarefa.setCategoria(txtCategoria.getText().trim());

            agendaService.cadastrarTarefa(tarefa);
            fecharForm();
            carregarTarefas();

        } catch (NumberFormatException e) {
            mostrarAlerta("Duração deve ser um número inteiro.");
        } catch (Exception e) {
            mostrarAlerta("Erro ao salvar: " + e.getMessage());
        }
    }

    private void carregarTarefas() {
        try {
            tabelaTarefas.setItems(FXCollections.observableArrayList(
                    agendaService.listarTarefas()));
        } catch (Exception e) {
            mostrarAlerta("Erro ao carregar tarefas: " + e.getMessage());
        }
    }

    private void configurarTabela() {
        colTitulo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitulo()));

        colCategoria.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getCategoria() != null
                                ? c.getValue().getCategoria() : "—"));

        colPrioridade.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getPrioridade().getDescricao()));

        colDuracao.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDuracaoEstimadaMinutos() + " min"));

        colPrazo.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getPrazo() != null
                                ? c.getValue().getPrazo().toString() : "—"));

        colStatusT.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getStatus().getDescricao()));

        // Coluna de ações com botões Concluir e Excluir
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnConcluir = new Button("✓");
            private final Button btnExcluir  = new Button("🗑");
            private final HBox box = new HBox(4, btnConcluir, btnExcluir);

            {
                btnConcluir.setStyle(
                        "-fx-background-color:#27ae60;-fx-text-fill:white;" +
                                "-fx-background-radius:4;-fx-cursor:hand;");
                btnExcluir.setStyle(
                        "-fx-background-color:#e74c3c;-fx-text-fill:white;" +
                                "-fx-background-radius:4;-fx-cursor:hand;");

                btnConcluir.setOnAction(e -> {
                    Tarefa t = getTableView().getItems().get(getIndex());
                    try {
                        agendaService.concluirTarefa(t);
                        carregarTarefas();
                    } catch (Exception ex) {
                        mostrarAlerta("Erro: " + ex.getMessage());
                    }
                });

                btnExcluir.setOnAction(e -> {
                    Tarefa t = getTableView().getItems().get(getIndex());
                    try {
                        agendaService.excluirTarefa(t.getId());
                        carregarTarefas();
                    } catch (Exception ex) {
                        mostrarAlerta("Erro: " + ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Tarefa t = getTableView().getItems().get(getIndex());
                    btnConcluir.setDisable(
                            t.getStatus() == StatusTarefa.CONCLUIDA);
                    setGraphic(box);
                }
            }
        });
    }

    private void limparForm() {
        txtTitulo.clear();
        txtDuracao.clear();
        txtCategoria.clear();
        txtDescricao.clear();
        datePrazo.setValue(null);
        cbPrioridade.setValue(Prioridade.MEDIA);
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}