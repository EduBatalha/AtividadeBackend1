package infrastructure;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        Map<LocalDateTime, Integer> registros = registroUsoPorSocio.computeIfAbsent(socio, k -> new LinkedHashMap<>());

        registros.put(horarioEntrada, 1); // Registro de entrada
        if (horarioSaida != null) {
            registros.put(horarioSaida, -1); // Registro de saída
        }
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
