import java.util.Scanner;
import resources.Clube;

public class Main {
    public static void main(String[] args) {
        Clube clube = new Clube();
        Scanner scanner = new Scanner(System.in);

        int opcao;

        do {
            exibirMenu();
            opcao = lerOpcao(scanner);

            switch (opcao) {
                case 1 -> clube.cadastrarNovoSocio(scanner);
                case 2 -> clube.consultarSocio(scanner);
                case 3 -> clube.atualizarRegistro(scanner);
                case 4 -> clube.excluirRegistro(scanner);
                case 5 -> clube.listarSocios();
                case 6 -> System.out.println("Saindo do sistema...");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 6);
    }

    private static void exibirMenu() {
        System.out.println("===== Menu =====");
        System.out.println("1. Cadastrar novo sócio");
        System.out.println("2. Consultar sócio");
        System.out.println("3. Atualizar registro por número de carteirinha");
        System.out.println("4. Excluir registro por número de carteirinha");
        System.out.println("5. Listar registros");
        System.out.println("6. Sair");
    }

    private static int lerOpcao(Scanner scanner) {
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Para limpar o buffer de entrada
        return opcao;
    }
}
