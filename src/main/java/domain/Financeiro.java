package domain;

import com.google.gson.reflect.TypeToken;
import infrastructure.Espaco;
import infrastructure.JsonReader;
import application.ArquivoPessoal;
import application.Clube;
import infrastructure.Socio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Financeiro {
    private ArquivoPessoal arquivoPessoal;
    private GestaoEspacos gestaoEspacos;
    private Clube clube;
    private JsonReader jsonReader;

    public Financeiro(ArquivoPessoal arquivoPessoal, GestaoEspacos gestaoEspacos, Clube clube) {
        this.arquivoPessoal = arquivoPessoal;
        this.gestaoEspacos = gestaoEspacos;
        this.clube = clube;
        this.jsonReader = new JsonReader();
    }

    public void processarRegistros() {
        List<Map<String, Object>> registros = jsonReader.readFromFile("registros.json", new TypeToken<List<Map<String, Object>>>() {}.getType());

        for (Map<String, Object> registro : registros) {
            String nomeSocio = (String) registro.get("nomeSocio");
            LocalDateTime horarioEntrada = LocalDateTime.parse((String) registro.get("horarioEntrada"));
            String espaco = (String) registro.get("espaco");
            LocalDateTime horarioSaida = registro.containsKey("horarioSaida") ? LocalDateTime.parse((String) registro.get("horarioSaida")) : null;

            Espaco espacoObj = obterEspacoPorNome(espaco);
            Socio socio = obterSocioPorNome(nomeSocio);

            if (espacoObj != null && socio != null) {
                // Adicione o registro de uso ao espaço
                espacoObj.registrarUso(socio, horarioEntrada, horarioSaida);
            }
        }
    }
    private Espaco obterEspacoPorNome(String nomeEspaco) {
        return gestaoEspacos.getEspacos().stream()
                .filter(espaco -> espaco.getNome().equals(nomeEspaco))
                .findFirst()
                .orElse(null);
    }

    private Socio obterSocioPorNome(String nomeSocio) {
        return clube.getSocios().values().stream()  // Obtém os valores (Socios) do mapa
                .filter(socio -> socio.getNome().equals(nomeSocio))
                .findFirst()
                .orElse(null);
    }

    public void gerarRelatorioTempoUsoPorCategoria() {
        System.out.println("===== Relatório de Tempo de Uso por Categoria =====");

        Map<Espaco.Categoria, Map<Socio, Long>> tempoUsoPorCategoria = calcularTempoUsoPorCategoria();

        for (Map.Entry<Espaco.Categoria, Map<Socio, Long>> entry : tempoUsoPorCategoria.entrySet()) {
            Espaco.Categoria categoria = entry.getKey();
            System.out.println("Categoria: " + categoria);

            Map<Socio, Long> tempoPorSocio = entry.getValue();

            for (Map.Entry<Socio, Long> socioEntry : tempoPorSocio.entrySet()) {
                Socio socio = socioEntry.getKey();
                Long tempoTotalPorSocio = socioEntry.getValue();

                System.out.println("Sócio: " + socio.getNome() + ", Tempo de Uso: " + formatarTempo(tempoTotalPorSocio));
            }

            System.out.println("===============================================");
        }
    }

    public Map<Espaco.Categoria, Map<Socio, Long>> calcularTempoUsoPorCategoria() {
        Map<Espaco.Categoria, Map<Socio, Long>> tempoUsoPorCategoria = new HashMap<>();

        List<Espaco> espacos = gestaoEspacos.getEspacos();

        for (Espaco espaco : espacos) {
            Espaco.Categoria categoria = espaco.getCategoria();
            Map<Socio, Map<LocalDateTime, Integer>> registrosPorSocio = espaco.getRegistroUsoPorSocio();

            if (!tempoUsoPorCategoria.containsKey(categoria)) {
                tempoUsoPorCategoria.put(categoria, new HashMap<>());
            }

            Map<Socio, Long> tempoPorSocio = tempoUsoPorCategoria.get(categoria);

            for (Socio socio : registrosPorSocio.keySet()) {
                Map<LocalDateTime, Integer> registros = registrosPorSocio.get(socio);
                long tempoTotal = calcularTempoTotalUso(registros);
                tempoPorSocio.put(socio, tempoTotal);
            }
        }

        return tempoUsoPorCategoria;
    }

    public Map<LocalDateTime, Long> calcularTempoUsoPorHorario() {
        Map<LocalDateTime, Long> tempoUsoPorHorario = new HashMap<>();

        List<Espaco> espacos = gestaoEspacos.getEspacos();

        for (Espaco espaco : espacos) {
            Map<Socio, Map<LocalDateTime, Integer>> registrosPorSocio = espaco.getRegistroUsoPorSocio();

            for (Map<LocalDateTime, Integer> registros : registrosPorSocio.values()) {
                for (Map.Entry<LocalDateTime, Integer> entry : registros.entrySet()) {
                    LocalDateTime horario = entry.getKey();
                    long tempoUso = entry.getValue() == -1 ? calcularTempoUsoIndividual(registros, horario) : 0;

                    tempoUsoPorHorario.merge(horario, tempoUso, Long::sum);
                }
            }
        }

        return tempoUsoPorHorario;
    }

    private long calcularTempoUsoIndividual(Map<LocalDateTime, Integer> registros, LocalDateTime horario) {
        long tempoUso = 0;
        LocalDateTime entrada = null;

        for (Map.Entry<LocalDateTime, Integer> entry : registros.entrySet()) {
            if (entry.getKey().isBefore(horario) && entry.getValue() == -1) {
                entrada = entry.getKey();
            }
            if (entrada != null && entry.getKey().isAfter(horario) && entry.getValue() != -1) {
                Duration duration = Duration.between(entrada, entry.getKey());
                tempoUso += duration.getSeconds();
                entrada = null;
            }
        }

        return tempoUso;
    }

    public void gerarRelatorioTempoUsoPorSocio() {
        System.out.println("===== Relatório de Tempo de Uso por Sócio =====");
        List<Socio> socios = (List<Socio>) clube.getSocios();

        Map<Espaco.Categoria, Map<Socio, Long>> tempoUsoPorCategoria = calcularTempoUsoPorCategoria();

        for (Socio socio : socios) {
            System.out.println("Sócio: " + socio.getNome());

            for (Map.Entry<Espaco.Categoria, Map<Socio, Long>> entry : tempoUsoPorCategoria.entrySet()) {
                Espaco.Categoria categoria = entry.getKey();
                Map<Socio, Long> tempoPorSocio = entry.getValue();

                Long tempoTotalPorSocio = tempoPorSocio.get(socio);
                if (tempoTotalPorSocio != null) {
                    System.out.println("Categoria: " + categoria + ", Tempo de Uso: " + formatarTempo(tempoTotalPorSocio));
                } else {
                    System.out.println("Categoria: " + categoria + ", Tempo de Uso: 00:00");
                }
            }

            System.out.println("===============================================");
        }
    }


    public void gerarRelatorioTempoUsoPorHorario() {
        System.out.println("===== Relatório de Tempo de Uso por Horário =====");

        Map<LocalDateTime, Long> tempoUsoPorHorario = calcularTempoUsoPorHorario();

        for (Map.Entry<LocalDateTime, Long> entry : tempoUsoPorHorario.entrySet()) {
            LocalDateTime horarioInicio = entry.getKey();
            LocalDateTime horarioFim = horarioInicio.plusHours(1);
            Long tempoTotal = entry.getValue();

            System.out.println("Horário: " + horarioInicio + " - " + horarioFim + ", Tempo de Uso: " + formatarTempo(tempoTotal));
        }
    }


    public void gerarRelatorioTempoUsoPorEspaco() {
        System.out.println("===== Relatório de Tempo de Uso por Espaço =====");

        List<Espaco> espacos = gestaoEspacos.getEspacos();

        for (Espaco espaco : espacos) {
            System.out.println("Espaço: " + espaco.getNome());

            Map<LocalDateTime, Integer> registros = espaco.getRegistroUsoPorSocio().values().stream()
                    .flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            long tempoTotal = calcularTempoTotalUso(registros);

            System.out.println("Tempo de Uso: " + formatarTempo(tempoTotal));

            System.out.println("===============================================");
        }
    }

    private long calcularTempoTotalUso(Map<LocalDateTime, Integer> registros) {
        long tempoTotal = 0;
        LocalDateTime entrada = null;

        for (Map.Entry<LocalDateTime, Integer> entry : registros.entrySet()) {
            if (entry.getValue() == 1) { // Entrada
                entrada = entry.getKey();
            } else if (entrada != null) {
                LocalDateTime saida = entry.getKey();
                long duracaoSegundos = Duration.between(entrada, saida).getSeconds();
                tempoTotal += duracaoSegundos;
                entrada = null;
            }
        }

        return tempoTotal;
    }

    private String formatarTempo(long segundos) {
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        long segundosRestantes = segundos % 60;

        return String.format("%02d:%02d:%02d", horas, minutos, segundosRestantes);
    }
}
