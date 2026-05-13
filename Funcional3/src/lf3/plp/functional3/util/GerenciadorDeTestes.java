package lf3.plp.functional3.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public void exportarResultadosJson(String nomeSuite) {
        Path destino = Paths.get("test-results.json");
        String json = montarJson(nomeSuite);
        try (BufferedWriter w = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            w.write(json);
        } catch (IOException e) {
            System.err.println("Falha ao exportar test-results.json: " + e.getMessage());
        }
    }

    private String montarJson(String nomeSuite) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"suite\": \"").append(escapeJson(nomeSuite)).append("\",\n");
        sb.append("  \"total\": ").append(testesPassaram + testesFalharam).append(",\n");
        sb.append("  \"passed\": ").append(testesPassaram).append(",\n");
        sb.append("  \"failed\": ").append(testesFalharam).append(",\n");
        sb.append("  \"failures\": [");
        for (int i = 0; i < logsDeFalha.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("\"").append(escapeJson(logsDeFalha.get(i))).append("\"");
        }
        sb.append("]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }
}