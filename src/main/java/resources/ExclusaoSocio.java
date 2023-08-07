package resources;

import java.util.Scanner;

public class ExclusaoSocio {
    private Clube clube;
    private Scanner scanner;

    public ExclusaoSocio(Clube clube, Scanner scanner) {
        this.clube = clube;
        this.scanner = scanner;
    }

    public boolean excluirRegistro() {
        System.out.println("===== Excluir Registro =====");
        System.out.print("Digite o número da carteirinha do sócio a ser excluído: ");
        int numeroCarteirinhaExcluir = scanner.nextInt();
        scanner.nextLine();

        ConsultaSocio consultaSocio = new ConsultaSocio(clube, scanner);
        Socio socioExcluir = consultaSocio.consultarPorCarteirinha(numeroCarteirinhaExcluir);

        if (socioExcluir != null) {
            clube.excluirRegistro(socioExcluir.getNumeroCarteirinha());
            System.out.println("Cadastro excluído com sucesso!");
            return true;
        } else {
            System.out.println("Cadastro não encontrado ou falha ao excluir.");
            return false;
        }
    }
}

