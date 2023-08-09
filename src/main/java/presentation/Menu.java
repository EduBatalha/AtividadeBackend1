package presentation;

import domain.Relatorios;

import java.util.Scanner;

public class Menu {
    private Scanner scanner;
    private Clube clube;
    private Relatorios relatorios;
    private GerenciamentoSocio gerenciamentoSocio;

    public Menu() {
        scanner = new Scanner(System.in);
        clube = new Clube(scanner);
        relatorios = new Relatorios(clube.getFinanceiro(), clube.getArquivoPessoal(), clube.getGestaoEspacos());
        gerenciamentoSocio = clube.getGerenciamentoSocio();
    }

    public void executar() {
        int escolha;

        do {
            System.out.println("===== Menu =====");
            System.out.println("1 - Cadastrar Novo Sócio");
            System.out.println("2 - Consultar Sócio");
            System.out.println("3 - Atualizar Sócio");
            System.out.println("4 - Excluir Sócio");
            System.out.println("5 - Listar sócios");
            System.out.println("6 - Exibir Opções de Relatórios");
            System.out.println("7 - Registrar Entrada e Saída de Sócio em Espaço");
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
                case 6 -> relatorios.exibirMenuRelatorios(scanner);
                case 7 -> clube.getArquivoPessoal().exibirMenuRegistrarEntradaSaida(scanner, clube);
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida.");
            }

        } while (escolha != 0);

        scanner.close();
    }
}
