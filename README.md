# PLP---IN1007---2026.1

1. Introdução e Motivação
Durante o desenvolvimento de programas nas linguagens construídas na disciplina, o processo de verificação de corretude (testes) costuma ser manual e intrusivo. Os testes frequentemente poluem o código principal com estruturas condicionais ou verificações manuais. Além disso, se uma função testada gera um erro, o interpretador interrompe a execução abruptamente, impedindo que o restante do programa seja avaliado.

Para solucionar esse problema, este projeto propõe a extensão da Linguagem Funcional 2 com a criação de uma DSL (Domain-Specific Language) nativa para Testes Unitários, inspirada em frameworks consolidados no mercado de software, como Jest e HUnit.

2. Objetivos e Escopo
O objetivo central é prover suporte nativo na Árvore Sintática Abstrata (AST) para a escrita e execução de testes isolados.

Características da implementação:

Sintaxe Declarativa: Adição das palavras reservadas describe, test e expect para estruturação lógica dos casos de teste.

Isolamento de Falhas (Non-blocking): Falhas em asserções ou exceções lançadas durante a avaliação de um teste serão capturadas pelo interpretador (try/catch no nível da AST em Java), garantindo que o programa continue rodando os demais testes.

Relatório Consolidado: Ao final da execução de uma suíte (describe), o interpretador exibirá no terminal um relatório sumarizando a quantidade de testes que passaram, que falharam e os respectivos logs de erro.

3. Exemplo de Uso (Sintaxe Esperada)
Na Funcional 2, como um programa é uma expressão e funções são cidadãos de primeira classe, a suíte de testes será avaliada como tal. Os testes serão passados como uma sequência de expressões separadas por vírgula.

let 
    var soma = fn x, y . x + y,
    var multiplica = fn x, y . x * y
in
    describe("Suite de Testes da Calculadora", 
        test("Deve somar dois numeros corretamente", 
            expect(soma(2, 3), 5)
        ),
        test("Deve falhar ao tentar somar errado", 
            expect(soma(2, 2), 99)
        ),
        test("Deve multiplicar corretamente", 
            expect(multiplica(3, 3), 9)
        )
    )

4. Backus-Naur Form (BNF) Atualizada


Programa ::= Expressao

Expressao ::= Valor
            | ExpUnaria
            | ExpBinaria
            | ExpDeclaracao
            | Id
            | Aplicacao
            | IfThenElse
            | - > ExpDescribe
            | - > ExpTest
            | - > ExpExpect

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

// ---  NOVAS REGRAS DA DSL DE TESTES ---

ExpDescribe ::= "describe" "(" Expressao "," ListExp ")"

ExpTest ::= "test" "(" Expressao "," Expressao ")"

ExpExpect ::= "expect" "(" Expressao "," Expressao ")"


5. Classes Principais a serem Desenvolvidas
ExpDescribe, ExpTest, ExpExpect: Novas classes que herdam de Expressao, responsáveis pela lógica de avaliação na AST.

TesteFalhouException: Exceção customizada no Java para gerenciar asserções incorretas de forma controlada.

GerenciadorDeTestes: Classe utilitária no interpretador Java responsável por armazenar os contadores de sucesso/falha e formatar o relatório impresso no console.
