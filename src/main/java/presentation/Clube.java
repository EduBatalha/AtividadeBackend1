package presentation;

import data.JsonReader;
import data.JsonWriter;
import domain.*;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Clube {
    private List<Socio> socios;
    private final String ARQUIVO_SOCIOS = "socios.json";

    private int ultimoNumeroCarteirinha = 0; // Variável para armazenar o último número de carteirinha utilizado
    private Scanner scanner;
    private GerenciamentoSocio gerenciamentoSocio;
    private Financeiro financeiro;
    private GestaoEspacos gestaoEspacos;
    private ArquivoPessoal arquivoPessoal;
    public Clube(Scanner scanner) {
        this.scanner = scanner;
        this.socios = new ArrayList<>();
        carregarDoArquivo();
        atualizarUltimoNumeroCarteirinha();
        this.gerenciamentoSocio = new GerenciamentoSocio(this, scanner);
        arquivoPessoal = new ArquivoPessoal();
        this.gestaoEspacos = new GestaoEspacos();
        this.financeiro = new Financeiro(arquivoPessoal, gestaoEspacos, this);
    }

    public List<Socio> getSocios() {
        return socios;
    }

    private void atualizarUltimoNumeroCarteirinha() {
        for (Socio socio : socios) {
            if (socio.getNumeroCarteirinha() > ultimoNumeroCarteirinha) {
                ultimoNumeroCarteirinha = socio.getNumeroCarteirinha();
            }
        }
    }

    public void cadastrarSocio(Socio socio) {
        socios.add(socio);
        ultimoNumeroCarteirinha = socio.getNumeroCarteirinha();
        salvarNoArquivo();
    }

    private void carregarDoArquivo() {
        JsonReader jsonReader = new JsonReader();
        Type listType = new TypeToken<List<Socio>>(){}.getType();

        List<Socio> listaSocios = jsonReader.readFromFile(ARQUIVO_SOCIOS, listType);
        if (listaSocios != null) {
            socios = listaSocios;
        } else {
            socios = new ArrayList<>();
        }
    }

    private void salvarNoArquivo() {
        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeToFile(ARQUIVO_SOCIOS, socios);
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

    public Financeiro getRelatorios() {
        return financeiro;
    }

    public GerenciamentoSocio getGerenciamentoSocio() {
        return gerenciamentoSocio;
    }

    public GestaoEspacos getGestaoEspacos() {
        return gestaoEspacos;
    }

    public ArquivoPessoal getArquivoPessoal() {
        return arquivoPessoal;
    }

    public boolean excluirRegistro(int numeroCarteirinhaExcluir) {
        GerenciamentoSocio gerenciamentoSocio = new GerenciamentoSocio(this, scanner);
        Socio socio = gerenciamentoSocio.consultarPorCarteirinha(numeroCarteirinhaExcluir);
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
