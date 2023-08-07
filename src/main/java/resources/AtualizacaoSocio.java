package resources;

import java.util.Scanner;

public class AtualizacaoSocio {
    private Clube clube;
    private ConsultaSocio consultaSocio;
    private Scanner scanner;

    public AtualizacaoSocio(Clube clube, ConsultaSocio consultaSocio, Scanner scanner) {
        this.clube = clube;
        this.consultaSocio = consultaSocio;
        this.scanner = scanner;
    }

    public void atualizarRegistro() {
        System.out.println("===== Atualizar Registro =====");
        System.out.print("Digite o número da carteirinha do sócio a ser atualizado: ");
        int numeroCarteirinhaAtualizar = scanner.nextInt();
        scanner.nextLine();

        Socio socioAtualizado = consultaSocio.consultarPorCarteirinha(numeroCarteirinhaAtualizar);
        if (socioAtualizado == null) {
            System.out.println("Sócio não encontrado.");
        } else {
            exibirMenuAtualizacao(socioAtualizado);
        }
    }

    private void exibirMenuAtualizacao(Socio socio) {
        System.out.println("Escolha o que deseja atualizar:");
        System.out.println("1 - Nome");
        System.out.println("2 - Documento");
        System.out.println("3 - Nome e Documento");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        Socio socioAtualizado = new Socio(socio.getNumeroCarteirinha(), socio.getDataAssociacao(), socio.getNome(), socio.getDocumento());

        switch (opcao) {
            case 1:
                System.out.print("Novo nome: ");
                String novoNome = scanner.nextLine();
                socioAtualizado.setNome(novoNome);
                break;
            case 2:
                System.out.print("Novo documento (RG ou CPF): ");
                String novoDocumento = scanner.nextLine();
                socioAtualizado.setDocumento(novoDocumento);
                break;
            case 3:
                System.out.print("Novo nome: ");
                novoNome = scanner.nextLine();
                System.out.print("Novo documento (RG ou CPF): ");
                novoDocumento = scanner.nextLine();
                socioAtualizado.setNome(novoNome);
                socioAtualizado.setDocumento(novoDocumento);
                break;
            default:
                System.out.println("Opção inválida.");
                return;
        }

        if (clube.atualizarRegistro(socio, socioAtualizado)) {
            System.out.println("Sócio atualizado com sucesso!");
        } else {
            System.out.println("Falha ao atualizar o sócio.");
        }

    }
}

