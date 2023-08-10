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

            switch (escolha) { /*
                case 1 -> gerarRelatorioTempoTotalUsoPorSocio();
                case 2 -> gerarRelatorioTempoTotalUsoPorEspaco();
                case 3 -> gerarRelatorioTempoTotalUsoPorCategoria();
                case 4 -> gerarRelatorioTempoUsoPorHorario();*/
                case 5 -> System.out.println("Voltando ao Menu Principal...");
                default -> System.out.println("Opção inválida.");
            }

        } while (escolha != 5);
    }



}
