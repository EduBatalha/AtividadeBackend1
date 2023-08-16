package application;

import infrastructure.Espaco;
import infrastructure.JsonReader;
import infrastructure.JsonWriter;
import domain.*;

import com.google.gson.reflect.TypeToken;
import infrastructure.Socio;

import java.lang.reflect.Type;
import java.util.*;

public class Clube {
    private Map<Integer, Socio> socios;  // Usando HashMap para mapear número da carteirinha para Socio
    private final String ARQUIVO_SOCIOS = "socios.json";
    private int ultimoNumeroCarteirinha = 0;
    private Scanner scanner;
    private GerenciamentoSocio gerenciamentoSocio;
    private Financeiro financeiro;
    private GestaoEspacos gestaoEspacos;
    private ArquivoPessoal arquivoPessoal;

    public Clube(Scanner scanner) {
        this.scanner = scanner;
        carregarDoArquivo();
        atualizarUltimoNumeroCarteirinha();
        this.gerenciamentoSocio = new GerenciamentoSocio(this, scanner);
        arquivoPessoal = new ArquivoPessoal();
        this.gestaoEspacos = new GestaoEspacos();
        this.financeiro = new Financeiro(arquivoPessoal, gestaoEspacos, this);
    }

    public Map<Integer, Socio> getSocios() {
        return socios;
    }

    private void atualizarUltimoNumeroCarteirinha() {
        for (Socio socio : socios.values()) {
            if (socio.getNumeroCarteirinha() > ultimoNumeroCarteirinha) {
                ultimoNumeroCarteirinha = socio.getNumeroCarteirinha();
            }
        }
    }

    public void cadastrarSocio(Socio socio) {
        socios.put(socio.getNumeroCarteirinha(), socio);
        ultimoNumeroCarteirinha = socio.getNumeroCarteirinha();
        salvarNoArquivo();
    }

    private void carregarDoArquivo() {
        JsonReader jsonReader = new JsonReader();
        Type listType = new TypeToken<List<Socio>>(){}.getType(); // Usar o tipo List<Socio>

        List<Socio> listaSocios = jsonReader.readFromFile(ARQUIVO_SOCIOS, listType);

        // Converter a lista de socios em um mapa
        socios = new HashMap<>();
        if (listaSocios != null) {
            for (Socio socio : listaSocios) {
                socios.put(socio.getNumeroCarteirinha(), socio);
            }
        }
    }

    private void salvarNoArquivo() {
        Map<Integer, Socio> socioMap = new HashMap<>();
        for (Socio socio : socios.values()) {
            socioMap.put(socio.getNumeroCarteirinha(), socio);
        }

        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeToFile(ARQUIVO_SOCIOS, (Map<Integer, Socio>) socioMap);
    }

    public void listarSocios() {
        if (socios.isEmpty()) {
            System.out.println("Não há registros de sócios.");
        } else {
            System.out.println("===== Lista de Sócios =====");
            socios.values().forEach(System.out::println);
        }
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
        Integer numeroCarteirinha = socioAntigo.getNumeroCarteirinha();
        if (socios.containsKey(numeroCarteirinha)) {
            socios.put(numeroCarteirinha, socioAtualizado);
            salvarNoArquivo();
            return true;
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
