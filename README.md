[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/Gsn00/plataforma-de-cursos/blob/main/LICENSE)

# Plataforma de Cursos Online

## 🎯 Visão Geral

Este projeto implementa o backend de uma plataforma de cursos online, desenvolvido com Java, Spring Boot e PostgreSQL. O objetivo é proporcionar uma experiência de aprendizado segura e eficiente, com funcionalidades robustas para diferentes perfis de usuários.

## ⚙️ Tecnologias Utilizadas

- **Java 17**: Linguagem principal do projeto.
- **Spring Boot**: Framework para desenvolvimento de aplicações Java.
- **Spring Data JPA**: ORM para interação com o banco de dados PostgreSQL.
- **Spring Security**: Implementação de autenticação e autorização com JWT e refresh tokens.
- **PostgreSQL**: Banco de dados relacional.
- **Lombok**: Redução de boilerplate.
- **Bean Validation**: Validação de entrada de dados.
- **Gumlet API**: Serviço para upload de vídeos.
- **OkHttp**: Cliente HTTP usado para consumo da API da Gumlet.
- **JUnit & Mockito**: Frameworks para testes unitários e mocks.
- **Swagger UI**: Documentação interativa.
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

## 🚀 Como Baixar

1. Clone o repositório:
   ```bash
   git clone https://github.com/Gsn00/Plataforma-de-Cursos.git
   ```
2. Navegue até o diretório do projeto:
   ```
   cd Plataforma-de-Cursos
   ```

## 🛠️ Configuração da API do Gumlet

Para que o upload de vídeos funcione corretamente, é necessário configurar uma API Key do Gumlet na aplicação.

### Passo 1: Criar uma conta no Gumlet

1. Acesse o [site oficial do Gumlet](https://www.gumlet.com/).
2. Clique em **Sign Up** para criar uma nova conta ou **Login** se já tiver uma conta.

### Passo 2: Gerar a API Key

1. Após o login, no painel do Gumlet, vá para **User > API Keys**.
2. Clique em **+ Generate API Key**.
3. Preencha o nome e as permissões desejadas para a chave.
4. Clique em **Create API Key**.
5. Copie a chave gerada.

### Passo 3: Configurar a API Key na aplicação

1. No seu projeto local, abra o arquivo `src/main/resources/application.properties`.
2. Adicione a seguinte linha, substituindo `SEU-GUMLET-API-KEY` pela chave copiada:
   ```properties
   gumlet.api.key=SEU-GUMLET-API-KEY
   ```

   Com isso, a aplicação estará pronta para interagir com a API do Gumlet para upload de vídeos.

## 🧪 Testando a API com Swagger

O projeto já inclui documentação interativa gerada com **Swagger UI**, que facilita os testes dos endpoints diretamente pelo navegador.

### Como acessar o Swagger:

1. Inicie a aplicação:
   ```bash
   ./mvnw spring-boot:run
    ```
2. Acesse a URL do Swagger no navegador:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```
3. Navegue pelas rotas disponíveis e faça testes de requisições (GET, POST, PUT, DELETE).
4. Para rotas protegidas, use o botão **Authorize** para inserir o token JWT após realizar o login.

## 👥 Contas pré-configuradas para testes
Ao iniciar a aplicação, três usuários são criados automaticamente no banco de dados para facilitar o teste das funcionalidades:
| Nome       | Email                                     | Senha    | Papel (Role) |
| ---------- | ----------------------------------------- | -------- | ------------ |
| John Doe   | [john@gmail.com](mailto:john@gmail.com)   | john123  | STUDENT      |
| Daryl Gray | [daryl@gmail.com](mailto:daryl@gmail.com) | daryl123 | TEACHER      |
| Ana Brown  | [ana@gmail.com](mailto:ana@gmail.com)     | ana123   | ADMIN        |

Você pode fazer login com esses dados para gerar um token JWT e testar as permissões específicas de cada papel.
   
