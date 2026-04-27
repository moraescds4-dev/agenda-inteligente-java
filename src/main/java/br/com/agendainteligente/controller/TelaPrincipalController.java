package br.com.agendainteligente.controller;

import br.com.agendainteligente.Main;
import br.com.agendainteligente.service.AgendaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller da tela principal.
 * Gerencia a navegação entre as seções do app
 * carregando cada painel dinamicamente na área central.
 */
public class TelaPrincipalController implements Initializable {

    @FXML private StackPane painelConteudo;
    @FXML private Label lblUsuario;
    @FXML private Button btnAgenda;
    @FXML private Button btnTarefas;
    @FXML private Button btnCompromissos;
    @FXML private Button btnConfiguracoes;

    private AgendaService agendaService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        agendaService = Main.getAgendaService();

        // Mostra o nome do usuário no rodapé da sidebar
        if (agendaService.getUsuarioAtivo() != null) {
            lblUsuario.setText("👤 " + agendaService.getUsuarioAtivo().getNome());
        }

        // Carrega a agenda do dia como tela inicial
        mostrarAgenda();
    }

    @FXML
    public void mostrarAgenda() {
        ativarBotao(btnAgenda);
        carregarPainel("/fxml/TelaAgenda.fxml");
    }

    @FXML
    public void mostrarTarefas() {
        ativarBotao(btnTarefas);
        carregarPainel("/fxml/TelaTarefas.fxml");
    }

    @FXML
    public void mostrarCompromissos() {
        ativarBotao(btnCompromissos);
        carregarPainel("/fxml/TelaCompromissos.fxml");
    }

    @FXML
    public void mostrarConfiguracoes() {
        ativarBotao(btnConfiguracoes);
        carregarPainel("/fxml/TelaConfiguracoes.fxml");
    }

    /**
     * Carrega um arquivo FXML e substitui o conteúdo da área central.
     */
    private void carregarPainel(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath));
            Node painel = loader.load();
            painelConteudo.getChildren().setAll(painel);
        } catch (IOException e) {
            System.err.println("Erro ao carregar painel: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Destaca o botão ativo e remove o destaque dos outros.
     */
    private void ativarBotao(Button ativo) {
        btnAgenda.getStyleClass().remove("btn-menu-ativo");
        btnTarefas.getStyleClass().remove("btn-menu-ativo");
        btnCompromissos.getStyleClass().remove("btn-menu-ativo");
        btnConfiguracoes.getStyleClass().remove("btn-menu-ativo");
        ativo.getStyleClass().add("btn-menu-ativo");
    }
}