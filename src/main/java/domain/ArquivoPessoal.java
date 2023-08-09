package domain;

import data.JsonWriter;
import presentation.Clube;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ArquivoPessoal {
    private Map<Socio, Map<Espaco, Map<LocalDateTime, Integer>>> historicoUsoPorSocio;
    private JsonWriter jsonWriter; // Adicione esta variável
    private static final String JSON_FILENAME = "registros.json";

    public ArquivoPessoal() {
        historicoUsoPorSocio = new HashMap<>();
        jsonWriter = new JsonWriter(); // Inicialize o JsonWriter
    }

    public void exibirMenuRegistrarEntradaSaida(Scanner scanner, Clube clube) {
        System.out.println("===== Registrar Entrada e Saída de Sócio em Espaço =====");
        System.out.print("Digite o número da carteirinha do sócio: ");
        int numeroCarteirinha = scanner.nextInt();
        scanner.nextLine();

        Socio socio = clube.getGerenciamentoSocio().consultarPorCarteirinha(numeroCarteirinha);

        if (socio != null) {
            System.out.println("Escolha uma opção:");
            System.out.println("1 - Registrar Entrada");
            System.out.println("2 - Registrar Saída");
            System.out.println("3 - Ler Registros por Número da Carteirinha");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> {
                    Espaco espaco = selecionarEspaco(scanner, clube.getGestaoEspacos());
                    if (espaco != null) {
                        registrarEntradaSaida(socio, espaco, true); // true representa entrada
                        System.out.println("Entrada registrada com sucesso!");
                    }
                }
                case 2 -> {
                    registrarEntradaSaida(socio, null, false); // false representa saída
                    System.out.println("Saída registrada com sucesso!");
                }
                case 3 -> lerRegistrosPorNumeroCarteirinha(numeroCarteirinha);
                default -> System.out.println("Opção inválida.");
            }
        } else {
            System.out.println("Sócio não encontrado.");
        }
    }


    private void registrarEntradaSaida(Socio socio, Espaco espaco, boolean isEntrada) {
        LocalDateTime horarioEntrada = LocalDateTime.now();
        LocalDateTime horarioSaida = horarioEntrada.plusHours(2); // Supondo que a permanência seja de 2 horas

        if (isEntrada) {
            registrarEntrada(socio, espaco, horarioEntrada, horarioSaida);
        } else {
            registrarSaida(socio, horarioEntrada, horarioSaida);
        }
    }

    private void registrarEntrada(Socio socio, Espaco espaco, LocalDateTime horarioEntrada, LocalDateTime horarioSaida) {
        if (historicoUsoPorSocio.containsKey(socio) && historicoUsoPorSocio.get(socio).containsKey(espaco)) {
            Map<LocalDateTime, Integer> registros = historicoUsoPorSocio.get(socio).get(espaco);
            if (!registros.isEmpty()) {
                LocalDateTime ultimoHorario = registros.keySet().stream().max(LocalDateTime::compareTo).get();
                if (registros.get(ultimoHorario) == -1) {
                    System.out.println("Não é possível registrar entrada. O sócio já está dentro do espaço.");
                    return;
                }
            }
        }

        historicoUsoPorSocio.putIfAbsent(socio, new HashMap<>());
        historicoUsoPorSocio.get(socio).putIfAbsent(espaco, new HashMap<>());

        historicoUsoPorSocio.get(socio).get(espaco).put(horarioEntrada, -1); // -1 representa entrada
        historicoUsoPorSocio.get(socio).get(espaco).put(horarioSaida, 1);     // 1 representa saída

        // Registra no arquivo JSON
        Map<String, Object> registro = new HashMap<>();
        registro.put("nomeSocio", socio.getNome());
        registro.put("numeroCarteirinha", socio.getNumeroCarteirinha());
        registro.put("espaco", espaco.getNome());
        registro.put("horarioEntrada", horarioEntrada.toString());
        registro.put("horarioSaida", horarioSaida.toString());
        jsonWriter.appendToJSONFile(JSON_FILENAME, registro);
    }

    private void registrarSaida(Socio socio, LocalDateTime horarioEntrada, LocalDateTime horarioSaida) {
        if (historicoUsoPorSocio.containsKey(socio)) {
            for (Espaco espaco : historicoUsoPorSocio.get(socio).keySet()) {
                Map<LocalDateTime, Integer> registros = historicoUsoPorSocio.get(socio).get(espaco);
                if (!registros.isEmpty()) {
                    LocalDateTime ultimoHorario = registros.keySet().stream().max(LocalDateTime::compareTo).get();
                    if (registros.get(ultimoHorario) == -1) {
                        historicoUsoPorSocio.get(socio).get(espaco).put(horarioSaida, 1); // 1 representa saída

                        // Registra saída no arquivo JSON
                        Map<String, Object> registro = new HashMap<>();
                        registro.put("nomeSocio", socio.getNome());
                        registro.put("numeroCarteirinha", socio.getNumeroCarteirinha());
                        registro.put("espaco", espaco.getNome());
                        registro.put("horarioEntrada", horarioEntrada.toString());
                        registro.put("horarioSaida", horarioSaida.toString());
                        jsonWriter.appendToJSONFile(JSON_FILENAME, registro);

                        break; // Registrar saída apenas no último espaço onde entrou
                    }
                }
            }
        } else {
            System.out.println("Nenhum histórico de uso encontrado para o sócio.");
        }
    }


    private Espaco selecionarEspaco(Scanner scanner, GestaoEspacos gestaoEspacos) {
        System.out.println("===== Espaços Disponíveis =====");

        List<Espaco> espacos = gestaoEspacos.getEspacos();
        for (int i = 0; i < espacos.size(); i++) {
            Espaco espaco = espacos.get(i);
            System.out.println(i + 1 + " - " + espaco.getNome() + " (" + espaco.getCategoria() + ")");
        }

        System.out.print("Digite o número do espaço desejado: ");
        int escolhaEspaco = scanner.nextInt();
        scanner.nextLine(); // Consumir a quebra de linha

        if (escolhaEspaco >= 1 && escolhaEspaco <= espacos.size()) {
            return espacos.get(escolhaEspaco - 1);
        } else {
            System.out.println("Espaço inválido.");
            return null;
        }
    }

    public void lerRegistrosPorNumeroCarteirinha(int numeroCarteirinha) {
        Socio socio = null;
        for (Socio s : historicoUsoPorSocio.keySet()) {
            if (s.getNumeroCarteirinha() == numeroCarteirinha) {
                socio = s;
                break;
            }
        }

        if (socio != null) {
            System.out.println("===== Registros de Entrada e Saída para o Sócio " + socio.getNome() + " =====");
            Map<Espaco, Map<LocalDateTime, Integer>> registros = historicoUsoPorSocio.get(socio);
            for (Espaco espaco : registros.keySet()) {
                System.out.println("- Espaço: " + espaco.getNome());
                Map<LocalDateTime, Integer> horarios = registros.get(espaco);
                for (Map.Entry<LocalDateTime, Integer> entry : horarios.entrySet()) {
                    String acao = entry.getValue() == -1 ? "Entrada" : "Saída";
                    LocalDateTime horario = entry.getKey();
                    System.out.println("  - " + acao + " às " + horario);
                }
            }
        } else {
            System.out.println("Nenhum sócio encontrado com o número de carteirinha " + numeroCarteirinha);
        }
    }

    public Map<Espaco, Map<LocalDateTime, Integer>> getHistoricoPorEspacoPorSocio(Socio socio) {
        return historicoUsoPorSocio.getOrDefault(socio, new HashMap<>());
    }

    public Map<Socio, Map<Espaco, Map<LocalDateTime, Integer>>> getHistoricoUsoPorSocio() {
        return historicoUsoPorSocio;
    }
}
