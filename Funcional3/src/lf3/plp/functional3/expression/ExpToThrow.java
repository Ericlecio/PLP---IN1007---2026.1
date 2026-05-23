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

public class ExpToThrow implements Expressao {

    private Expressao expressaoAlvo;

    public ExpToThrow(Expressao expressaoAlvo) {
        this.expressaoAlvo = expressaoAlvo;
    }

    @Override
    public Valor avaliar(AmbienteExecucao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        try {
            expressaoAlvo.avaliar(amb);
            throw new TesteFalhouException(
                    "Era esperada uma exceção nativa, mas a expressão foi avaliada com sucesso.");
        } catch (TesteFalhouException e) {
            throw e;
        } catch (Throwable e) {
            return new ValorBooleano(true);
        }
    }

    @Override
    public Expressao reduzir(AmbienteExecucao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        return avaliar(amb);
    }

    @Override
    public boolean checaTipo(AmbienteCompilacao amb)
            throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        try {
            System.out.println("Debug: " + this.getClass().getSimpleName() + " checado com sucesso.");
            return true;
        } catch (Exception e) {
            System.out.println("Debug: ERRO em " + this.getClass().getSimpleName() + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public Tipo getTipo(AmbienteCompilacao amb) {
        return TipoPrimitivo.BOOLEANO;
    }

    @Override
    public Expressao clone() {
        return new ExpToThrow(expressaoAlvo.clone());
    }
}