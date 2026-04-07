package lf3.plp.functional3.util;

import java.util.ArrayList;
import java.util.List;

public class GerenciadorDeTestes {
    private static GerenciadorDeTestes instancia;
    private int testesPassaram;
    private int testesFalharam;
    private List<String> logsDeFalha;

    private GerenciadorDeTestes() {
        reset();
    }

    public static GerenciadorDeTestes getInstancia() {
        if (instancia == null) {
            instancia = new GerenciadorDeTestes();
        }
        return instancia;
    }

    public void reset() {
        testesPassaram = 0;
        testesFalharam = 0;
        logsDeFalha = new ArrayList<>();
    }

    public void registrarSucesso() {
        testesPassaram++;
    }

    public void registrarFalha(String nomeTeste, String motivo) {
        testesFalharam++;
        logsDeFalha.add("[FALHOU] " + nomeTeste + " -> " + motivo);
    }

    public String gerarRelatorio(String nomeSuite) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== RELATÓRIO DE TESTES: ").append(nomeSuite).append(" ===\n");
        sb.append("Total Executados: ").append(testesPassaram + testesFalharam).append("\n");
        sb.append("Passaram: ").append(testesPassaram).append("\n");
        sb.append("Falharam: ").append(testesFalharam).append("\n");

        if (testesFalharam > 0) {
            sb.append("\nDetalhes das Falhas:\n");
            for (String log : logsDeFalha) {
                sb.append(log).append("\n");
            }
        }
        sb.append("=====================================\n");
        return sb.toString();
    }
}