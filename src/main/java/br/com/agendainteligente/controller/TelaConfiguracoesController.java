package br.com.agendainteligente.controller;

import br.com.agendainteligente.Main;
import br.com.agendainteligente.model.ConfiguracaoRotina;
import br.com.agendainteligente.model.Usuario;
import br.com.agendainteligente.service.AgendaService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.ResourceBundle;

public class TelaConfiguracoesController implements Initializable {

    @FXML private TextField txtInicioDia;
    @FXML private TextField txtFimDia;
    @FXML private TextField txtInicioAlmoco;
    @FXML private TextField txtFimAlmoco;
    @FXML private TextField txtInicioDescanso;
    @FXML private TextField txtNome;
    @FXML private Label lblStatus;

    private AgendaService agendaService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        agendaService = Main.getAgendaService();
        carregarConfiguracoes();
    }

    /**
     * Carrega as configurações salvas nos campos da tela.
     */
    private void carregarConfiguracoes() {
        try {
            // Carrega nome do usuário
            Usuario usuario = agendaService.getUsuarioAtivo();
            if (usuario != null) {
                txtNome.setText(usuario.getNome());
            }

            // Carrega rotina se existir
            Optional<ConfiguracaoRotina> rotina = agendaService.buscarRotina();
            if (rotina.isPresent()) {
                ConfiguracaoRotina r = rotina.get();
                txtInicioDia.setText(r.getHoraInicioDia().toString());
                txtFimDia.setText(r.getHoraFimDia().toString());
                txtInicioAlmoco.setText(r.getInicioAlmoco().toString());
                txtFimAlmoco.setText(r.getFimAlmoco().toString());
                txtInicioDescanso.setText(r.getHoraInicioDescanso().toString());
                lblStatus.setText("✅ Configurações carregadas.");
            } else {
                // Valores padrão sugeridos
                txtInicioDia.setText("07:00");
                txtFimDia.setText("23:00");
                txtInicioAlmoco.setText("12:00");
                txtFimAlmoco.setText("13:00");
                txtInicioDescanso.setText("22:00");
                lblStatus.setText("⚠️ Nenhuma configuração salva ainda.");
            }

        } catch (Exception e) {
            lblStatus.setText("Erro ao carregar: " + e.getMessage());
        }
    }

    @FXML
    public void salvarConfiguracoes() {
        // Valida campos obrigatórios
        if (txtInicioDia.getText().isBlank() || txtFimDia.getText().isBlank()
                || txtInicioAlmoco.getText().isBlank()
                || txtFimAlmoco.getText().isBlank()
                || txtInicioDescanso.getText().isBlank()) {
            lblStatus.setText("⚠️ Preencha todos os horários.");
            return;
        }

        try {
            LocalTime inicioDia     = LocalTime.parse(txtInicioDia.getText().trim());
            LocalTime fimDia        = LocalTime.parse(txtFimDia.getText().trim());
            LocalTime inicioAlmoco  = LocalTime.parse(txtInicioAlmoco.getText().trim());
            LocalTime fimAlmoco     = LocalTime.parse(txtFimAlmoco.getText().trim());
            LocalTime inicioDescanso= LocalTime.parse(txtInicioDescanso.getText().trim());

            // Validações de lógica
            if (!inicioDia.isBefore(fimDia)) {
                lblStatus.setText("⚠️ Início do dia deve ser antes do fim.");
                return;
            }
            if (!inicioAlmoco.isBefore(fimAlmoco)) {
                lblStatus.setText("⚠️ Início do almoço deve ser antes do fim.");
                return;
            }
            if (!inicioAlmoco.isAfter(inicioDia)) {
                lblStatus.setText("⚠️ Almoço deve ser depois do início do dia.");
                return;
            }
            if (!inicioDescanso.isAfter(fimAlmoco)) {
                lblStatus.setText("⚠️ Descanso deve ser depois do almoço.");
                return;
            }

            // Salva rotina
            ConfiguracaoRotina rotina = new ConfiguracaoRotina(
                    inicioDia, fimDia,
                    inicioAlmoco, fimAlmoco,
                    inicioDescanso
            );
            agendaService.salvarRotina(rotina);

            // Salva nome do usuário
            if (!txtNome.getText().isBlank()) {
                Usuario usuario = agendaService.getUsuarioAtivo();
                usuario.setNome(txtNome.getText().trim());
                agendaService.atualizarUsuario(usuario);
            }

            lblStatus.setText("✅ Configurações salvas com sucesso!");

        } catch (DateTimeParseException e) {
            lblStatus.setText("⚠️ Horário inválido. Use o formato HH:mm (ex: 08:30)");
        } catch (Exception e) {
            lblStatus.setText("Erro ao salvar: " + e.getMessage());
        }
    }
}