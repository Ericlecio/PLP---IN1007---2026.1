package lf3.plp.functional3.expression;

import java.util.ArrayList;
import java.util.List;

import lf3.plp.expressions1.util.Tipo;
import lf3.plp.expressions1.util.TipoPrimitivo;
import lf3.plp.expressions2.expression.Expressao;
import lf3.plp.expressions2.expression.Valor;
import lf3.plp.expressions2.expression.ValorString;
import lf3.plp.expressions2.memory.AmbienteCompilacao;
import lf3.plp.expressions2.memory.AmbienteExecucao;
import lf3.plp.expressions2.memory.VariavelJaDeclaradaException;
import lf3.plp.expressions2.memory.VariavelNaoDeclaradaException;
import lf3.plp.functional3.util.GerenciadorDeTestes;

public class ExpDescribe implements Expressao {

    private Expressao descricao;
    private List<Expressao> testes;

    public ExpDescribe(Expressao descricao, List<Expressao> testes) {
        this.descricao = descricao;
        this.testes = testes;
    }

    @Override
    public Valor avaliar(AmbienteExecucao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        String nomeSuite = ((ValorString) descricao.avaliar(amb)).valor();
        GerenciadorDeTestes manager = GerenciadorDeTestes.getInstancia();

        manager.reset();

        for (Expressao teste : testes) {
            teste.avaliar(amb);
        }

        String relatorio = manager.gerarRelatorio(nomeSuite);
        System.out.println(relatorio);
        manager.exportarResultadosJson(nomeSuite);

        return new ValorString(relatorio);
    }

    @Override
    public Expressao reduzir(AmbienteExecucao amb) throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        return avaliar(amb);
    }

    @Override
    public boolean checaTipo(AmbienteCompilacao amb)
            throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        boolean tiposOk = descricao.checaTipo(amb);
        for (Expressao teste : testes) {
            tiposOk = tiposOk && teste.checaTipo(amb);
        }
        return tiposOk;
    }

    @Override
    public Tipo getTipo(AmbienteCompilacao amb) {
        // CORRIGIDO AQUI
        return TipoPrimitivo.STRING;
    }

    @Override
    public Expressao clone() {
        List<Expressao> novaLista = new ArrayList<>();
        for (Expressao exp : testes) {
            novaLista.add(exp.clone());
        }
        return new ExpDescribe(descricao.clone(), novaLista);
    }
}