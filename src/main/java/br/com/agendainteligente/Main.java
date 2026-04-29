package br.com.agendainteligente;

import br.com.agendainteligente.config.DatabaseInitializer;
import br.com.agendainteligente.service.AgendaService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

/**
 * Ponto de entrada do aplicativo Agenda Inteligente.
 * Inicializa o banco, carrega o usuário e abre a tela principal.
 */
public class Main extends Application {

    private static AgendaService agendaService;

    @Override
    public void start(Stage stage) throws Exception {

        DatabaseInitializer.inicializar();

        agendaService = new AgendaService();
        agendaService.inicializar("Usuário");

        System.out.println(">>> Classpath: " + System.getProperty("java.class.path"));
        System.out.println(">>> Raiz dos recursos: " + getClass().getResource("/"));

        URL fxmlUrl = getClass().getResource("/fxml/TelaPrincipal.fxml");
        if (fxmlUrl == null) {
            throw new RuntimeException("TelaPrincipal.fxml não encontrado!");
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 900, 600);

        URL cssUrl = getClass().getResource("/css/estilo.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

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