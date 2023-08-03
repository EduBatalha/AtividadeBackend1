import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Clube {
    private List<Socio> socios;
    private final String ARQUIVO_SOCIOS = "socios.txt";

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Clube() {
        this.socios = new ArrayList<>();
        carregarDoArquivo();
    }

    public void cadastrarSocio(Socio socio) {
        socios.add(socio);
        salvarNoArquivo();
        System.out.println("Cadastro efetuado com sucesso!");
    }

    public Socio consultarPorDocumento(String documento) {
        for (Socio socio : socios) {
            if (socio.getDocumento().equals(documento)) {
                return socio;
            }
        }
        return null;
    }

    public Socio consultarPorCarteirinha(int numeroCarteirinha) {
        for (Socio socio : socios) {
            if (socio.getNumeroCarteirinha() == numeroCarteirinha) {
                return socio;
            }
        }
        return null;
    }

    public Socio consultarPorNome(String nome) {
        for (Socio socio : socios) {
            if (socio.getNome().equalsIgnoreCase(nome)) {
                return socio;
            }
        }
        return null;
    }

    public boolean atualizarRegistro(int numeroCarteirinha, Socio socioAtualizado) {
        for (int i = 0; i < socios.size(); i++) {
            Socio socio = socios.get(i);
            if (socio.getNumeroCarteirinha() == numeroCarteirinha) {
                socios.set(i, socioAtualizado);
                salvarNoArquivo();
                return true;
            }
        }
        return false;
    }

    public boolean excluirRegistro(int numeroCarteirinha) {
        for (int i = 0; i < socios.size(); i++) {
            Socio socio = socios.get(i);
            if (socio.getNumeroCarteirinha() == numeroCarteirinha) {
                socios.remove(i);
                salvarNoArquivo();
                return true;
            }
        }
        return false;
    }

    public void cadastrarNovoSocio(Scanner scanner) {
        System.out.println("===== Cadastrar Novo Sócio =====");
        System.out.print("Número da carteirinha: ");
        int numeroCarteirinha = scanner.nextInt();
        scanner.nextLine();
        LocalDate dataAssociacao = LocalDate.now(); // Utiliza a data do sistema

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Documento (RG ou CPF): ");
        String documento = scanner.nextLine();
        Socio novoSocio = new Socio(numeroCarteirinha, dataAssociacao, nome, documento);
        cadastrarSocio(novoSocio);
    }

    public void consultarSocio(Scanner scanner) {
        System.out.println("===== Consultar Sócio =====");
        System.out.print("Digite o documento, nome ou número da carteirinha do sócio: ");
        String busca = scanner.nextLine();

        Socio socioEncontrado = null;

        if (Character.isDigit(busca.charAt(0))) {
            int numeroCarteirinhaConsulta = Integer.parseInt(busca);
            socioEncontrado = consultarPorCarteirinha(numeroCarteirinhaConsulta);
        } else {
            socioEncontrado = consultarPorDocumento(busca);
            if (socioEncontrado == null) {
                socioEncontrado = consultarPorNome(busca);
            }
        }

        if (socioEncontrado != null) {
            System.out.println("Cadastro encontrado:");
            System.out.println(socioEncontrado);
        } else {
            System.out.println("Cadastro não encontrado.");
        }
    }

    public void atualizarRegistro(Scanner scanner) {
        System.out.println("===== Atualizar Registro =====");
        System.out.print("Digite o número da carteirinha do sócio a ser atualizado: ");
        int numeroCarteirinhaAtualizar = scanner.nextInt();
        scanner.nextLine();
        Socio socioAtualizado = consultarPorCarteirinha(numeroCarteirinhaAtualizar);
        if (socioAtualizado == null) {
            System.out.println("Sócio não encontrado.");
        } else {
            exibirMenuAtualizacao(scanner, socioAtualizado);
        }
    }

    private void exibirMenuAtualizacao(Scanner scanner, Socio socio) {
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

        if (atualizarRegistro(socio.getNumeroCarteirinha(), socioAtualizado)) {
            System.out.println("Sócio atualizado com sucesso!");
        } else {
            System.out.println("Falha ao atualizar o sócio.");
        }
    }


    public void excluirRegistro(Scanner scanner) {
        System.out.println("===== Excluir Registro =====");
        System.out.print("Digite o número da carteirinha do sócio a ser excluído: ");
        int numeroCarteirinhaExcluir = scanner.nextInt();
        if (excluirRegistro(numeroCarteirinhaExcluir)) {
            System.out.println("Cadastro excluído com sucesso!");
        } else {
            System.out.println("Cadastro não encontrado ou falha ao excluir.");
        }
    }

    private void carregarDoArquivo() {
        try (Scanner scanner = new Scanner(new File(ARQUIVO_SOCIOS))) {
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                String[] campos = linha.split(";");
                int numeroCarteirinha = Integer.parseInt(campos[0]);
                LocalDate dataAssociacao;
                try {
                    dataAssociacao = LocalDate.parse(campos[1], dateFormatter);
                } catch (Exception e) {
                    System.out.println("Erro ao ler a data do arquivo socios.txt: " + e.getMessage());
                    continue;
                }
                String nome = campos[2];
                String documento = campos[3];

                Socio socio = new Socio(numeroCarteirinha, dataAssociacao, nome, documento);
                socios.add(socio);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo socios.txt: " + e.getMessage());
        }
    }

    private void salvarNoArquivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_SOCIOS))) {
            for (Socio socio : socios) {
                String dataAssociacao = socio.getDataAssociacao().format(dateFormatter);
                writer.println(socio.getNumeroCarteirinha() + ";" +
                        dataAssociacao + ";" +
                        socio.getNome() + ";" +
                        socio.getDocumento());
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados no arquivo: " + e.getMessage());
        }
    }

    public void listarSocios() {
        if (socios.isEmpty()) {
            System.out.println("Não há registros de sócios.");
        } else {
            System.out.println("===== Lista de Sócios =====");
            for (Socio socio : socios) {
                System.out.println(socio);
            }
        }
    }
}
