package br.com.agendainteligente.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa o usuário do sistema. É o "dono" da agenda — todos os compromissos,
 * tarefas e configurações pertencem a um usuário.
 *
 * No MVP atual, o sistema é mono-usuário (um único usuário cadastrado),
 * mas a modelagem já prevê multi-usuário para evoluções futuras (versão web).
 */
public class Usuario {

    private Long id;
    private String nome;
    private String email;

    private ConfiguracaoRotina configuracaoRotina;
    private final List<CompromissoFixo> compromissos = new ArrayList<>();
    private final List<Tarefa> tarefas = new ArrayList<>();

    // ===== Construtores =====

    public Usuario() {
        // Construtor vazio para frameworks de persistência e mapeamento.
    }

    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public Usuario(Long id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    // ===== Comportamentos do domínio =====

    /**
     * Cadastra um compromisso fixo (aula, reunião) na agenda do usuário.
     */
    public void cadastrarCompromisso(CompromissoFixo compromisso) {
        Objects.requireNonNull(compromisso, "Compromisso não pode ser nulo");
        compromissos.add(compromisso);
    }

    /**
     * Cadastra uma tarefa flexível para ser encaixada nos blocos livres do dia.
     */
    public void cadastrarTarefa(Tarefa tarefa) {
        Objects.requireNonNull(tarefa, "Tarefa não pode ser nula");
        tarefas.add(tarefa);
    }

    // ===== Getters e Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ConfiguracaoRotina getConfiguracaoRotina() {
        return configuracaoRotina;
    }

    public void setConfiguracaoRotina(ConfiguracaoRotina configuracaoRotina) {
        this.configuracaoRotina = configuracaoRotina;
    }

    public List<CompromissoFixo> getCompromissos() {
        return List.copyOf(compromissos);
    }

    public List<Tarefa> getTarefas() {
        return List.copyOf(tarefas);
    }

    // ===== equals, hashCode e toString =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{id=%d, nome='%s', email='%s'}".formatted(id, nome, email);
    }
}