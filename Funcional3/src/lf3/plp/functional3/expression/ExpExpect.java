package lf3.plp.functional3.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.expression.Expressao;
import lf3.plp.expressions2.expression.Valor;
import lf3.plp.expressions2.expression.ValorBooleano;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;
import lf3.plp.functional3.exception.TesteFalhouException;

public class ExpExpect implements Expressao {

    private Expressao expressaoEsquerda;
    private Expressao expressaoDireita;

    public ExpExpect(Expressao expressaoEsquerda, Expressao expressaoDireita) {
        this.expressaoEsquerda = expressaoEsquerda;
        this.expressaoDireita = expressaoDireita;
    }

    @Override
    public Valor avaliar(AmbienteExecucao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        Valor valorEsq = expressaoEsquerda.avaliar(amb);
        Valor valorDir = expressaoDireita.avaliar(amb);

        if (valorEsq.equals(valorDir)) {
            return new ValorBooleano(true);
        } else {
            throw new TesteFalhouException("Esperado " + valorDir + ", mas obteve " + valorEsq);
        }
    }

    @Override
    public Expressao reduzir(AmbienteExecucao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        return avaliar(amb);
    }

    @Override
    public boolean checaTipo(AmbienteCompilacao amb)
            throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        return expressaoEsquerda.checaTipo(amb) && expressaoDireita.checaTipo(amb);
    }

    @Override
    public Tipo getTipo(AmbienteCompilacao amb) {
        // CORRIGIDO AQUI
        return TipoPrimitivo.BOOLEANO;
    }

    @Override
    public Expressao clone() {
        return new ExpExpect(expressaoEsquerda.clone(), expressaoDireita.clone());
    }
}