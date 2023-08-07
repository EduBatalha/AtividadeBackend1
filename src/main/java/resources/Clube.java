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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Clube {
    private List<Socio> socios;
    private final String ARQUIVO_SOCIOS = "socios.json";
    private Gson gson;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private int ultimoNumeroCarteirinha = 0; // Variável para armazenar o último número de carteirinha utilizado
    private Scanner scanner;
    private ConsultaSocio consultaSocio;

    public Clube(Scanner scanner) {
        this.scanner = scanner;
        this.socios = new ArrayList<>();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .disableInnerClassSerialization()
                .setPrettyPrinting()
                .create();
        carregarDoArquivo();
        atualizarUltimoNumeroCarteirinha();
        this.consultaSocio = new ConsultaSocio(this, scanner);
    }

    public List<Socio> getSocios() {
        return socios;
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

    public void carregarDoArquivo() {
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

    public void salvarNoArquivo() {
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

    public boolean excluirRegistro(int numeroCarteirinhaExcluir) {
        ConsultaSocio consultaSocio = new ConsultaSocio(this, scanner);
        Socio socio = consultaSocio.consultarPorCarteirinha(numeroCarteirinhaExcluir);
        if (socio != null) {
            socios.remove(socio);
            salvarNoArquivo();
            return true;
        } else {
            return false;
        }
    }

    public boolean atualizarRegistro(Socio socioAntigo, Socio socioAtualizado) {
        for (int i = 0; i < socios.size(); i++) {
            Socio socio = socios.get(i);
            if (socio.getNumeroCarteirinha() == socioAntigo.getNumeroCarteirinha()) {
                socios.set(i, socioAtualizado);
                salvarNoArquivo();
                return true;
            }
        }
        return false;
    }


    public int getUltimoNumeroCarteirinha() {
        return ultimoNumeroCarteirinha;
    }

    // Métodos de gerenciamento das outras classes

    public void cadastrarNovoSocio() {
        new CadastroSocio(this, scanner).cadastrarNovoSocio();
    }

    public void consultarSocio() {
        new ConsultaSocio(this, scanner).consultarSocio();
    }

    public void atualizarRegistro() {
        ConsultaSocio consultaSocio = new ConsultaSocio(this, scanner);
        new AtualizacaoSocio(this, consultaSocio, scanner).atualizarRegistro();
    }

    public void excluirRegistro() {
        new ExclusaoSocio(this, scanner).excluirRegistro();
    }
}

