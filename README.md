
# Staff Manager API

## Sobre

A Staff Manager API foi desenvolvida para ajudar o setor de Recursos Humanos (RH) de uma empresa a gerenciar times, funcionários e departamentos de forma simples e eficiente. Com ela, o RH pode centralizar as informações dos colaboradores, controlar a estrutura organizacional e facilitar a gestão operacional do quadro de funcionários, contribuindo para processos mais ágeis e organizados.

## Tabela de Conteúdos

- [Funcionalidades](#funcionalidades)  
- [Tecnologias](#tecnologias)  
- [Como Rodar](#como-rodar)  
- [Endpoints da API](#endpoints-da-api)  
  - [Departamentos](#departamentos)  
  - [Funcionários](#funcionários)  
  - [Fotos de Funcionários](#fotos-de-funcionários)  
- [Estado Atual](#estado-atual)  
- [Contribuição](#contribuição) 

## Funcionalidades

- [✓] Cadastro, edição, consultas, exclusão de departamentos  
- [✓] Gestão de funcionários, incluindo cadastro, edição, consulta, exclusão e upload/remoção de fotos  
- [Ainda não Implementada] Implementação de segurança (Spring Security)  
- [Ainda não Implementada] Documentação via Swagger  
- [Ainda não Implementada] Testes unitários e de integração  

## Tecnologias

- Java 21  
- Spring Boot 3.5.3 (Web, Data JPA, Validation) 
- Maven  
- MySQL
- MapStruct  
- Lombok  

## Como Rodar

1. Clone o repositório:  
   ```
   git clone https://github.com/MarceloB-Junior/staff_manager_api.git
   ```
2. Certifique-se de que o Java 21 e o Maven estão instalados e configurados na sua máquina.  
3. Configure o banco de dados MySQL local com a database `staff_manager_db` e ajuste as credenciais no arquivo `application.properties` caso necessário.  
4. No terminal, navegue até o diretório do projeto e execute:  
   ```
   mvn clean install
   mvn spring-boot:run
   ```
5. A aplicação estará disponível em `http://localhost:8080`.

## Endpoints da API

### Departamentos
- `GET /api/v1/departments`  
  Lista todos os departamentos com paginação e ordenação.  
- `GET /api/v1/departments/{id}`  
  Retorna detalhes de um departamento pelo seu ID.  
- `POST /api/v1/departments`  
  Cria um novo departamento.  
- `PUT /api/v1/departments/{id}`  
  Atualiza um departamento existente.  
- `DELETE /api/v1/departments/{id}`  
  Remove um departamento pelo ID.

### Funcionários
- `GET /api/v1/employees`  
  Lista todos os funcionários com paginação e ordenação.  
- `GET /api/v1/employees/{id}`  
  Retorna detalhes de um funcionário pelo seu ID.  
- `POST /api/v1/employees`  
  Cria um novo funcionário.  
- `PUT /api/v1/employees/{id}`  
  Atualiza um funcionário existente.  
- `DELETE /api/v1/employees/{id}`  
  Remove um funcionário pelo ID.  

### Fotos de Funcionários
- `GET /api/v1/employees/{id}/photo`  
  Busca a foto do funcionário pelo ID.  
- `POST /api/v1/employees/{id}/photo`  
  Faz upload ou atualização da foto do funcionário.  
- `DELETE /api/v1/employees/{id}/photo`  
  Remove a foto do funcionário.

## Estado Atual

Este projeto está em desenvolvimento e atualmente a versão é a 0.0.1-SNAPSHOT.  

> **ATENÇÃO:** Este projeto ainda não possui segurança implementada. Use em ambiente controlado e evite expor dados sensíveis até que a autenticação e autorização estejam funcionando.

## Contribuição

Para contribuir com o projeto, siga os passos abaixo:

- Faça um fork do repositório  
- Crie uma branch com a feature:  
  ```
  git checkout -b feature/nova-funcionalidade
  ```  
- Faça commit das suas alterações e envie ao repositório remoto  
- Abra um pull request para o branch principal do projeto  

Contribuições são muito bem-vindas! Por favor, siga as normas de código e padrões do projeto.

Se precisar de ajuda ou quiser sugerir melhorias, fique à vontade para abrir issues no repositório.

