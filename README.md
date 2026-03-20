# 🚀 iPaaS Task Manager - Backend Pleno (Em Desenvolvimento 🚧)

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)
![Docker](https://img.shields.io/badge/Docker-Container-blue.svg)
![Flyway](https://img.shields.io/badge/Flyway-Migration-red.svg)

Este repositório contém a solução em desenvolvimento para o **Desafio Técnico da iPaaS (Backend Java Pleno).**  

A proposta é construir uma API RESTful para gerenciamento de tarefas e subtarefas, com foco em código limpo, regras de negócio bem definidas e uma arquitetura preparada para crescer.

---

## 🧠 Arquitetura e Decisões Técnicas (Planejamento)

O projeto segue os princípios de Clean Architecture, com separação clara de responsabilidades para facilitar manutenção, testes e evolução.

- **Camada Controller:** Responsável apenas por receber as requisições HTTP e delegar para a camada de serviço.
- **Camada Service:** Onde ficam as regras de negócio. Aqui é garantido, por exemplo, que uma tarefa só pode ser concluída se todas as subtarefas estiverem concluídas.
- **Camada Repository:** Camada de acesso a dados utilizando Spring Data JPA.
- **DTOs (Records):** Definem os contratos de entrada e saída da API, incluindo validações.

### 🛡️ Tratamento de Erros
- Validação com Bean Validation (@NotNull, @Email, etc.).
- Tratamento global com @RestControllerAdvice
- Retorno padronizado de erros (sem expor detalhes internos da aplicação)

---

## 🛠️ Tecnologias que Serão Utilizadas

- **Java 21**
- **Spring Boot** (Web, Data JPA, Validation)
- **PostgreSQL**
- **Flyway** (controle de migração de banco)
- **Docker & Docker Compose** (Containerização do banco e da aplicação)
- **Springdoc OpenAPI / Swagger** 
- **JUnit 5, Mockito & Testcontainers**  

---

## 🧩 Modelagem do Domínio

O sistema é baseado em três entidades principais:

- User → Task → Subtask

Relacionamentos:

- Um usuário possui várias tarefas

- Uma tarefa possui várias subtarefas

- Uma subtarefa pertence a uma tarefa    


##### O uso de banco relacional (PostgreSQL) foi escolhido por refletir melhor esse tipo de relacionamento.
---

## 📡 Endpoints Mapeados na Solução

A API contará com uma OpenAPI interativa (Swagger UI) expondo a construção dos seguintes endpoints definidos nos requisitos do desafio:

#### 👤 Usuários
- `POST /usuarios` - cria um usuário
- `GET /usuarios/{id}` - busca por ID

#### ✅ Tarefas
- `POST /tarefas` - cria uma tarefa
- `GET /tarefas?status=` - lista tarefas com filtro
- `PATCH /tarefas/{id}/status` - atualiza status

#### 📌 Subtarefas
- `POST /tarefas/{tarefaId}/subtarefas` - cria subtarefa
- `GET /tarefas/{tarefaId}/subtarefas` - lista subtarefas
- `PATCH /subtarefas/{id}/status` - atualiza status

---
## ⚠️ Regras de Negócio

- dataConclusao só é preenchida se o status for **CONCLUIDA**

- Uma tarefa só pode ser concluída **se todas as subtarefas estiverem concluídas**

- Subtarefas não alteram automaticamente a tarefa, mas podem **bloquear sua conclusão**

---

## ⚙️ Como Executar o Projeto Localmente (Em Planejamento)

*(As instruções completas e exatas de boot estarão formalizadas assim que o core da infraestrutura e o Flyway forem finalizados ao decorrer do projeto).*

### Pré-requisitos estipulados:
- [Docker](https://www.docker.com/) e Docker Compose
- [Java 17+](https://adoptium.net/)

O fluxo de execução seguirá este padrão simples:
1. Subida da infraestrutura do banco utilizando o `docker-compose up -d`.
2. Rodar a aplicação
3. O Flyway cria as tabelas automaticamente

---

## 🧪 Estratégia de Testes (TDD / Integração Contínua)

- **Testes Unitários**

  - Foco nas regras de negócio (camada Service)
  - Uso de Mockito

- **Testes de Integração**

  - SpringBootTest + Testcontainers

  - Validação do fluxo completo:
    - API sobe corretamente
    - Endpoints funcionam
    - JSON entra/sai corretamente
    - Validações funcionam
    - Dados persistem no banco

---

## 💡 Objetivo do Projeto

Mais do que apenas cumprir o desafio, a ideia é demonstrar:

- organização de código
- domínio de backend com Spring
- aplicação prática de regras de negócio
- preocupação com qualidade e escalabilidade

*Coded with 🖤 by Raul - Work In Progress.*
