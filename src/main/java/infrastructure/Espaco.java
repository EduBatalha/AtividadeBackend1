package infrastructure;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Espaco {
    private String nome;
    private Categoria categoria;
    private int lotacaoMaxima;
    private Map<Socio, Map<LocalDateTime, Integer>> registroUsoPorSocio;

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

    public void registrarUso(Socio socio, LocalDateTime horarioEntrada, LocalDateTime horarioSaida) {
        if (!registroUsoPorSocio.containsKey(socio)) {
            registroUsoPorSocio.put(socio, new HashMap<>());
        }

        Map<LocalDateTime, Integer> registrosSocio = registroUsoPorSocio.get(socio);

        registrosSocio.put(horarioEntrada, 1); // Entrada é representada por 1
        registrosSocio.put(horarioSaida, 0);   // Saída é representada por 0
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

    public Map<Socio, Map<LocalDateTime, Integer>> getRegistroUsoPorSocio() {
        return registroUsoPorSocio;
    }
}
