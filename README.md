[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/Gsn00/plataforma-de-cursos/blob/main/LICENSE)

# Plataforma de Cursos Online

## 🎯 Visão Geral

Este projeto implementa o backend de uma plataforma de cursos online, desenvolvido com Java, Spring Boot e PostgreSQL. O objetivo é proporcionar uma experiência de aprendizado segura e eficiente, com funcionalidades robustas para diferentes perfis de usuários.

## ⚙️ Tecnologias Utilizadas

- **Java 17**: Linguagem principal do projeto.
- **Spring Boot**: Framework para desenvolvimento de aplicações Java.
- **Spring Data JPA**: ORM para interação com o banco de dados PostgreSQL.
- **Spring Security**: Implementação de autenticação e autorização com JWT e refresh tokens.
- **Lombok**: Redução de boilerplate.
- **Bean Validation**: Validação de entrada de dados.
- **PostgreSQL**: Banco de dados relacional.
- **Gumlet API**: Serviço para upload de vídeos.
- **OkHttp**: Cliente HTTP usado para consumo da API da Gumlet.
- **JUnit & Mockito**: Frameworks para testes unitários e mocks.
- **Postman**: Testes manuais dos endpoints durante o desenvolvimento.

## 🔐 Funcionalidades

- **Autenticação e Autorização**: Implementação de login com JWT, incluindo refresh tokens armazenados no banco de dados.
- **Controle de Acesso por Papéis**: Diferenciação de permissões entre `Student`, `Teacher` e `Admin`.
- **Upload de Vídeos**: Integração com a API da Gumlet para upload e gerenciamento de vídeos.
- **Gerenciamento de Dados**: Criação de DTOs e Responses para estruturação de dados.
- **Testes Automatizados**: Cobertura de testes na camada de serviço utilizando JUnit e Mockito.
- **Gerenciamento de Exclusões em Cascata**: Implementação de lógica personalizada para exclusão de cursos, aulas e matrículas.

## 🗂 Estrutura do Projeto

```text
src
└── main
    ├── java
    │   ├── app.config           # Configurações gerais
    │   ├── app.controllers      # Endpoints REST (AuthController, CourseController, etc.)
    │   ├── app.domain           # Entidades JPA (Course, User, etc.)
    │   ├── app.domain.dto       # DTOs para requisição e resposta
    │   ├── app.domain.enums     # Enums (ex: RoleType)
    │   ├── app.domain.lifecycle # Eventos de ciclo de vida JPA (ex: CascadeDeletionManager)
    │   ├── app.exceptions       # Tratamento de erros personalizados
    │   ├── app.mappers          # Conversão DTO ↔ Entity
    │   ├── app.repositories     # Interfaces do Spring Data JPA
    │   ├── app.security         # JWT, filtros de autenticação e autorização
    │   ├── app.services         # Regras de negócio
    │   └── app.streaming        # Integração com API da Gumlet (HTTP client via OkHttp)
└── test
    └── java
        └── app.services         # Testes unitários com JUnit e Mockito
```

## 🚀 Como Executar

1. Clone o repositório:

   ```bash
   git clone https://github.com/Gsn00/Plataforma-de-Cursos.git
   ```

2. Navegue até o diretório do projeto:
   
   ```
   cd Plataforma-de-Cursos
   ```
3. Compile e execute a aplicação:

   ```
   ./mvnw spring-boot:run
   ```

4. Acesse a aplicação em http://localhost:8080.

   

   
