package presentation;

import domain.GestaoEspacos;
import domain.Relatorios;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    private Scanner scanner;
    private Clube clube;
    private Relatorios relatorios;
    private GerenciamentoSocio gerenciamentoSocio;
    private GestaoEspacos gestaoEspacos;

    public Menu() {
        scanner = new Scanner(System.in);
        clube = new Clube(scanner);
        relatorios = new Relatorios(clube.getFinanceiro(), clube.getArquivoPessoal(), clube.getGestaoEspacos());
        gerenciamentoSocio = clube.getGerenciamentoSocio();
        gestaoEspacos = clube.getGestaoEspacos();
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
                    case 6 -> relatorios.exibirMenuRelatorios(scanner);
                    case 7 -> clube.getArquivoPessoal().exibirMenuRegistrarEntradaSaida(scanner, clube);
                    case 8 -> gestaoEspacos.menuEspaco();
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
}
