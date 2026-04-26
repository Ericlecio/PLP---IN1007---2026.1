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
        GerenciadorDeTestes manager = GerenciadorDeTestes.getInstancia();
        String nomeDoTeste = null;
        try {
            Valor descValor = descricao.avaliar(amb);
            if (!(descValor instanceof ValorString)) {
                throw new TesteFalhouException("A descrição do teste deve ser uma string.");
            }
            nomeDoTeste = ((ValorString) descValor).valor();
            assercao.avaliar(amb);
            manager.registrarSucesso();
            return new ValorBooleano(true);
        } catch (TesteFalhouException e) {
            manager.registrarFalha(nomeRotulo(nomeDoTeste), mensagemOuTipo(e));
            return new ValorBooleano(false);
        } catch (Throwable t) {
            manager.registrarFalha(nomeRotulo(nomeDoTeste), mensagemErroNativo(t));
            return new ValorBooleano(false);
        }
    }

    private static String nomeRotulo(String nomeDoTeste) {
        return nomeDoTeste != null ? nomeDoTeste : "(erro ao obter nome do teste)";
    }

    private static String mensagemOuTipo(Throwable t) {
        String m = t.getMessage();
        return (m != null && !m.isEmpty()) ? m : t.getClass().getSimpleName();
    }

    private static String mensagemErroNativo(Throwable t) {
        String m = t.getMessage();
        if (m != null && !m.isEmpty()) {
            return "Erro nativo não tratado: " + m;
        }
        return "Erro nativo não tratado: " + t.getClass().getSimpleName();
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
        return TipoPrimitivo.BOOLEANO;
    }

    @Override
    public Expressao clone() {
        return new ExpTest(descricao.clone(), assercao.clone());
    }
}