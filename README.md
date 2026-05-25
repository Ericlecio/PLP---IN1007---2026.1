# DSL de Testes Unitários — Funcional 3

> **PLP - IN1007 - 2026.1** | Universidade Federal de Pernambuco (UFPE) — Centro de Informática (CIn)  
> **Disciplina:** Paradigmas de Linguagens de Programação  
> **Professor:** Augusto Sampaio  
> **Equipe:** Ericlecio Thiago e Anderson M. Marinho

---

## Visão Geral

Este projeto estende a **Linguagem Funcional 3** com uma DSL (_Domain-Specific Language_) nativa para testes unitários. A arquitetura é inspirada no estilo **BDD (Behavior-Driven Development)** e em frameworks como Jest e HUnit, permitindo que os testes sirvam tanto como validadores de código quanto como documentação viva do comportamento do sistema.

### Problema resolvido

O processo de verificação tradicional é manual, intrusivo e bloqueante:

- Testes poluem o código principal com verificações manuais espalhadas.
- Erros de escopo ou tipagem interrompem a execução inteira (_Fail-Fast_), impedindo que o restante dos testes seja avaliado.

### Solução

- **Sintaxe declarativa BDD** com as palavras reservadas `describe`, `test`, `expect` e `toThrow`.
- **Isolamento de falhas (non-blocking):** exceções capturadas no nível da AST em Java, garantindo que a suíte continue mesmo após uma falha.
- **Relatório consolidado** impresso no terminal ao final de cada `describe`.
- **Exportação para CI/CD** via arquivo `test-results.json` gerado automaticamente.

---

## 1.1 Arquitetura de Resiliência e Isolamento

A grande inovação deste projeto não é apenas a sintaxe, mas a **gestão de exceções em tempo de execução**. Diferente da linguagem original — que propaga exceções nativas do Java (como `ArithmeticException` ou `NullPointerException`) até o encerramento do processo — nossa DSL implementa uma **barreira de isolamento (Sandbox AST)**:

- **Escudo de Compilação:** O `checaTipo` dos nós de teste (`toThrow`) é capaz de validar expressões que seriam inválidas em um programa comum.
- **Recuperação de Erro _(Error Recovery)_:** Exceções são capturadas no nível da AST, impedindo que a falha de um nó corrompa o ambiente global de execução e permitindo que a suíte de testes continue sua marcha.

---

## Fluxo de Execução com Isolamento

```
┌─────────────────────────────────────────────────────────┐
│                   describe("Suíte")                     │
│                                                         │
│   ┌─────────────┐   ┌─────────────┐   ┌─────────────┐  │
│   │   test 1    │   │   test 2    │   │   test 3    │  │
│   │  expect(…)  │   │  expect(…)  │   │  expect(…)  │  │
│   │             │   │             │   │             │  │
│   │  ✅ PASSA   │   │ ❌ FALHA    │   │  ✅ PASSA   │  │
│   └─────────────┘   └──────┬──────┘   └─────────────┘  │
│                            │                            │
│                     ┌──────▼──────┐                     │
│                     │  Sandbox    │                     │
│                     │  captura a  │                     │
│                     │  exceção ✓  │                     │
│                     └─────────────┘                     │
│                                                         │
│   Relatório Final: 2 passaram · 1 falhou                │
└─────────────────────────────────────────────────────────┘
```

A falha no Teste 2 é encapsulada pela Sandbox AST, garantindo a execução do Teste 3.

---

## Pré-requisitos

- **Java Development Kit (JDK)** 8 ou superior
- **Apache Maven**
- Terminal (PowerShell, Bash, etc.)

---

## Como Executar

**1. Criar o arquivo de entrada**

Na raiz do projeto (mesma pasta do `pom.xml`), crie um arquivo chamado `input` (sem extensão) com o código Funcional 3 que deseja testar.

**2. Compilar e gerar o parser**

```bash
# Primeiro limpa o projeto e gera as classes do Parser via JavaCC
mvn clean install
```

> **Nota:** O `mvn clean install` é essencial pois garante que o JavaCC processe o `.jj` antes de compilar os arquivos `.java` que dependem do Parser gerado. Usar apenas `mvn clean compile` pode resultar em falhas de dependência se as classes do parser ainda não existirem.

**3. Rodar o interpretador**

```bash
mvn exec:java "-Dexec.mainClass=lf3.plp.functional3.parser.Func3Parser"
```

O interpretador lê o arquivo `input` automaticamente e processa a AST.

---

## Exemplo de Uso

O exemplo abaixo valida uma função de saque: o caminho feliz (sucesso) e o caminho de erro esperado (exceção interna do interpretador).

**Arquivo `input`:**

```haskell
let
    var sacar = fn saldo valor . if saldo > valor then saldo - valor else saldo - "texto"
in
    describe("Operacao de Saque no Caixa Eletronico",
        test("Dado saldo positivo, Quando sacar, Entao deve liberar dinheiro",
            expect(sacar(100, 20), 80)
        ),
        test("Dado saldo insuficiente, Quando tentar sacar, Entao deve gerar erro nativo",
            toThrow(sacar(10, 50))
        )
    )
```

**Saída no terminal:**

```
=== RELATÓRIO DE TESTES: Operacao de Saque no Caixa Eletronico ===
Total Executados: 2
Passaram: 2
Falharam: 0
```

Um arquivo `test-results.json` também é gerado na raiz do projeto.

---

## Sintaxe da DSL (BNF)

As novas regras adicionadas à gramática da Funcional 3:

```bnf
Programa ::= Expressao

Expressao ::= Valor | ExpUnaria | ExpBinaria | ExpDeclaracao
            | Id | Aplicacao | IfThenElse
            | ExpDescribe | ExpTest | ExpExpect | ExpToThrow

ExpDescribe ::= "describe" "(" Expressao "," ListExp ")"
ExpTest     ::= "test"     "(" Expressao "," Expressao ")"
ExpExpect   ::= "expect"   "(" Expressao "," Expressao ")"
ExpToThrow  ::= "toThrow"  "(" Expressao ")"
```

A gramática completa está no arquivo `Funcional3.jj`.

---

## Arquitetura

| Componente | Descrição |
|---|---|
| `Funcional3.jj` | Gramática atualizada com os novos tokens, sem conflitos de recursividade. |
| `ExpDescribe` | Nó raiz da suíte; agrega os resultados e imprime o relatório final. |
| `ExpTest` | Atua como escudo da AST — captura erros e impede a interrupção da suíte. |
| `ExpExpect` | Valida a igualdade entre o valor obtido e o valor esperado. |
| `ExpToThrow` | Semântica invertida: intercepta exceções nativas e as converte em resultado positivo. |
| `TesteFalhouException` | Exceção customizada em Java para asserções incorretas, separada de erros sistêmicos reais. |
| `GerenciadorDeTestes` | Singleton responsável por acumular estados de execução, formatar o relatório e exportar o JSON. |

---

> ** Nota de Compatibilidade:** A DSL de Testes aqui implementada estende a gramática da Linguagem Funcional 3 com novos nós de AST. Portanto, estes comandos são executáveis exclusivamente através do interpretador Java customizado neste projeto (`mvn exec:java`), **não sendo compatíveis** com o transpilador Web legado do ambiente da disciplina.
