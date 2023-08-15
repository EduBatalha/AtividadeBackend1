package domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import com.google.gson.reflect.TypeToken;

import data.JsonReader;
import data.JsonWriter;

public class GestaoEspacos {
    private List<Espaco> espacos;
    private Scanner scanner;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    public GestaoEspacos() {
        espacos = new ArrayList<>();
        espacos.addAll(Espacos()); // Adicione os espaços iniciais
        scanner = new Scanner(System.in);
        jsonReader = new JsonReader(); // Primeiro inicialize o jsonReader
        jsonWriter = new JsonWriter();
        List<Espaco> espacosFromJson = jsonReader.readFromFile(JSON_FILENAME, new TypeToken<List<Espaco>>() {}.getType());
        if (espacosFromJson != null) {
            espacos.addAll(espacosFromJson);
        }
    }

    private static final String JSON_FILENAME = "espacos.json";

    public List<Espaco> getEspacos() {
        return espacos;
    }

    public List<Espaco> Espacos() {
        return Arrays.asList(
                new Espaco("Quadra de Futebol Indoor", Espaco.Categoria.ESPORTES, 10),
                new Espaco("Quadra de Vôlei de Praia", Espaco.Categoria.ESPORTES, 8),
                new Espaco("Quadra de Beach Tennis", Espaco.Categoria.ESPORTES, 6),
                new Espaco("Campo de Golfe A", Espaco.Categoria.ESPORTES, 18),
                new Espaco("Campo de Golfe B", Espaco.Categoria.ESPORTES, 18),
                new Espaco("Piscina Olímpica A", Espaco.Categoria.RECREACAO, 500),
                new Espaco("Piscina Olímpica B", Espaco.Categoria.RECREACAO, 500),
                new Espaco("Lago com Pedalinhos", Espaco.Categoria.RECREACAO, 20),
                new Espaco("Jardim Botânico", Espaco.Categoria.RELAXAMENTO, 100),
                new Espaco("Academia", Espaco.Categoria.ESPORTES, 50),
                new Espaco("Spá", Espaco.Categoria.RELAXAMENTO, 15),
                new Espaco("Área para Churrasco", Espaco.Categoria.RECREACAO, 30),
                new Espaco("Parque Infantil", Espaco.Categoria.RECREACAO, 50)
        );
    }

    public void saveEspacosToJson() {
        jsonWriter.writeToFile("espacos.json", espacos); // Escreve os espaços no arquivo JSON
    }
}
