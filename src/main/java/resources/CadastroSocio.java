package resources;

import java.time.LocalDate;
import java.util.Scanner;

public class CadastroSocio {
    private Clube clube;
    private Scanner scanner;

    public CadastroSocio(Clube clube, Scanner scanner) {
        this.clube = clube;
        this.scanner = scanner;
    }

    public void cadastrarNovoSocio() {
        System.out.println("===== Cadastrar Novo Sócio =====");
        try {
            // Incrementar o número da carteirinha antes de criar o novo sócio
            int numeroCarteirinha = clube.getUltimoNumeroCarteirinha() + 1;

            System.out.print("Nome: ");
            String nome = scanner.nextLine();

            // Escolhendo o tipo do documento
            System.out.println("Escolha o tipo de documento: ");
            for (Clube.TipoDocumento tipo : Clube.TipoDocumento.values()) {
                System.out.println(tipo.ordinal() + ". " + tipo.getDescricao());
            }
            int escolha = scanner.nextInt();
            scanner.nextLine();

            String documento;
            if (escolha == Clube.TipoDocumento.RG.ordinal()) {
                documento = formatarDocumento(scanner, Clube.TipoDocumento.RG);
            } else {
                documento = formatarDocumento(scanner, Clube.TipoDocumento.CPF);
            }

            Socio novoSocio = new Socio(numeroCarteirinha, LocalDate.now(), nome, documento);
            clube.cadastrarSocio(novoSocio);
        } catch (NumberFormatException e) {
            System.out.println("Erro ao ler entrada numérica: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar, consulte a numeração de seu documento.");
        }
    }

    private String formatarDocumento(Scanner scanner, Clube.TipoDocumento tipo) {
        System.out.print("Documento (" + tipo.getMascara() + "): ");
        String documento = scanner.nextLine();
        StringBuilder documentoFormatado = new StringBuilder();

        int count = 0;
        for (char c : tipo.getMascara().toCharArray()) {
            if (c == '#') {
                documentoFormatado.append(documento.charAt(count));
                count++;
            } else {
                documentoFormatado.append(c);
            }
        }

        return documentoFormatado.toString();
    }
}
