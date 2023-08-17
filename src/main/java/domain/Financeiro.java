package domain;

import application.ArquivoPessoal;
import com.google.gson.reflect.TypeToken;
import infrastructure.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Financeiro {
    private ArquivoPessoal arquivoPessoal;
    private GestaoEspacos gestaoEspacos;
    private SocioNegocio socioNegocio;
    private JsonReader jsonReader;

    public Financeiro(ArquivoPessoal arquivoPessoal, GestaoEspacos gestaoEspacos, SocioNegocio socioNegocio) {
        this.arquivoPessoal = arquivoPessoal;
        this.gestaoEspacos = gestaoEspacos;
        this.socioNegocio = socioNegocio;
        this.jsonReader = new JsonReader();
    }

    public void processarRegistros() {
        if (jsonReader.fileExists("registros.json")) {
            Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> registros = jsonReader.readFromFile("registros.json", type);

            for (Map<String, Object> registro : registros) {
                String nomeSocio = (String) registro.get("nomeSocio");
                LocalDateTime horarioEntrada = LocalDateTime.parse((String) registro.get("horarioEntrada"));
                String espaco = (String) registro.get("espaco");
                LocalDateTime horarioSaida = registro.containsKey("horarioSaida") ? LocalDateTime.parse((String) registro.get("horarioSaida")) : null;

                Espaco espacoObj = obterEspacoPorNome(espaco);
                Socio socio = obterSocioPorNome(nomeSocio);

                if (espacoObj != null && socio != null) {
                    espacoObj.registrarUso(socio, horarioEntrada, horarioSaida);
                }
            }
        } else {
            System.out.println("O arquivo de registros não existe.");
        }
    }

    private long calcularTempoTotalUso(Map<LocalDateTime, Integer> registros, Espaco.Categoria categoria) {
        LocalDateTime entrada = null;
        long tempoTotal = 0;

        for (Map.Entry<LocalDateTime, Integer> entry : registros.entrySet()) {
            if (entry.getValue() == 1) {
                entrada = entry.getKey();
            } else if (entrada != null && entry.getValue() == -1) {
                LocalDateTime saida = entry.getKey();
                Duration duration = Duration.between(entrada, saida);
                tempoTotal += duration.toMillis() / 1000; // Conversão para segundos
                entrada = null;
            }
        }

        return tempoTotal;
    }

    private Espaco obterEspacoPorNome(String nomeEspaco) {
        return gestaoEspacos.getEspacos().stream()
                .filter(espaco -> espaco.getNome().equals(nomeEspaco))
                .findFirst()
                .orElse(null);
    }

    private Socio obterSocioPorNome(String nomeSocio) {
        return socioNegocio.getSocios().values().stream()
                .filter(socio -> socio.getNome().equals(nomeSocio))
                .findFirst()
                .orElse(null);
    }

    public void gerarRelatorioTempoUsoPorCategoria() {
        processarRegistros();
        System.out.println("===== Relatório de Tempo de Uso por Categoria =====");

        Map<Espaco.Categoria, Map<Socio, Long>> tempoUsoPorCategoria = calcularTempoUsoPorCategoria();

        for (Map.Entry<Espaco.Categoria, Map<Socio, Long>> entry : tempoUsoPorCategoria.entrySet()) {
            Espaco.Categoria categoria = entry.getKey();
            System.out.println("Categoria: " + categoria);

            Map<Socio, Long> tempoPorSocio = entry.getValue();
            long tempoTotalCategoria = 0;

            for (Long tempo : tempoPorSocio.values()) {
                tempoTotalCategoria += tempo;
            }

            System.out.println("Tempo de Uso Total: " + formatarTempo(tempoTotalCategoria));
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
                long tempoTotal = calcularTempoTotalUso(registros, categoria); // Passa a categoria como parâmetro
                tempoPorSocio.put(socio, tempoTotal);
            }
        }

        return tempoUsoPorCategoria;
    }

    public void gerarRelatorioTempoUsoPorSocio() {
        processarRegistros();
        System.out.println("===== Relatório de Tempo de Uso por Sócio =====");

        Map<Espaco.Categoria, Map<Socio, Long>> tempoUsoPorCategoria = calcularTempoUsoPorCategoria();

        for (Socio socio : socioNegocio.getSocios().values()) {
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

    private String formatarTempo(long segundos) {
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        long segundosRestantes = segundos % 60;

        return String.format("%02d:%02d:%02d", horas, minutos, segundosRestantes);
    }

    public void gerarRelatorioTempoUsoPorEspaco() {
        processarRegistros();
        System.out.println("===== Relatório de Tempo de Uso por Espaço =====");

        List<Espaco> espacos = gestaoEspacos.getEspacos();

        for (Espaco espaco : espacos) {
            System.out.println("Espaço: " + espaco.getNome());

            long tempoTotal = calcularTempoTotalUsoPorEspaco(espaco); // Novo método para calcular tempo total de uso

            System.out.println("Tempo de Uso: " + formatarTempo(tempoTotal));

            System.out.println("===============================================");
        }
    }

    private long calcularTempoTotalUsoPorEspaco(Espaco espaco) {
        long tempoTotal = 0;

        Map<Socio, Map<LocalDateTime, Integer>> registrosPorSocio = espaco.getRegistroUsoPorSocio();

        for (Map<LocalDateTime, Integer> registros : registrosPorSocio.values()) {
            tempoTotal += calcularTempoTotalUso(registros, espaco.getCategoria());
        }

        return tempoTotal;
    }

    public void gerarRelatorioTempoUsoPorHorario() {
        processarRegistros();
        System.out.println("===== Relatório de Tempo de Uso por Horário =====");

        Map<Espaco.Categoria, Map<Integer, Long>> tempoUsoPorHorario = calcularTempoUsoPorHorario();

        for (Map.Entry<Espaco.Categoria, Map<Integer, Long>> entry : tempoUsoPorHorario.entrySet()) {
            Espaco.Categoria categoria = entry.getKey();
            System.out.println("Categoria: " + categoria);

            Map<Integer, Long> tempoPorHorario = entry.getValue(); // Correção aqui
            int horarioPico = encontrarHorarioPico(tempoPorHorario);

            for (Map.Entry<Integer, Long> horarioEntry : tempoPorHorario.entrySet()) {
                int horario = horarioEntry.getKey();
                long tempo = horarioEntry.getValue();

                System.out.println("Horário: " + horario + ":00 - " + (horario + 1) + ":00, Tempo de Uso: " + formatarTempo(tempo));

                if (horario == horarioPico) {
                    System.out.println("**** Horário de Pico ****");
                }
            }

            System.out.println("===============================================");
        }
    }

    private Map<Espaco.Categoria, Map<Integer, Long>> calcularTempoUsoPorHorario() {
        Map<Espaco.Categoria, Map<Integer, Long>> tempoUsoPorHorario = new HashMap<>();

        List<Espaco> espacos = gestaoEspacos.getEspacos();

        for (Espaco espaco : espacos) {
            Espaco.Categoria categoria = espaco.getCategoria();
            Map<Socio, Map<LocalDateTime, Integer>> registrosPorSocio = espaco.getRegistroUsoPorSocio();

            if (!tempoUsoPorHorario.containsKey(categoria)) {
                tempoUsoPorHorario.put(categoria, new HashMap<>());
            }

            Map<Integer, Long> tempoPorHorario = tempoUsoPorHorario.get(categoria); // Correção aqui

            for (Map<LocalDateTime, Integer> registros : registrosPorSocio.values()) {
                int entradaHora = -1;
                LocalDateTime entrada = null;

                for (Map.Entry<LocalDateTime, Integer> entry : registros.entrySet()) {
                    LocalDateTime horario = entry.getKey();
                    int hora = horario.getHour();

                    if (entry.getValue() == 1) { // Entrada
                        entrada = horario;
                        entradaHora = hora;
                    } else if (entrada != null && entry.getValue() == -1) { // Saída
                        if (hora > entradaHora) {
                            long segundos = ChronoUnit.SECONDS.between(entrada, horario);
                            tempoPorHorario.put(hora, tempoPorHorario.getOrDefault(hora, 0L) + segundos);
                        }

                        entrada = null;
                    }
                }
            }
        }

        return tempoUsoPorHorario;
    }

    private int encontrarHorarioPico(Map<Integer, Long> tempoPorHorario) {
        int horarioPico = -1;
        long maxTempo = 0;

        for (Map.Entry<Integer, Long> entry : tempoPorHorario.entrySet()) {
            int horario = entry.getKey();
            long tempo = entry.getValue();

            if (tempo > maxTempo) {
                maxTempo = tempo;
                horarioPico = horario;
            }
        }

        return horarioPico;
    }
}

