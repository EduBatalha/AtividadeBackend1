package domain;

import java.util.*;
import com.google.gson.reflect.TypeToken;
import infrastructure.Espaco;
import infrastructure.JsonReader;
import infrastructure.JsonWriter;

public class GestaoEspacos {
    private List<Espaco> espacos;
    private Scanner scanner;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private static final String JSON_FILENAME = "espacos.json";

    public GestaoEspacos() {
        espacos = new ArrayList<>();
        espacos.addAll(Espacos()); // Adicione os espaços iniciais
        scanner = new Scanner(System.in);
        jsonReader = new JsonReader();
        jsonWriter = new JsonWriter();
        loadEspacosFromJson();
    }

    public List<Espaco> getEspacos() {
        return espacos;
    }

    public void saveEspacosToJson() {
        try {
            jsonWriter.writeEspacosToFile(JSON_FILENAME, (List<Espaco>) espacos);
            System.out.println("Espaços salvos com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao salvar espaços: " + e.getMessage());
        }
    }

    private void loadEspacosFromJson() {
        try {
            espacos = jsonReader.readFromFile(JSON_FILENAME, new TypeToken<List<Espaco>>() {}.getType());
            if (espacos == null) {
                System.out.println("O arquivo espacos.json não foi encontrado. Criando um novo arquivo...");
                espacos = Espacos(); // Adicione espaços iniciais
                saveEspacosToJson(); // Salve os espaços iniciais no arquivo
            }
        } catch (Exception ignored) {
        }
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
}
