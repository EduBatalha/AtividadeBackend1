package application;

import infrastructure.Socio;

import java.time.LocalDate;
import java.util.Scanner;

public class GerenciamentoSocio {
    private Clube clube;
    private Scanner scanner;

    public GerenciamentoSocio(Clube clube, Scanner scanner) {
        this.clube = clube;
        this.scanner = scanner;
    }

    public void cadastrarNovoSocio() {
        System.out.println("===== Cadastrar Novo Sócio =====");

        try {
            // Incrementar o número da carteirinha antes de criar o novo sócio
            int numeroCarteirinha = clube.getUltimoNumeroCarteirinha() + 1;

            System.out.print("Nome: ");
            String nome = scanner.nextLine().trim();

            if (nome.isEmpty() || nome.length() < 3) {
                System.out.println("Erro: O nome deve conter pelo menos 3 letras.");
                return;
            }

            if (!nome.matches("^[A-Za-zÀ-ÖØ-öø-ÿ ]+$")) {
                System.out.println("Erro: Utilize apenas letras e espaços no nome.");
                return;
            }

            // Escolhendo o tipo do documento
            System.out.println("Escolha o tipo de documento: ");
            for (Clube.TipoDocumento tipo : Clube.TipoDocumento.values()) {
                System.out.println(tipo.ordinal() + ". " + tipo.getDescricao());
            }
            int escolha = scanner.nextInt();
            scanner.nextLine();

            String documento;
            if (escolha == Clube.TipoDocumento.RG.ordinal()) {
                documento = formatarDocumento(scanner, Clube.TipoDocumento.RG.getMascara());
            } else if (escolha == Clube.TipoDocumento.CPF.ordinal()) {
                documento = formatarDocumento(scanner, Clube.TipoDocumento.CPF.getMascara());
            } else {
                System.out.println("Erro: Escolha inválida de tipo de documento.");
                return;
            }

            if (documento == null) {
                return; // O método formatarDocumento já exibiu os erros
            }

            Socio novoSocio = new Socio(numeroCarteirinha, LocalDate.now(), nome, documento);
            clube.cadastrarSocio(novoSocio);
            System.out.println("Cadastro efetuado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Erro ao ler entrada numérica: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar, consulte a numeração de seu documento.");
        }
    }

    private String formatarDocumento(Scanner scanner, String mascara) {
        System.out.print("Documento (" + mascara + "): ");
        String input = scanner.nextLine().trim();

        // Remove tudo exceto os números do input
        String documentoNumerico = input.replaceAll("[^0-9]", "");

        if (documentoNumerico.length() != mascara.replaceAll("[^#]", "").length()) {
            System.out.println("Erro: Registro incorreto, verifique sua documentação.");
            return null;
        }

        StringBuilder documentoFormatado = new StringBuilder();
        int count = 0;
        for (char c : mascara.toCharArray()) {
            if (c == '#') {
                if (count < documentoNumerico.length()) {
                    documentoFormatado.append(documentoNumerico.charAt(count));
                    count++;
                }
            } else {
                documentoFormatado.append(c);
            }
        }

        return documentoFormatado.toString();
    }

    public void atualizarRegistro() {
        System.out.println("===== Atualizar Registro =====");
        System.out.print("Digite o número da carteirinha do sócio a ser atualizado: ");
        int numeroCarteirinhaAtualizar = scanner.nextInt();
        scanner.nextLine();

        Socio socioAtualizar = null;

        for (Socio socio : clube.getSocios()) {
            if (socio.getNumeroCarteirinha() == numeroCarteirinhaAtualizar) {
                socioAtualizar = socio;
                break;
            }
        }

        if (socioAtualizar == null) {
            System.out.println("Sócio não encontrado.");
        } else {
            exibirMenuAtualizacao(socioAtualizar);
        }
    }

    public void consultarSocio() {
        System.out.println("===== Consultar Sócio =====");
        System.out.print("Digite o número da carteirinha do sócio: ");
        int numeroCarteirinhaConsulta = scanner.nextInt();
        scanner.nextLine();

        Socio socioEncontrado = consultarPorCarteirinha(numeroCarteirinhaConsulta);

        if (socioEncontrado != null) {
            System.out.println("Cadastro encontrado:");
            System.out.println(socioEncontrado);
        } else {
            System.out.println("Cadastro não encontrado.");
        }
    }


    public Socio consultarPorCarteirinha(int numeroCarteirinha) {
        for (Socio socio : clube.getSocios()) {
            if (socio.getNumeroCarteirinha() == numeroCarteirinha) {
                return socio;
            }
        }
        return null;
    }

    public boolean excluirRegistro() {
        System.out.println("===== Excluir Registro =====");
        System.out.print("Digite o número da carteirinha do sócio a ser excluído: ");
        int numeroCarteirinhaExcluir = scanner.nextInt();
        scanner.nextLine();

        Socio socioExcluir = consultarPorCarteirinha(numeroCarteirinhaExcluir);

        if (socioExcluir != null) {
            clube.excluirRegistro(socioExcluir.getNumeroCarteirinha());
            System.out.println("Cadastro excluído com sucesso!");
            return true;
        } else {
            System.out.println("Cadastro não encontrado ou falha ao excluir.");
            return false;
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
