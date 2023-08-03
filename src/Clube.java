import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Clube {
    private List<Socio> socios;
    private final String ARQUIVO_SOCIOS = "socios.txt";

    public Clube() {
        this.socios = new ArrayList<>();
        carregarDoArquivo();
    }

    public void cadastrarSocio(Socio socio) {
        socios.add(socio);
        salvarNoArquivo();
        System.out.println("Sócio cadastrado com sucesso!");
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
        System.out.print("Data de associação: ");
        String dataAssociacao = scanner.nextLine();
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
            System.out.println("Sócio encontrado:");
            System.out.println(socioEncontrado);
        } else {
            System.out.println("Sócio não encontrado.");
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
            System.out.print("Nova data de associação: ");
            String novaDataAssociacao = scanner.nextLine();
            System.out.print("Novo nome: ");
            String novoNome = scanner.nextLine();
            System.out.print("Novo documento (RG ou CPF): ");
            String novoDocumento = scanner.nextLine();

            socioAtualizado = new Socio(numeroCarteirinhaAtualizar, novaDataAssociacao, novoNome, novoDocumento);
            if (atualizarRegistro(numeroCarteirinhaAtualizar, socioAtualizado)) {
                System.out.println("Sócio atualizado com sucesso!");
            } else {
                System.out.println("Falha ao atualizar o sócio.");
            }
        }
    }

    public void excluirRegistro(Scanner scanner) {
        System.out.println("===== Excluir Registro =====");
        System.out.print("Digite o número da carteirinha do sócio a ser excluído: ");
        int numeroCarteirinhaExcluir = scanner.nextInt();
        if (excluirRegistro(numeroCarteirinhaExcluir)) {
            System.out.println("Sócio excluído com sucesso!");
        } else {
            System.out.println("Sócio não encontrado ou falha ao excluir.");
        }
    }

    private void carregarDoArquivo() {
        try (Scanner scanner = new Scanner(new File(ARQUIVO_SOCIOS))) {
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                String[] campos = linha.split(";");
                int numeroCarteirinha = Integer.parseInt(campos[0]);
                String dataAssociacao = campos[1];
                String nome = campos[2];
                String documento = campos[3];

                Socio socio = new Socio(numeroCarteirinha, dataAssociacao, nome, documento);
                socios.add(socio);
            }
        } catch (IOException e) {
            // Caso ocorra algum erro na leitura do arquivo
            System.out.println("Erro ao ler o arquivo socios.txt: " + e.getMessage());
        }
    }

    private void salvarNoArquivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_SOCIOS))) {
            for (Socio socio : socios) {
                writer.println(socio.getNumeroCarteirinha() + ";" +
                        socio.getDataAssociacao() + ";" +
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
