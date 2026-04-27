package br.com.agendainteligente;

import br.com.agendainteligente.config.DatabaseInitializer;
import br.com.agendainteligente.service.AgendaService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Ponto de entrada do aplicativo Agenda Inteligente.
 * Inicializa o banco, carrega o usuário e abre a tela principal.
 */
public class Main extends Application {

    private static AgendaService agendaService;

    @Override
    public void start(Stage stage) throws Exception {

        // Passo 1: inicializa o banco e cria as tabelas se necessário
        DatabaseInitializer.inicializar();

        // Passo 2: carrega ou cria o usuário padrão
        agendaService = new AgendaService();
        agendaService.inicializar("Usuário");

        // Passo 3: abre a tela principal
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/TelaPrincipal.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        scene.getStylesheets().add(
                getClass().getResource("/css/estilo.css").toExternalForm());

        stage.setTitle("Agenda Inteligente");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.show();
    }

    /**
     * Disponibiliza o AgendaService para os controllers.
     */
    public static AgendaService getAgendaService() {
        return agendaService;
    }

    public static void main(String[] args) {
        launch(args);
    }
}