# Decisões Arquiteturais

## 1. Nome do Projeto
`taskmanager`

---

## 2. Idioma do Projeto
- **Código (classes, variáveis, métodos):** Inglês (ex: `User`, `TaskService`, `SubtaskRepository`)
- **Endpoints (URLs):** Português (ex: `/usuarios`, `/tarefas`) para manter aderência ao enunciado do desafio

---

## 3. Stack Tecnológica

- **Linguagem:** Java 21
- **Framework:** Spring Boot (Spring Web, Spring Data JPA)
- **Validação:** Bean Validation
- **Banco de Dados:** PostgreSQL
- **Migrações:** Flyway
- **Documentação:** Swagger / OpenAPI
- **Boilerplate:** Lombok
- **Testes:** JUnit 5, Mockito, Testcontainers
- **Infraestrutura:** Docker (Dockerfile + Docker Compose)

---

## 4. Modelagem de Dados

O sistema foi modelado utilizando banco relacional (PostgreSQL), pois as entidades possuem relacionamento direto entre si.

### Entidades e Relacionamentos

- **User (Usuário)**  
  Um usuário pode possuir várias tarefas (1:N)

- **Task (Tarefa)**  
  Pertence a um usuário e pode possuir várias subtarefas (1:N)

- **Subtask (Subtarefa)**  
  Pertence a uma única tarefa (N:1)

---

## 5. Regras de Negócio

###  Usuário
- `id`: UUID PK gerado automaticamente
- `nome`: String obrigatório 
- `email`: String obrigatório, único e válido

**Endpoints:**
- `POST /usuarios` → cria usuário
- `GET /usuarios/{id}` → busca por ID

---

###  Tarefa
- `id`: UUID PK gerado automaticamente 
- `titulo`: String obrigatório
- `descricao`: String opcional
- `status`: ENUM `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`
- `dataCriacao`: LocalDateTime gerada automaticamente
- `dataConclusao`: LocalDateTime - prrenchida apenas se `CONCLUIDA`
- `usuarioId`: UUID referência ao usuário @mANYtoOne pra usuario

**Regras:**
- `dataConclusao` só é preenchida quando o status for `CONCLUIDA`
- Uma tarefa só pode ser concluída se **todas as subtarefas estiverem concluídas**

**Endpoints:**
- `POST /tarefas` → cria tarefa
- `GET /tarefas?status=...` → lista com filtro
- `PATCH /tarefas/{id}/status` → atualiza status

---

###  Subtarefa
- `id`: UUID PK gerado automaticamente
- `titulo`: String obrigatório
- `descricao`: String opcional
- `status`: ENUM `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`
- `dataCriacao`: LocalDateTime gerada automaticamente
- `dataConclusao`: LocalDateTime preenchida apenas se `CONCLUIDA`
- `tarefaId`: UUID referência à tarefa @ManyToONE pra tarefa

**Regras:**
- Concluir uma subtarefa **não conclui automaticamente a tarefa**
- A existência de subtarefas pendentes **impede a conclusão da tarefa**

**Endpoints:**
- `POST /tarefas/{tarefaId}/subtarefas` → cria subtarefa
- `GET /tarefas/{tarefaId}/subtarefas` → lista subtarefas
- `PATCH /subtarefas/{id}/status` → atualiza status

---

## 6. Estratégia de Testes

###  Testes Unitários
- Foco na camada de **Service**
- Validação isolada das regras de negócio
- Uso de **Mockito** para mockar dependências

---

###  Testes de Integração
- Uso de `@SpringBootTest`
- Testcontainers para subir um PostgreSQL real em ambiente de teste
- Validação do fluxo completo da aplicação

**Cenários cobertos:**
- A aplicação sobe corretamente
- Endpoints respondem corretamente
- JSON entra e sai conforme esperado
- Validações funcionam
- Dados persistem no banco
- Comunicação entre camadas está funcionando

---

## Objetivo

Demonstrar domínio em:
- Modelagem de domínio
- Regras de negócio bem definidas
- Arquitetura limpa
- Testes automatizados
- Boas práticas de desenvolvimento backend com Spring Boot