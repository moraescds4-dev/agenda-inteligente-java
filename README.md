# 🧠 Agenda Inteligente

> Aplicativo desktop em Java que organiza automaticamente o seu dia, encaixando tarefas flexíveis nos horários livres entre seus compromissos fixos — e garantindo que você pare pra almoçar e descansar.

## 📌 O problema

Quem estuda, trabalha e ainda toca projetos pessoais em paralelo vive a mesma dor: a rotina é uma mistura de **compromissos com hora marcada** (aulas, reuniões) e **tarefas que flutuam** (estudar, escrever, criar). Uma agenda comum organiza só a primeira metade. A segunda metade fica na cabeça — e é aí que o caos começa.

## 💡 A solução

A Agenda Inteligente separa essas duas naturezas de atividade e faz o trabalho que você não devia ter que fazer: **decidir o que cabe onde**. Você cadastra seus compromissos fixos, joga a lista de tarefas flexíveis, e o sistema monta o dia pra você — respeitando prioridade, prazo, almoço e descanso noturno.

## ✨ Funcionalidades do MVP

- Cadastro de compromissos fixos (aulas, reuniões, consultas)
- Cadastro de tarefas flexíveis (com prioridade, prazo e duração estimada)
- Configuração da rotina pessoal (início/fim do dia, almoço, descanso)
- Geração automática da agenda diária encaixando tarefas nos blocos livres
- Sugestão da próxima atividade a fazer agora
- Marcação de tarefas como concluídas

## 🗺️ Roadmap

| Fase | Descrição | Status |
|------|-----------|--------|
| 1 | Reunião, planejamento e definição de stack | ✅ Concluída |
| 2 | Modelagem UML (casos de uso, classes, atividades) | ✅ Concluída |
| 3 | Implementação do domínio e enums | 🚧 Em andamento |
| 4 | Serviço de agendamento inteligente + testes | ⏳ Próximo |
| 5 | Persistência com SQLite | ⏳ |
| 6 | Interface JavaFX | ⏳ |
| 7 | Lembretes e refinamento | ⏳ |
| Futuro | Versão web, mobile e comando por voz | 💭 |

## 🛠️ Stack

- **Linguagem:** Java 21 (LTS)
- **Interface gráfica:** JavaFX 21
- **Banco de dados:** SQLite (via JDBC)
- **Build:** Maven
- **Testes:** JUnit 5
- **IDE:** IntelliJ IDEA

## 🏗️ Arquitetura

Organização em camadas, separando claramente as responsabilidades:

```
src/main/java/br/com/agendainteligente/
├── model/          → Classes de domínio (Tarefa, CompromissoFixo, AgendaDiaria...)
├── enums/          → Prioridade, StatusTarefa, TipoBloco, StatusBloco, TipoCompromisso
├── repository/     → Acesso ao banco SQLite
├── service/        → Regras de negócio (AgendadorInteligenteService)
├── controller/     → Controle das telas JavaFX
├── view/           → Telas JavaFX e ponto de entrada (MainApp)
├── config/         → Configuração do banco e da aplicação
└── util/           → Utilitários gerais
```

## 📚 Documentação UML

Os diagramas da modelagem estão em [`docs/uml/`](docs/uml/):

- **Diagrama de Classes** — estrutura do domínio com todas as 8 classes e 5 enums
- **Diagrama de Atividades** — fluxo do agendamento automático (o "coração" do sistema)

## ▶️ Como rodar

> ⚠️ Ainda em desenvolvimento — quando o MVP estiver pronto, esta seção vai ter o passo a passo completo.

Pré-requisitos:

- JDK 21 ou superior
- Maven 3.9+

```bash
# Clonar o repositório
git clone https://github.com/SEU-USUARIO/agenda-inteligente.git
cd agenda-inteligente

# Rodar com o plugin do JavaFX
mvn clean javafx:run
```

## 🧪 Rodar os testes

```bash
mvn test
```

## 📂 Estrutura do repositório

```
agenda-inteligente/
├── src/
│   ├── main/
│   │   ├── java/          → Código-fonte
│   │   └── resources/     → FXML, CSS e scripts SQL
│   └── test/
│       └── java/          → Testes unitários
├── docs/
│   └── uml/               → Diagramas do projeto
├── pom.xml
├── .gitignore
└── README.md
```

## 📜 Licença

Este projeto é parte do meu portfólio de estudos em Java. Fique à vontade pra usar como referência.

---

*Projeto em desenvolvimento ativo. Os commits contam a história: cada etapa do checklist vira um commit com propósito claro.*
