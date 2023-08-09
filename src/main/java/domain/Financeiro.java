package domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Financeiro {
    private Map<Socio, Map<Espaco, Integer>> usoEspacosPorSocio;

    public Financeiro() {
        usoEspacosPorSocio = new HashMap<>();
    }

    public void registrarUsoEspaco(Socio socio, Espaco espaco, int horas) {
        usoEspacosPorSocio.computeIfAbsent(socio, k -> new HashMap<>());
        usoEspacosPorSocio.get(socio).merge(espaco, horas, Integer::sum);
    }

    public void imprimirRelatorioTempoUsoPorSocio() {
        System.out.println("===== Relatório de Tempo de Uso por Sócio =====");
        for (Socio socio : usoEspacosPorSocio.keySet()) {
            System.out.println("Sócio: " + socio.getNome());
            Map<Espaco, Integer> espacos = usoEspacosPorSocio.get(socio);
            for (Espaco espaco : espacos.keySet()) {
                int horas = espacos.get(espaco);
                System.out.println("- " + espaco.getNome() + ": " + horas + " horas");
            }
        }
    }

    public void imprimirRelatorioTempoUsoPorHorario() {
        System.out.println("===== Relatório de Tempo de Uso por Horário =====");

        // Mapa para armazenar o total de horas de uso por horário
        Map<String, Integer> totalHorasPorHorario = new HashMap<>();

        // Percorrer os registros de uso de espaços por sócio
        for (Map<Espaco, Integer> espacos : usoEspacosPorSocio.values()) {
            for (Map.Entry<Espaco, Integer> entry : espacos.entrySet()) {
                int horas = entry.getValue();

                // Obter o horário atual do sistema
                LocalDateTime horarioAtual = LocalDateTime.now();

                // Montar a representação do horário como string (ex: "09:00")
                String horarioStr = horarioAtual.getHour() + ":" + horarioAtual.getMinute();

                // Incrementar o total de horas para o horário correspondente
                totalHorasPorHorario.merge(horarioStr, horas, Integer::sum);
            }
        }

        // Exibir o relatório de tempo de uso por horário
        for (Map.Entry<String, Integer> entry : totalHorasPorHorario.entrySet()) {
            String horario = entry.getKey();
            int totalHoras = entry.getValue();

            System.out.println("- Horário " + horario + ": " + totalHoras + " horas");
        }
    }

    // Método para obter o relatório de tempo de uso por sócio
    public Map<Socio, Map<Espaco, Integer>> getRelatorioTempoUsoPorSocio() {
        return usoEspacosPorSocio;
    }
}
