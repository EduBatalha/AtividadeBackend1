package domain;

import com.google.gson.reflect.TypeToken;
import data.JsonWriter;
import data.JsonReader;
import presentation.Clube;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

public class ArquivoPessoal {
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private GestaoEspacos gestaoEspacos;
    private static final String JSON_FILENAME = "registros.json";
    private Map<Socio, Map<Espaco, Map<LocalDateTime, Integer>>> historicoUsoPorSocio;

    public ArquivoPessoal() {
        gestaoEspacos = new GestaoEspacos();
        jsonWriter = new JsonWriter();
        jsonReader = new JsonReader();
        historicoUsoPorSocio = new HashMap<>();
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
                    }
                }
                case 2 -> registrarEntradaSaida(socio, null, false); // false representa saída
                case 3 -> lerRegistrosPorNumeroCarteirinha(numeroCarteirinha);
                default -> System.out.println("Opção inválida.");
            }
        } else {
            System.out.println("Sócio não encontrado.");
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
        Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> registros = jsonReader.readFromFile("registros.json", type);

        if (registros != null) {
            boolean foundSocio = false;

            for (Map<String, Object> registro : registros) {
                Double carteirinha = (Double) registro.get("numeroCarteirinha");

                if (carteirinha != null && carteirinha.intValue() == numeroCarteirinha) {
                    foundSocio = true;

                    String nomeSocio = (String) registro.get("nomeSocio");
                    System.out.println("===== Registros de Entrada e Saída para o Sócio " + nomeSocio + " =====");

                    String espaco = (String) registro.get("espaco");
                    System.out.println("- Espaço: " + espaco);

                    String horarioEntrada = (String) registro.get("horarioEntrada");
                    System.out.println("  - Entrada às " + horarioEntrada);

                    String horarioSaida = (String) registro.get("horarioSaida");
                    if (horarioSaida != null) {
                        System.out.println("  - Saída às " + horarioSaida);
                    }
                }
            }

            if (!foundSocio) {
                System.out.println("Nenhum sócio encontrado com o número de carteirinha " + numeroCarteirinha);
            }
        } else {
            System.out.println("Erro ao ler registros do arquivo JSON.");
        }
    }

    private void registrarEntradaSaida(Socio socio, Espaco espaco, boolean isEntrada) {
        if (isEntrada) {
            registrarEntrada(socio, espaco);
        } else {
            registrarSaida(socio.getNumeroCarteirinha());
        }
    }

    private void registrarEntrada(Socio socio, Espaco espaco) {
        LocalDateTime horarioEntrada = LocalDateTime.now();

        if (!historicoUsoPorSocio.containsKey(socio)) {
            historicoUsoPorSocio.put(socio, new HashMap<>());
        }

        Map<Espaco, Map<LocalDateTime, Integer>> historicoPorEspaco = historicoUsoPorSocio.get(socio);
        if (!historicoPorEspaco.containsKey(espaco)) {
            historicoPorEspaco.put(espaco, new HashMap<>());
        }

        Map<LocalDateTime, Integer> registros = historicoPorEspaco.get(espaco);

        if (!registros.isEmpty() && registros.values().stream().anyMatch(h -> h == -1)) {
            System.out.println("Não é possível registrar entrada. O sócio já está dentro do espaço.");
            return;
        }

        registros.put(horarioEntrada, -1); // -1 representa entrada

        System.out.println("Entrada registrada com sucesso!");

        // Registra no arquivo JSON
        Map<String, Object> registro = new HashMap<>();
        registro.put("nomeSocio", socio.getNome());
        registro.put("numeroCarteirinha", socio.getNumeroCarteirinha());
        registro.put("espaco", espaco.getNome()); // Alterado para nome do espaço
        registro.put("horarioEntrada", horarioEntrada.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")));
        registro.put("horarioSaida", null); // Ainda não há horário de saída

        jsonWriter.appendToJSONFile(JSON_FILENAME, registro); // Usar appendToJSONFile para adicionar o novo registro
    }

    private void registrarSaida(int numeroCarteirinha) {
        Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> registrosExistentes = jsonReader.readFromFile(JSON_FILENAME, type);

        if (registrosExistentes != null) {
            boolean foundMatchingEntry = false;

            for (Map<String, Object> registro : registrosExistentes) {
                int registroNumeroCarteirinha = ((Double) registro.get("numeroCarteirinha")).intValue();
                if (registroNumeroCarteirinha == numeroCarteirinha &&
                        registro.get("horarioSaida") == null) {
                    // Atualiza o horário de saída
                    registro.put("horarioSaida", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")));

                    foundMatchingEntry = true;
                    break;
                }
            }

            if (foundMatchingEntry) {
                jsonWriter.writeToFile(JSON_FILENAME, registrosExistentes);
                System.out.println("Saída registrada com sucesso!");
            } else {
                System.out.println("Registro de entrada correspondente não encontrado para registrar a saída.");
            }
        } else {
            System.out.println("Erro ao ler registros existentes no arquivo JSON.");
        }
    }
}
