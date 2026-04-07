package lf3.plp.functional3.expression;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.expression.Expressao;
import lf3.plp.expressions2.expression.Valor;
import lf3.plp.expressions2.expression.ValorBooleano;
import lf3.plp.expressions2.expression.ValorString;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;
import lf3.plp.functional3.exception.TesteFalhouException;
import lf3.plp.functional3.util.GerenciadorDeTestes;

public class ExpTest implements Expressao {

    private Expressao descricao;
    private Expressao assercao;

    public ExpTest(Expressao descricao, Expressao assercao) {
        this.descricao = descricao;
        this.assercao = assercao;
    }

    @Override
    public Valor avaliar(AmbienteExecucao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        String nomeDoTeste = ((ValorString) descricao.avaliar(amb)).valor();
        GerenciadorDeTestes manager = GerenciadorDeTestes.getInstancia();

        try {
            assercao.avaliar(amb);
            manager.registrarSucesso();
            return new ValorBooleano(true);
        } catch (TesteFalhouException e) {
            manager.registrarFalha(nomeDoTeste, e.getMessage());
            return new ValorBooleano(false);
        } catch (Exception e) {
            manager.registrarFalha(nomeDoTeste, "Erro nativo não tratado: " + e.getMessage());
            return new ValorBooleano(false);
        }
    }

    @Override
    public Expressao reduzir(AmbienteExecucao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        return avaliar(amb);
    }

    @Override
    public boolean checaTipo(AmbienteCompilacao amb)
            throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        return descricao.checaTipo(amb) && assercao.checaTipo(amb);
    }

    @Override
    public Tipo getTipo(AmbienteCompilacao amb) {
        // CORRIGIDO AQUI
        return TipoPrimitivo.BOOLEANO;
    }

    @Override
    public Expressao clone() {
        return new ExpTest(descricao.clone(), assercao.clone());
    }
}