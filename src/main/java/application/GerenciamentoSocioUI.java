package application;

import java.util.Scanner;
import domain.SocioNegocio;
import infrastructure.Socio;

public class GerenciamentoSocioUI {
    private Scanner scanner;
    private SocioNegocio socioNegocio;

    public GerenciamentoSocioUI(Scanner scanner, SocioNegocio gerenciamentoSocioNegocio) {
        this.scanner = scanner;
        this.socioNegocio = gerenciamentoSocioNegocio;
    }

    public void exibirMenuAtualizacao(Socio socio, SocioNegocio socioNegocio) {
        System.out.println("Escolha o que deseja atualizar:");
        System.out.println("1 - Nome");
        System.out.println("2 - Documento");
        System.out.println("3 - Nome e Documento");
        System.out.print("Opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1 -> socioNegocio.atualizarNome(socio);
            case 2 -> socioNegocio.atualizarDocumento(socio);
            case 3 -> {
                socioNegocio.atualizarNome(socio);
                socioNegocio.atualizarDocumento(socio);
            }
            default -> System.out.println("Opção inválida.");
        }
    }
}
