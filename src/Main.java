import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Clube clube = new Clube();
        Scanner scanner = new Scanner(System.in);

        int opcao;

        do {
            System.out.println("===== Menu =====");
            System.out.println("1. Cadastrar novo sócio");
            System.out.println("2. Consultar por documento ou nome");
            System.out.println("3. Atualizar registro por número de carteirinha");
            System.out.println("4. Excluir registro por número de carteirinha");
            System.out.println("5. Listar registros");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1 -> {
                    System.out.println("===== Cadastrar Novo Sócio =====");
                    System.out.print("Número da carteirinha: ");
                    int numeroCarteirinha = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Data de associação: ");
                    String dataAssociacao = scanner.nextLine();
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("Documento (RG ou CPF): ");
                    String documento = scanner.nextLine();
                    Socio novoSocio = new Socio(numeroCarteirinha, dataAssociacao, nome, documento);
                    clube.cadastrarSocio(novoSocio);
                }
                case 2 -> {
                    System.out.println("===== Consultar Sócio =====");
                    System.out.print("Digite o documento ou nome do sócio: ");
                    scanner.nextLine();
                    String busca = scanner.nextLine();
                    Socio socioEncontrado = clube.consultarPorDocumento(busca);
                    if (socioEncontrado == null) {
                        socioEncontrado = clube.consultarPorNome(busca);
                    }
                    if (socioEncontrado != null) {
                        System.out.println("Sócio encontrado:");
                        System.out.println(socioEncontrado);
                    } else {
                        System.out.println("Sócio não encontrado.");
                    }
                }
                case 3 -> {
                    System.out.println("===== Atualizar Registro =====");
                    System.out.print("Digite o número da carteirinha do sócio a ser atualizado: ");
                    int numeroCarteirinhaAtualizar = scanner.nextInt();
                    scanner.nextLine();
                    Socio socioAtualizado = clube.consultarPorNumeroCarteirinha(numeroCarteirinhaAtualizar);
                    if (socioAtualizado == null) {
                        System.out.println("Sócio não encontrado.");
                    } else {
                        System.out.print("Nova data de associação: ");
                        String novaDataAssociacao = scanner.nextLine();
                        System.out.print("Novo nome: ");
                        String novoNome = scanner.nextLine();
                        System.out.print("Novo documento (RG ou CPF): ");
                        String novoDocumento = scanner.nextLine();

                        socioAtualizado = new Socio(numeroCarteirinhaAtualizar, novaDataAssociacao, novoNome, novoDocumento);
                        if (clube.atualizarRegistro(numeroCarteirinhaAtualizar, socioAtualizado)) {
                            System.out.println("Sócio atualizado com sucesso!");
                        } else {
                            System.out.println("Falha ao atualizar o sócio.");
                        }
                    }
                }
                case 4 -> {
                    System.out.println("===== Excluir Registro =====");
                    System.out.print("Digite o número da carteirinha do sócio a ser excluído: ");
                    int numeroCarteirinhaExcluir = scanner.nextInt();
                    if (clube.excluirRegistro(numeroCarteirinhaExcluir)) {
                        System.out.println("Sócio excluído com sucesso!");
                    } else {
                        System.out.println("Sócio não encontrado ou falha ao excluir.");
                    }
                }
                case 5 -> clube.listarSocios();
                case 6 -> System.out.println("Saindo do sistema...");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 6);
    }
}
