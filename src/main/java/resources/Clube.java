package resources;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Clube {
    private List<Socio> socios;
    private final String ARQUIVO_SOCIOS = "socios.json";
    private Gson gson;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private int ultimoNumeroCarteirinha = 0; // Variável para armazenar o último número de carteirinha utilizado

    public Clube() {
        this.socios = new ArrayList<>();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .disableInnerClassSerialization()
                .setPrettyPrinting()
                .create();
        carregarDoArquivo();
        atualizarUltimoNumeroCarteirinha();
    }

    public Socio consultarPorDocumento(String documento) {
        for (Socio socio : socios) {
            if (socio.getDocumento().equals(documento)) {
                return socio;
            }
        }
        return null;
    }

    public enum TipoDocumento {
        RG("RG", "##.###.###-#"),
        CPF("CPF", "###.###.###-##");

        private final String descricao;
        private final String mascara;

        TipoDocumento(String descricao, String mascara) {
            this.descricao = descricao;
            this.mascara = mascara;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getMascara() {
            return mascara;
        }
    }

    private String formatarDocumento(Scanner scanner, TipoDocumento tipo) {
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

    private void atualizarUltimoNumeroCarteirinha() {
        // Encontrar o maior número de carteirinha já registrado
        for (Socio socio : socios) {
            if (socio.getNumeroCarteirinha() > ultimoNumeroCarteirinha) {
                ultimoNumeroCarteirinha = socio.getNumeroCarteirinha();
            }
        }
    }

    public void cadastrarSocio(Socio socio) {
        socios.add(socio);
        // Atualizar o número da última carteirinha utilizada
        ultimoNumeroCarteirinha = socio.getNumeroCarteirinha();
        salvarNoArquivo();
        System.out.println("Cadastro efetuado com sucesso!");
    }

    public void cadastrarNovoSocio(Scanner scanner) {
        System.out.println("===== Cadastrar Novo Sócio =====");
        try {
            // Incrementar o número da carteirinha antes de criar o novo sócio
            int numeroCarteirinha = ultimoNumeroCarteirinha + 1;

            System.out.print("Nome: ");
            String nome = scanner.nextLine();

            // Escolhendo o tipo do documento
            System.out.println("Escolha o tipo de documento: ");
            for (TipoDocumento tipo : TipoDocumento.values()) {
                System.out.println(tipo.ordinal() + ". " + tipo.getDescricao());
            }
            int escolha = scanner.nextInt();
            scanner.nextLine();

            String documento;
            if (escolha == TipoDocumento.RG.ordinal()) {
                documento = formatarDocumento(scanner, TipoDocumento.RG);
            } else {
                documento = formatarDocumento(scanner, TipoDocumento.CPF);
            }

            Socio novoSocio = new Socio(numeroCarteirinha, LocalDate.now(), nome, documento);
            cadastrarSocio(novoSocio);
        } catch (NumberFormatException e) {
            System.out.println("Erro ao ler entrada numérica: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("Erro ao ler data de associação: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar, consulte a numeração de seu documento.");
        }
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
        try (FileReader reader = new FileReader(ARQUIVO_SOCIOS)) {
            Type listType = new TypeToken<List<Socio>>(){}.getType();
            List<Socio> listaSocios = gson.fromJson(reader, listType);
            if (listaSocios != null) {
                socios = listaSocios;
            } else {
                socios = new ArrayList<>();
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo socios.json: " + e.getMessage());
        }
    }

    private void salvarNoArquivo() {
        try (FileWriter writer = new FileWriter(ARQUIVO_SOCIOS)) {
            gson.toJson(socios, writer);
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

    public int getTotalSocios() {
        return socios.size();
    }
}
