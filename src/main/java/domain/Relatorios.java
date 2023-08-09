package domain;

import data.JsonWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.*;

public class Relatorios {
    private Financeiro financeiro;
    private ArquivoPessoal arquivoPessoal;
    private GestaoEspacos gestaoEspacos;

    public Relatorios(Financeiro financeiro, ArquivoPessoal arquivoPessoal, GestaoEspacos gestaoEspacos) {
        this.financeiro = financeiro;
        this.arquivoPessoal = arquivoPessoal;
        this.gestaoEspacos = gestaoEspacos;
    }

    public void exibirMenuRelatorios(Scanner scanner) {
        int escolha;

        do {
            System.out.println("===== Submenu Relatórios =====");
            System.out.println("1 - Relatório de Tempo Total de Uso por Sócio");
            System.out.println("2 - Relatório de Tempo Total de Uso por Espaço");
            System.out.println("3 - Relatório de Tempo Total de Uso por Categoria");
            System.out.println("4 - Relatório de Tempo de Uso por Horário");
            System.out.println("5 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            escolha = scanner.nextInt();
            scanner.nextLine();

            switch (escolha) {
                case 1 -> gerarRelatorioTempoTotalUsoPorSocio();
                case 2 -> gerarRelatorioTempoTotalUsoPorEspaco();
                case 3 -> gerarRelatorioTempoTotalUsoPorCategoria();
                case 4 -> gerarRelatorioTempoUsoPorHorario();
                case 5 -> System.out.println("Voltando ao Menu Principal...");
                default -> System.out.println("Opção inválida.");
            }

        } while (escolha != 5);
    }

    public void gerarRelatorioTempoTotalUsoPorSocio() {
        System.out.println("===== Relatório de Tempo Total de Uso por Sócio =====");

        Map<Socio, Integer> tempoTotalPorSocio = new HashMap<>();
        for (Socio socio : arquivoPessoal.getHistoricoUsoPorSocio().keySet()) {
            Map<Espaco, Map<LocalDateTime, Integer>> historicoPorEspacoPorSocio = arquivoPessoal.getHistoricoPorEspacoPorSocio(socio);

            int tempoTotal = 0;
            for (Map<LocalDateTime, Integer> historicoPorHorario : historicoPorEspacoPorSocio.values()) {
                for (int horas : historicoPorHorario.values()) {
                    tempoTotal += horas;
                }
            }
            tempoTotalPorSocio.put(socio, tempoTotal);
        }

        for (Map.Entry<Socio, Integer> entry : tempoTotalPorSocio.entrySet()) {
            Socio socio = entry.getKey();
            int tempoTotal = entry.getValue();
            System.out.println(socio.getNome() + ": " + tempoTotal + " horas");
        }
    }

    public void gerarRelatorioTempoTotalUsoPorEspaco() {
        System.out.println("===== Relatório de Tempo Total de Uso por Espaço =====");

        Map<Espaco, Integer> tempoTotalPorEspaco = new HashMap<>();
        for (Map<Espaco, Map<LocalDateTime, Integer>> historicoPorEspaco : arquivoPessoal.getHistoricoUsoPorSocio().values()) {
            for (Map<LocalDateTime, Integer> historicoPorHorario : historicoPorEspaco.values()) {
                for (int horas : historicoPorHorario.values()) {
                    Espaco espaco = historicoPorEspaco.keySet().iterator().next();
                    tempoTotalPorEspaco.merge(espaco, horas, Integer::sum);
                }
            }
        }

        for (Map.Entry<Espaco, Integer> entry : tempoTotalPorEspaco.entrySet()) {
            Espaco espaco = entry.getKey();
            int tempoTotal = entry.getValue();
            System.out.println(espaco.getNome() + ": " + tempoTotal + " horas");
        }
    }



    public void gerarRelatorioTempoTotalUsoPorCategoria() {
        System.out.println("===== Relatório de Tempo Total de Uso por Categoria =====");

        Map<Espaco.Categoria, Integer> tempoTotalPorCategoria = new HashMap<>();
        for (Map<Espaco, Map<LocalDateTime, Integer>> historicoPorEspaco : arquivoPessoal.getHistoricoUsoPorSocio().values()) {
            for (Map<LocalDateTime, Integer> historicoPorHorario : historicoPorEspaco.values()) {
                for (int horas : historicoPorHorario.values()) {
                    Espaco.Categoria categoria = historicoPorEspaco.keySet().iterator().next().getCategoria();
                    tempoTotalPorCategoria.merge(categoria, horas, Integer::sum);
                }
            }
        }

        for (Map.Entry<Espaco.Categoria, Integer> entry : tempoTotalPorCategoria.entrySet()) {
            Espaco.Categoria categoria = entry.getKey();
            int tempoTotal = entry.getValue();
            System.out.println(categoria.name() + ": " + tempoTotal + " horas");
        }
    }


    public void gerarRelatorioTempoUsoPorHorario() {
        System.out.println("===== Relatório de Tempo de Uso por Horário =====");

        Map<LocalTime, Integer> tempoTotalPorHorario = new HashMap<>();
        for (Map<Espaco, Map<LocalDateTime, Integer>> historicoPorEspaco : arquivoPessoal.getHistoricoUsoPorSocio().values()) {
            for (Map<LocalDateTime, Integer> historicoPorHorario : historicoPorEspaco.values()) {
                for (Map.Entry<LocalDateTime, Integer> entry : historicoPorHorario.entrySet()) {
                    LocalDateTime horario = entry.getKey();
                    int horas = entry.getValue();

                    LocalTime horaDoDia = horario.toLocalTime();
                    tempoTotalPorHorario.merge(horaDoDia, horas, Integer::sum);
                }
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (Map.Entry<LocalTime, Integer> entry : tempoTotalPorHorario.entrySet()) {
            LocalTime horario = entry.getKey();
            int tempoTotal = entry.getValue();
            System.out.println(horario.format(formatter) + ": " + tempoTotal + " horas");
        }
    }


    public void salvarRelatorioTempoTotalUsoPorSocioEmJSON(String nomeArquivo) {
        List<Map<String, Object>> relatorios = new ArrayList<>();
        Map<Socio, Integer> tempoTotalPorSocio = new HashMap<>();

        for (Socio socio : arquivoPessoal.getHistoricoUsoPorSocio().keySet()) {
            Map<Espaco, Map<LocalDateTime, Integer>> historicoPorEspacoPorSocio = arquivoPessoal.getHistoricoPorEspacoPorSocio(socio);

            int tempoTotal = 0;
            for (Map<LocalDateTime, Integer> historicoPorHorario : historicoPorEspacoPorSocio.values()) {
                for (int horas : historicoPorHorario.values()) {
                    tempoTotal += horas;
                }
            }
            tempoTotalPorSocio.put(socio, tempoTotal);
        }

        for (Map.Entry<Socio, Integer> entry : tempoTotalPorSocio.entrySet()) {
            Socio socio = entry.getKey();
            int tempoTotal = entry.getValue();

            Map<String, Object> relatorio = new HashMap<>();
            relatorio.put("socio", socio.getNome());
            relatorio.put("tempoTotalUso", tempoTotal);

            relatorios.add(relatorio);
        }

        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeToFile(nomeArquivo, relatorios);

        System.out.println("Relatório de Tempo Total de Uso por Sócio salvo com sucesso em " + nomeArquivo);
    }

    public void salvarRelatorioTempoTotalUsoPorEspacoEmJSON(String nomeArquivo) {
        List<Map<String, Object>> relatorios = new ArrayList<>();
        Map<Espaco, Integer> tempoTotalPorEspaco = new HashMap<>();

        for (Map<Espaco, Map<LocalDateTime, Integer>> historicoPorEspaco : arquivoPessoal.getHistoricoUsoPorSocio().values()) {
            for (Map<LocalDateTime, Integer> historicoPorHorario : historicoPorEspaco.values()) {
                for (int horas : historicoPorHorario.values()) {
                    Espaco espaco = historicoPorEspaco.keySet().iterator().next();
                    tempoTotalPorEspaco.merge(espaco, horas, Integer::sum);
                }
            }
        }

        for (Map.Entry<Espaco, Integer> entry : tempoTotalPorEspaco.entrySet()) {
            Espaco espaco = entry.getKey();
            int tempoTotal = entry.getValue();

            Map<String, Object> relatorio = new HashMap<>();
            relatorio.put("espaco", espaco.getNome());
            relatorio.put("categoria", espaco.getCategoria().name());
            relatorio.put("tempoTotalUso", tempoTotal);

            relatorios.add(relatorio);
        }

        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeToFile(nomeArquivo, relatorios);

        System.out.println("Relatório de Tempo Total de Uso por Espaço salvo com sucesso em " + nomeArquivo);
    }


    public void salvarRelatorioTempoTotalUsoPorCategoriaEmJSON(String nomeArquivo) {
        List<Map<String, Object>> relatorios = new ArrayList<>();
        Map<Espaco.Categoria, Integer> tempoTotalPorCategoria = new HashMap<>();

        for (Map<Espaco, Map<LocalDateTime, Integer>> historicoPorEspaco : arquivoPessoal.getHistoricoUsoPorSocio().values()) {
            for (Map<LocalDateTime, Integer> historicoPorHorario : historicoPorEspaco.values()) {
                for (int horas : historicoPorHorario.values()) {
                    Espaco.Categoria categoria = historicoPorEspaco.keySet().iterator().next().getCategoria();
                    tempoTotalPorCategoria.merge(categoria, horas, Integer::sum);
                }
            }
        }

        for (Map.Entry<Espaco.Categoria, Integer> entry : tempoTotalPorCategoria.entrySet()) {
            Espaco.Categoria categoria = entry.getKey();
            int tempoTotal = entry.getValue();

            Map<String, Object> relatorio = new HashMap<>();
            relatorio.put("categoria", categoria.name());
            relatorio.put("tempoTotalUso", tempoTotal);

            relatorios.add(relatorio);
        }

        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeToFile(nomeArquivo, relatorios);

        System.out.println("Relatório de Tempo Total de Uso por Categoria salvo com sucesso em " + nomeArquivo);
    }

}