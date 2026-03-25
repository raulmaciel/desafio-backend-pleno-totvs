# 🚀 iPaaS Task Manager — Backend Java Pleno

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)
![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)
![Flyway](https://img.shields.io/badge/Flyway-Migration-red.svg)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-brightgreen.svg)
![Testcontainers](https://img.shields.io/badge/Testcontainers-Integration-orange.svg)

API RESTful para gerenciamento de tarefas e subtarefas, desenvolvida como solução ao **Desafio Técnico Backend Java Pleno da iPaaS**. O projeto prioriza código limpo, regras de negócio bem isoladas e uma arquitetura preparada para crescer.

---

## 🧠 Arquitetura e Decisões Técnicas

O projeto segue os princípios de **Clean Architecture**, com separação clara de responsabilidades entre as camadas:

**Controller** — Recebe as requisições HTTP, delega para a camada de serviço e retorna as respostas. Não contém nenhuma lógica de negócio.

**Service** — Concentra todas as regras de negócio. É aqui que são aplicadas, por exemplo, a validação de subtarefas pendentes antes de concluir uma tarefa, e o preenchimento automático de `concludedAt` apenas quando o status é `COMPLETED`.

**Repository** — Acesso a dados via Spring Data JPA. O `TaskRepository` usa JPQL customizado para suportar filtros combinados (`status` + `userId`) com paginação nativa do Spring.

**DTOs (Records)** — Contratos bem definidos de entrada e saída da API. Os `*Request` carregam as validações via Bean Validation; os `*Response` encapsulam a conversão da entidade com método estático `fromEntity()`. A conversão de request para entidade é feita com `toEntity()` dentro do próprio record.

### 🛡️ Tratamento de Exceções

O projeto tem um `GlobalExceptionHandler` com `@RestControllerAdvice` que centraliza o tratamento de todos os erros da aplicação. Cada tipo de erro retorna um `StandardError` padronizado com `timestamp`, `status`, `error`, `message` e `path` — sem expor detalhes internos.

| Exceção | HTTP Status | Quando ocorre |
|---|---|---|
| `ResourceNotFoundException` | 404 Not Found | Usuário, tarefa ou subtarefa não encontrados |
| `EmailAlreadyExistsException` | 409 Conflict | E-mail duplicado na criação de usuário |
| `BusinessRuleException` | 409 Conflict | Tentativa de concluir tarefa com subtarefas pendentes |
| `MethodArgumentNotValidException` | 400 Bad Request | Falha nas validações de Bean Validation |

O `UserService` também captura `DataIntegrityViolationException` como fallback de segurança para violações de constraint única no banco, relançando-a como `EmailAlreadyExistsException`.

### 🗄️ Banco de Dados e Migrations

PostgreSQL 17 com schema versionado via **Flyway**:

- `V1__create_users_table.sql` — Tabela `users` com constraint `UNIQUE` no email
- `V2__create_tasks_table.sql` — Tabela `tasks` com FK para `users` (`ON DELETE CASCADE`)
- `V3__create_subtasks_table.sql` — Tabela `subtasks` com FK para `tasks` (`ON DELETE CASCADE`)

O JPA opera com `ddl-auto: validate`, ou seja, nunca altera o schema — o Flyway é a única fonte da verdade para a estrutura do banco.

### 📄 Documentação OpenAPI / Swagger

Todos os endpoints são documentados com anotações `@Operation`, `@ApiResponse` e `@Schema` do Springdoc OpenAPI. A Swagger UI fica disponível em `http://localhost:8080/swagger-ui/index.html` após subir a aplicação.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3** (Web, Data JPA, Validation, Actuator)
- **PostgreSQL 17**
- **Flyway** — migrations versionadas
- **Docker & Docker Compose** — containerização da aplicação e do banco
- **Springdoc OpenAPI / Swagger UI**
- **Lombok** — redução de boilerplate nas entidades e serviços
- **JUnit 5 + Mockito** — testes unitários
- **Spring MockMvc** — testes de controller
- **Testcontainers** — testes de integração com PostgreSQL real

---

## 🧩 Modelagem do Domínio

```
UserEntity (1) ──── (N) TaskEntity (1) ──── (N) SubtaskEntity
```

- Um usuário possui várias tarefas
- Uma tarefa possui várias subtarefas
- Deleção em cascata: ao remover um usuário, suas tarefas e subtarefas são removidas; ao remover uma tarefa, suas subtarefas também são

O relacionamento `@ManyToOne` nas entidades usa `FetchType.LAZY` para evitar consultas desnecessárias ao banco.

---

## ⚠️ Regras de Negócio

- `concludedAt` é preenchido **automaticamente** apenas quando o status é definido como `COMPLETED`, e limpo nos demais casos
- Uma tarefa **só pode ser concluída** se **todas as suas subtarefas** estiverem com status `COMPLETED` — validado via consulta ao repositório que verifica subtarefas pendentes.
- Alterar o status de uma subtarefa **não afeta automaticamente** a tarefa pai, mas pode bloquear sua conclusão enquanto houver subtarefas pendentes
- O status inicial de tarefas e subtarefas é definido como `PENDING` via `@PrePersist`

---

## 📡 Endpoints da API

### 👤 Usuários

| Método | Endpoint | Descrição | Status de Retorno |
|---|---|---|---|
| `POST` | `/usuarios` | Cria um novo usuário | 201, 400, 409 |
| `GET` | `/usuarios/{id}` | Busca usuário por ID | 200, 404 |

### ✅ Tarefas

| Método | Endpoint | Descrição | Status de Retorno |
|---|---|---|---|
| `POST` | `/tarefas` | Cria uma nova tarefa | 201, 400, 404 |
| `GET` | `/tarefas` | Lista tarefas com filtros e paginação | 200 |
| `PATCH` | `/tarefas/{id}/status` | Atualiza o status da tarefa | 200, 400, 404, 409 |

**Filtros disponíveis em `GET /tarefas`:** `status` (PENDING, IN_PROGRESS, COMPLETED), `userId` (UUID), `page`, `size`, `sort` — todos opcionais e combináveis.

### 📌 Subtarefas

| Método | Endpoint | Descrição | Status de Retorno |
|---|---|---|---|
| `POST` | `/tarefas/{taskId}/subtarefas` | Cria uma subtarefa | 201, 400, 404 |
| `GET` | `/tarefas/{taskId}/subtarefas` | Lista subtarefas com paginação | 200, 404 |
| `PATCH` | `/subtarefas/{id}/status` | Atualiza o status da subtarefa | 200, 400, 404 |

---

## 🧪 Estratégia de Testes

O projeto conta com três camadas de testes, cobrindo desde a lógica de negócio isolada até o fluxo completo da aplicação.

**Testes Unitários** — Cobrem a camada de serviço com **JUnit 5 + Mockito**, sem subir o contexto do Spring. Validam os caminhos felizes, lançamento de exceções de negócio e de recurso não encontrado, e comportamentos como preenchimento e limpeza automática de `concludedAt`.

**Testes de Controller** — Usam **Spring MockMvc** com `@WebMvcTest` para testar o comportamento HTTP de cada endpoint de forma isolada, incluindo validações de entrada (Bean Validation), códigos de status de erro e integração com o `GlobalExceptionHandler`.

**Testes de Integração** — Um único teste de ponta a ponta sobe a aplicação completa com `@SpringBootTest` e um banco **PostgreSQL real via Testcontainers**. Percorre o fluxo completo: criação de usuário, tarefa e subtarefa, tentativa de concluir a tarefa com subtarefa pendente (esperando `409`), conclusão da subtarefa e, por fim, conclusão bem-sucedida da tarefa. O banco é limpo via `@AfterEach` para garantir isolamento.

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos

- [Docker](https://www.docker.com/) e Docker Compose
- [Java 21](https://adoptium.net/temurin/releases/?version=21) (Apenas para execução local sem Docker)

### Rodando localmente (dev)

Para rodar localmente, certifique-se de que o PostgreSQL está disponível em sua máquina ou via Docker, utilizando as mesmas variáveis de ambiente definidas no `application.yml`. Execute o comando abaixo:

```bash
# Linux / macOS
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# Ou
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### Rodando com Docker (prod)

O profile `prod` será utilizado por padrão via `docker-compose.yml`.

```bash
# Primeira vez — faz o build e start
docker compose up --build

# Depois
docker compose up
```

O Flyway cria as tabelas automaticamente na primeira execução. Com a aplicação no ar, acesse a documentação interativa e endpoints de gerenciamento:

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **Actuator Health:** `http://localhost:8080/actuator/health`
- **Actuator Readiness:** `http://localhost:8080/actuator/health/readiness`
- **Actuator Info:** `http://localhost:8080/actuator/info`

---

## 🏃 Executando os testes (test)

A execução dos testes requer o Docker ativo (o Testcontainers sobe o banco de dados efêmero automaticamente).

```bash
# Linux / macOS
SPRING_PROFILES_ACTIVE=test ./mvnw test

# Windows
set SPRING_PROFILES_ACTIVE=test && mvnw.cmd test
```

---