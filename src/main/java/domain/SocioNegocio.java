package domain;

import com.google.gson.reflect.TypeToken;
import infrastructure.*;
import application.*;


import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class SocioNegocio {

    private GerenciamentoSocioUI socioUI;
    private Scanner scanner;
    private Socio socio;
    private SocioNegocio socioNegocio;
    private JsonReader jsonReader;
    private JsonWriter jsonWriter;
    private Map<Integer, Socio> socios;
    private Map<Socio, Map<Espaco, Map<LocalDateTime, Integer>>> historicoUsoPorSocio;
    private static final String JSON_FILENAME = "socios.json";

    public SocioNegocio(Scanner scanner, GerenciamentoSocioUI socioUI) {
        this.scanner = scanner;
        this.socioUI = socioUI;
        this.historicoUsoPorSocio = new HashMap<>();
        this.socios = new HashMap<>();
        this.jsonReader = new JsonReader();
        this.jsonWriter = new JsonWriter();

        if (!jsonReader.fileExists(JSON_FILENAME)) {
            // Se o arquivo não existir, cria um arquivo vazio
            File file = new File(JSON_FILENAME);
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println("Erro ao criar o arquivo socios.json: " + e.getMessage());
            }
        }
        if (jsonReader.fileExists(JSON_FILENAME)) {
            carregarSociosDeArquivoJSON(JSON_FILENAME);
        }
    }

    public void salvarMudancasEmArquivo() {
        jsonWriter.writeToFile(JSON_FILENAME, socios);
    }

    public void carregarSociosDeArquivoJSON(String filename) {
        List<Socio> sociosLidos = jsonReader.readFromFile(filename, new TypeToken<List<Socio>>() {}.getType());

        if (sociosLidos != null) {
            for (Socio socio : sociosLidos) {
                socios.put(socio.getNumeroCarteirinha(), socio);
            }
        }
    }

    public Map<Integer, Socio> getSocios() {
        return socios;
    }

    public void cadastrarNovoSocio() {
        System.out.println("===== Cadastrar Novo Sócio =====");

        try {
            // Incrementar o número da carteirinha antes de criar o novo sócio
            int numeroCarteirinha = getUltimoNumeroCarteirinha() + 1;

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
            for (SocioNegocio.TipoDocumento tipo : SocioNegocio.TipoDocumento.values()) {
                System.out.println(tipo.ordinal() + ". " + tipo.getDescricao());
            }
            int escolha = scanner.nextInt();
            scanner.nextLine();

            String documento;
            if (escolha == SocioNegocio.TipoDocumento.RG.ordinal()) {
                documento = formatarDocumento(scanner, SocioNegocio.TipoDocumento.RG.getMascara());
            } else if (escolha == SocioNegocio.TipoDocumento.CPF.ordinal()) {
                documento = formatarDocumento(scanner, SocioNegocio.TipoDocumento.CPF.getMascara());
            } else {
                System.out.println("Erro: Escolha inválida de tipo de documento.");
                return;
            }

            if (documento == null) {
                return; // O método formatarDocumento já exibiu os erros
            }

            // Verificar se o documento já está cadastrado
            for (Socio existingSocio : socios.values()) {
                if (existingSocio.getDocumento().equals(documento)) {
                    System.out.println("Erro: Este documento já está cadastrado.");
                    return;
                }
            }

            Socio novoSocio = new Socio(numeroCarteirinha, LocalDate.now(), nome, documento);
            socios.put(numeroCarteirinha, novoSocio);  // Adicionar o novo sócio ao mapa

            // Salvar as mudanças no arquivo JSON
            salvarMudancasEmArquivo();

            System.out.println("Cadastro efetuado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Erro ao ler entrada numérica: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar, consulte a numeração de seu documento.");
        }
    }

    public String formatarDocumento(Scanner scanner, String mascara) {
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
        return socios.get(numeroCarteirinha);
    }

    public void listarSocios() {
        if (socios.isEmpty()) {
            System.out.println("Não há registros de sócios.");
        } else {
            System.out.println("===== Lista de Sócios =====");
            socios.values().forEach(socio -> {
                System.out.println("Número da carteirinha: " + socio.getNumeroCarteirinha());
                System.out.println("Nome: " + socio.getNome());
                System.out.println("Data de Associação: " + socio.getDataAssociacao());
                System.out.println("Documento: " + socio.getDocumento());
                System.out.println();
            });
        }
    }

    private int getUltimoNumeroCarteirinha() {
        int ultimoNumeroCarteirinha = 0;
        for (Socio socio : socios.values()) {
            if (socio.getNumeroCarteirinha() > ultimoNumeroCarteirinha) {
                ultimoNumeroCarteirinha = socio.getNumeroCarteirinha();
            }
        }
        return ultimoNumeroCarteirinha;
    }

    public boolean excluirRegistro() {
        System.out.println("===== Excluir Registro =====");
        System.out.print("Digite o número da carteirinha do sócio a ser excluído: ");
        int numeroCarteirinhaExcluir = scanner.nextInt();
        scanner.nextLine();

        Socio socioExcluir = consultarPorCarteirinha(numeroCarteirinhaExcluir);

        if (socioExcluir != null) {
            socios.remove(numeroCarteirinhaExcluir); // Remove o sócio da lista
            salvarMudancasEmArquivo();
            System.out.println("Cadastro excluído com sucesso!");
            return true;
        } else {
            System.out.println("Cadastro não encontrado ou falha ao excluir.");
            return false;
        }
    }

    public void atualizarRegistro() {
        System.out.println("===== Atualizar Registro =====");
        System.out.print("Digite o número da carteirinha do sócio a ser atualizado: ");
        int numeroCarteirinhaAtualizar = scanner.nextInt();
        scanner.nextLine();

        Socio socioAtualizar = consultarPorCarteirinha(numeroCarteirinhaAtualizar);

        if (socioAtualizar == null) {
            System.out.println("Sócio não encontrado.");
        } else {
            socioUI.exibirMenuAtualizacao(socioAtualizar, this); // Passa a referência da classe SocioNegocio
        }
    }

    public void atualizarNome(Socio socio) {
        boolean nomeAtualizado = false;

        while (!nomeAtualizado) {
            System.out.print("Novo nome: ");
            String novoNome = scanner.nextLine().trim();

            if (novoNome.matches("^[A-Za-zÀ-ÖØ-öø-ÿ ]+$")) {
                socio.setNome(novoNome);
                nomeAtualizado = true;
                salvarMudancasEmArquivo();
            } else {
                System.out.println("Erro: Utilize apenas letras e espaços no nome.");
            }
        }
    }

    public void atualizarDocumento(Socio socio) {
        boolean documentoAtualizado = false;
        while (!documentoAtualizado) {
            System.out.println("Escolha o tipo de documento:");
            System.out.println("1 - RG");
            System.out.println("2 - CPF");
            System.out.print("Opção: ");
            int escolhaDocumento;
            try {
                escolhaDocumento = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Insira uma opção válida.");
                continue;
            }

            String novoDocumento = null;
            if (escolhaDocumento == 1) {
                novoDocumento = formatarDocumento(scanner, TipoDocumento.RG.getMascara());
            } else if (escolhaDocumento == 2) {
                novoDocumento = formatarDocumento(scanner, TipoDocumento.CPF.getMascara());
            } else {
                System.out.println("Opção inválida.");
                continue;
            }

            if (novoDocumento != null) {
                socio.setDocumento(novoDocumento);
                documentoAtualizado = true;
                salvarMudancasEmArquivo();
            } else {
                System.out.println("Erro ao atualizar documento. Verifique a formatação e tente novamente.");
            }
        }
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
}
