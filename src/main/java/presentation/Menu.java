package presentation;

import domain.Espaco;
import domain.Financeiro;
import domain.GestaoEspacos;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    private Scanner scanner;
    private Clube clube;
    private GerenciamentoSocio gerenciamentoSocio;
    private GestaoEspacos gestaoEspacos;
    private Financeiro financeiro;
    private ArquivoPessoal arquivoPessoal;

    public Menu() {
        scanner = new Scanner(System.in);
        clube = new Clube(scanner);
        arquivoPessoal = new ArquivoPessoal();
        gestaoEspacos = clube.getGestaoEspacos();
        financeiro = new Financeiro(arquivoPessoal, gestaoEspacos, clube);
        gerenciamentoSocio = clube.getGerenciamentoSocio();
    }

    public void executar() {
        boolean sair = false;
        do{
            int escolha;

            try {
                System.out.println("===== Menu =====");
                System.out.println("1 - Cadastrar Novo Sócio");
                System.out.println("2 - Consultar Sócio");
                System.out.println("3 - Atualizar Sócio");
                System.out.println("4 - Excluir Sócio");
                System.out.println("5 - Listar sócios");
                System.out.println("6 - Exibir Opções de Relatórios");
                System.out.println("7 - Registrar Entrada e Saída de Sócio em Espaço");
                System.out.println("8 - Gerenciar Espaços");
                System.out.println("0 - Sair");
                System.out.print("Escolha uma opção: ");
                escolha = scanner.nextInt();
                scanner.nextLine();

                switch (escolha) {
                    case 1 -> gerenciamentoSocio.cadastrarNovoSocio();
                    case 2 -> gerenciamentoSocio.consultarSocio();
                    case 3 -> gerenciamentoSocio.atualizarRegistro();
                    case 4 -> gerenciamentoSocio.excluirRegistro();
                    case 5 -> clube.listarSocios();
                    case 6 -> exibirMenuRelatorios(scanner);
                    case 7 -> clube.getArquivoPessoal().exibirMenuRegistrarEntradaSaida(scanner, clube);
                    case 8 -> menuEspaco();
                    case 0 -> sair= true;
                    default -> System.out.println("Opção inválida.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Erro: Insira uma opção válida.");
                scanner.nextLine(); // Limpar a entrada inválida
            }
        }while (!sair);
        if (sair) System.out.println("Saindo...");
    }

    public void menuEspaco() {
        int escolha;

        do {
            System.out.println("===== Menu de Gestão de Espaços =====");
            System.out.println("1 - Adicionar Novo Espaço");
            System.out.println("2 - Listar Espaços");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");
            escolha = scanner.nextInt();
            scanner.nextLine(); // Consumir a quebra de linha após o nextInt()

            switch (escolha) {
                case 1:
                    adicionarNovoEspaco();
                    break;
                case 2:
                    listarEspacos();
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }

        } while (escolha != 0);
    }

    public void adicionarNovoEspaco() {
        System.out.print("Digite o nome do novo espaço: ");
        String nomeEspaco = scanner.nextLine();

        System.out.println("Escolha a categoria do espaço:");
        for (int i = 0; i < Espaco.Categoria.values().length; i++) {
            System.out.println(i + 1 + " - " + Espaco.Categoria.values()[i]);
        }
        int categoriaIndex = scanner.nextInt();
        Espaco.Categoria categoria = Espaco.Categoria.values()[categoriaIndex - 1];

        System.out.print("Digite a lotação máxima do espaço: ");
        int lotacaoMaxima = scanner.nextInt();

        Espaco novoEspaco = new Espaco(nomeEspaco, categoria, lotacaoMaxima);
        gestaoEspacos.getEspacos().add(novoEspaco); // Adicionar o novo espaço à lista espacos
        System.out.println("Novo espaço adicionado com sucesso!");

        gestaoEspacos.saveEspacosToJson(); // Salva os espaços atualizados no arquivo JSON
    }

    public void listarEspacos() {
        System.out.println("===== Lista de Espaços =====");
        for (int i = 0; i < gestaoEspacos.getEspacos().size(); i++) {
            Espaco espaco = gestaoEspacos.getEspacos().get(i);
            System.out.println("Espaço " + (i + 1));
            System.out.println("Nome: " + espaco.getNome());
            System.out.println("Categoria: " + espaco.getCategoria());
            System.out.println("Lotação Máxima: " + espaco.getLotacaoMaxima());
            System.out.println("-------------------------");
        }
    }

    public void exibirMenuRelatorios(Scanner scanner) {
        financeiro.processarRegistros(); // Processa os registros antes de exibir os relatórios

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
                case 1 -> financeiro.gerarRelatorioTempoUsoPorSocio();
                case 2 -> financeiro.gerarRelatorioTempoUsoPorEspaco();
                case 3 -> financeiro.gerarRelatorioTempoUsoPorCategoria();
                case 4 -> financeiro.gerarRelatorioTempoUsoPorHorario();
                case 5 -> System.out.println("Voltando ao Menu Principal...");
                default -> System.out.println("Opção inválida.");
            }

        } while (escolha != 5);
    }

}
