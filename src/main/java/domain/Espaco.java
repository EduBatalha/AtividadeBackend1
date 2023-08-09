package domain;
import java.util.HashMap;
import java.util.Map;


public class Espaco {
    private String nome;
    private Categoria categoria;
    private int lotacaoMaxima;
    private Map<Socio, Integer> registroUsoPorSocio;

    public enum Categoria {
        ESPORTES,
        RECREACAO,
        RELAXAMENTO
    }

    public Espaco(String nome, Categoria categoria, int lotacaoMaxima) {
        this.nome = nome;
        this.categoria = categoria;
        this.lotacaoMaxima = lotacaoMaxima;
        this.registroUsoPorSocio = new HashMap<>();
    }


    public String getNome() {
        return nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public int getLotacaoMaxima() {
        return lotacaoMaxima;
    }
}
