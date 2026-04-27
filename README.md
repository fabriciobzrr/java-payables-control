# 📊 Java Payables Control

Sistema de controle de contas a pagar desenvolvido em Java com persistência em arquivo CSV.

## 🎯 Funcionalidades

- Cadastrar contas com descrição, valor, vencimento, categoria e fornecedor
- Listar todas as contas
- Pagar e estornar contas
- Filtrar por status (Em Aberto, Paga, Vencida, Estornada)
- Filtrar por período de vencimento
- Alerta de contas vencidas
- Resumo financeiro (total em aberto + vencidas)
- Persistência automática em arquivo CSV

## 🏗️ Estrutura do Projeto

```
src/
├── application/Main.java
├── entities/
│ ├── ContaPagar.java
│ └── enums/
│ ├── CategoriaConta.java
│ ├── OpcoesMenu.java
│ └── StatusConta.java
├── services/ContaPagarService.java
├── repository/ContaPagarRepository.java
└── exceptions/
├── ContaNaoEncontradaException.java
└── ValorInvalidoException.java

dados/contas.csv
```


## 🛠️ Tecnologias

- Java 25 LTS
- IntelliJ IDEA
- Arquivo CSV para persistência

## 📚 Conceitos aplicados

- POO (classes, encapsulamento, enums)
- Arquivos e persistência (CSV)
- Stream API (filtros, cálculos)
- Repository Pattern
- Tratamento de exceções personalizadas
- Validação em camadas

## 🚀 Como executar

1. Clone o repositório
2. Abra no IntelliJ
3. Execute a classe `application/Main.java`

## 👨‍💻 Autor

Fabricio Bezerra

## 🙏 Agradecimento

Nélio Alves - Curso Java COMPLETO (Udemy)
