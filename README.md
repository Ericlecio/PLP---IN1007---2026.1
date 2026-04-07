# PLP - IN1007 - 2026.1: DSL de Testes Unitários

**Universidade Federal de Pernambuco (UFPE) - Centro de Informática (CIn)** **Disciplina:** Paradigmas de Linguagens de Programação  
**Professor:** Augusto Sampaio  
**Equipe:** Ericlecio Thiago e Anderson M. Marinho

---

## 1. Introdução e Motivação

Durante o desenvolvimento de programas nas linguagens construídas na disciplina, o processo de verificação de corretude (testes) costuma ser manual, intrusivo e bloqueante. Os testes frequentemente poluem o código principal com verificações manuais. Além disso, se uma função testada gera um erro de escopo ou tipagem, o interpretador interrompe a execução abruptamente (Fail-Fast), impedindo que o restante do programa seja avaliado.

Para solucionar esse problema, este projeto propõe a extensão da **Linguagem Funcional 3** com a criação de uma DSL (_Domain-Specific Language_) nativa para Testes Unitários, inspirada em frameworks consolidados no mercado, como Jest e HUnit.

## 2. Objetivos e Escopo

O objetivo central é prover suporte nativo na Árvore Sintática Abstrata (AST) para a escrita e execução de testes isolados.

### Características da implementação:

- **Sintaxe Declarativa:** Adição das palavras reservadas `describe`, `test`, `expect` (para fluxos de sucesso) e `toThrow` (para fluxos de erro esperado).
- **Isolamento de Falhas (Non-blocking):** Falhas em asserções ou exceções lançadas durante a avaliação de um teste serão capturadas pelo interpretador (_try/catch_ no nível da AST em Java), garantindo que o programa continue rodando a suíte.
- **Relatório Consolidado:** Ao final da execução de uma suíte (`describe`), o interpretador exibirá no terminal um relatório sumarizando a quantidade de testes que passaram, que falharam e os respectivos logs de erro.
- **Exportação para CI/CD:** Geração automática de um arquivo `test-results.json` na raiz do projeto com os resultados da execução, facilitando integrações futuras.

---

## 3. Pré-requisitos

Para compilar e executar o projeto, você precisará ter instalado em sua máquina:

- **Java Development Kit (JDK)** versão 8 ou superior.
- **Apache Maven** (para gerenciar o _build_ e rodar o _JavaCC_).
- Terminal ou Prompt de Comando (PowerShell, Bash, etc).

---

## 4. Como Executar o Projeto

**Passo 1: Preparar o arquivo de teste** Na raiz do projeto (mesma pasta onde fica o `pom.xml`), crie um arquivo de texto chamado **`input`** (sem nenhuma extensão, como `.txt`). Cole nele o código na linguagem Funcional 3 que deseja testar.

**Passo 2: Compilar o código fonte e gerar o Parser** Abra o terminal na pasta raiz do projeto e execute o comando abaixo. Ele fará o Maven ler as regras do `.jj` e gerar as classes do interpretador:

```bash
mvn clean compile
```

**Passo 3: Rodar o interpretador** Execute o comando abaixo para iniciar o programa. O interpretador lerá automaticamente o arquivo `input` e processará a AST:

```bash
mvn exec:java "-Dexec.mainClass=lf3.plp.functional3.parser.Func3Parser"
```

---

## 5. Exemplo de Uso (Sintaxe Validada)

Na Funcional 3, como um programa é uma expressão, a suíte de testes será avaliada como tal. O exemplo abaixo demonstra o uso validando tanto o caminho feliz, quanto erros lógicos e a captura de uma exceção interna do interpretador:

**Arquivo `input`:**

```haskell
let
    var soma = fn x y . x + y
in
    describe("Suite Calculadora",
        test("Soma simples", expect(soma(2, 3), 5)),
        test("Soma errada proposital", expect(soma(2, 2), 99)),
        test("Captura erro de tipo", toThrow(soma(1, "texto")))
    )
```

### Saída no Terminal:

```text
=== RELATÓRIO DE TESTES: Suite Calculadora ===
Total Executados: 3
Passaram: 2
Falharam: 1

Detalhes das Falhas:
[FALHOU] Soma errada proposital -> Esperado 99, mas obteve 4
=====================================
```

_(Além disso, um arquivo `test-results.json` será gerado na raiz do projeto)._

---

## 6. Backus-Naur Form (BNF) Atualizada

```bnf
Programa ::= Expressao

Expressao ::= Valor | ExpUnaria | ExpBinaria | ExpDeclaracao
            | Id | Aplicacao | IfThenElse
            | ExpDescribe | ExpTest | ExpExpect | ExpToThrow

Valor ::= ValorConcreto | ValorAbstrato
ValorAbstrato ::= ValorFuncao
ValorConcreto ::= ValorInteiro | ValorBooleano | ValorString
ValorFuncao ::= "fn" ListId "." Expressao

ExpUnaria ::= "-" Expressao | "not" Expressao | "length" Expressao
ExpBinaria ::= Expressao "+" Expressao | Expressao "-" Expressao
             | Expressao "and" Expressao | Expressao "or" Expressao
             | Expressao "==" Expressao | Expressao "++" Expressao

ExpDeclaracao ::= "let" DeclaracaoFuncional "in" Expressao
DeclaracaoFuncional ::= DecVariavel | DecFuncao | DecComposta
DecVariavel ::= "var" Id "=" Expressao
DecFuncao ::= "fun" ListId "=" Expressao
DecComposta ::= DeclaracaoFuncional "," DeclaracaoFuncional

ListId ::= Id | Id ListId
Aplicacao ::= Expressao "(" ListExp ")"
ListExp ::= Expressao | Expressao "," ListExp

// --- NOVAS REGRAS DA DSL DE TESTES ---
ExpDescribe ::= "describe" "(" Expressao "," ListExp ")"
ExpTest ::= "test" "(" Expressao "," Expressao ")"
ExpExpect ::= "expect" "(" Expressao "," Expressao ")"
ExpToThrow ::= "toThrow" "(" Expressao ")"
```

---

## 7. Classes Principais Desenvolvidas

- **`Funcional3.jj`:** Arquivo de gramática alterado para incorporar os novos _tokens_ (`describe`, `test`, `expect`, `toThrow`) evitando conflitos de recursividade com a estrutura padrão.
- **`ExpDescribe`, `ExpTest`, `ExpExpect`:** Novas classes que herdam de _Expressao_. O `ExpTest` atua como um escudo protetor da AST (engolindo erros), e o `ExpExpect` valida nativamente a igualdade de valores.
- **`ExpToThrow`:** Nó com semântica invertida. Intercepta erros sistêmicos (_Exceptions_ nativas do interpretador) tornando-os resultados positivos em fluxos de testes onde o erro era desejado.
- **`TesteFalhouException`:** Exceção customizada no Java para gerenciar asserções incorretas de forma controlada, separando-as de erros sistêmicos reais da linguagem.
- **`GerenciadorDeTestes`:** Classe utilitária (_Singleton_) no interpretador Java responsável por armazenar os estados das execuções em memória (_side-effects_ pacíficos), além de formatar o relatório impresso no console e exportar o arquivo JSON final.
