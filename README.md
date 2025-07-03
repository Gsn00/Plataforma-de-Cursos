# Plataforma de Cursos Online

## 🎯 Visão Geral

Este projeto implementa o backend de uma plataforma de cursos online, desenvolvido com Java, Spring Boot e PostgreSQL. O objetivo é proporcionar uma experiência de aprendizado segura e eficiente, com funcionalidades robustas para diferentes perfis de usuários.

## ⚙️ Tecnologias Utilizadas

- **Java 17**: Linguagem principal do projeto.
- **Spring Boot**: Framework para desenvolvimento de aplicações Java.
- **Spring Data JPA**: ORM para interação com o banco de dados PostgreSQL.
- **Spring Security**: Implementação de autenticação e autorização com JWT e refresh tokens.
- **PostgreSQL**: Banco de dados relacional.
- **Gumlet API**: Serviço para upload de vídeos.
- **JUnit & Mockito**: Frameworks para testes unitários e mocks.

## 🔐 Funcionalidades

- **Autenticação e Autorização**: Implementação de login com JWT, incluindo refresh tokens armazenados no banco de dados.
- **Controle de Acesso por Papéis**: Diferenciação de permissões entre `Student`, `Teacher` e `Admin`.
- **Upload de Vídeos**: Integração com a API da Gumlet para upload e gerenciamento de vídeos.
- **Gerenciamento de Dados**: Criação de DTOs e Responses para estruturação de dados.
- **Testes Automatizados**: Cobertura de testes na camada de serviço utilizando JUnit e Mockito.
- **Gerenciamento de Exclusões em Cascata**: Implementação de lógica personalizada para exclusão de cursos, aulas e matrículas.

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

   

   
