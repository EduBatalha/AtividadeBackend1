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

    private List<Espaco> Espacos() {
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

    public void menuEspaco() {
        int escolha;

        do {
            System.out.println("===== Menu de Gestão de Espaços =====");
            System.out.println("1 - Adicionar Novo Espaço");
            System.out.println("2 - Listar Espaços");
            System.out.println("0 - Voltar");
            System.out.print("Escolha uma opção: ");
            escolha = scanner.nextInt();
            scanner.nextLine(); // Consumir a quebra de linha após o nextInt()

            switch (escolha) {
                case 1:
                    adicionarNovoEspaco();
                    break;
                case 2:
                    listarEspacos();
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }

        } while (escolha != 0);
    }

    public void adicionarNovoEspaco() {
        System.out.print("Digite o nome do novo espaço: ");
        String nomeEspaco = scanner.nextLine();

        System.out.println("Escolha a categoria do espaço:");
        for (int i = 0; i < Espaco.Categoria.values().length; i++) {
            System.out.println(i + 1 + " - " + Espaco.Categoria.values()[i]);
        }
        int categoriaIndex = scanner.nextInt();
        Espaco.Categoria categoria = Espaco.Categoria.values()[categoriaIndex - 1];

        System.out.print("Digite a lotação máxima do espaço: ");
        int lotacaoMaxima = scanner.nextInt();

        Espaco novoEspaco = new Espaco(nomeEspaco, categoria, lotacaoMaxima);
        espacos.add(novoEspaco); // Adicionar o novo espaço à lista espacos
        System.out.println("Novo espaço adicionado com sucesso!");

        saveEspacosToJson(); // Salva os espaços atualizados no arquivo JSON
    }

    private void saveEspacosToJson() {
        jsonWriter.writeToFile("espacos.json", espacos); // Escreve os espaços no arquivo JSON
    }

    public void listarEspacos() {
        System.out.println("===== Lista de Espaços =====");
        for (int i = 0; i < espacos.size(); i++) {
            Espaco espaco = espacos.get(i);
            System.out.println("Espaço " + (i + 1));
            System.out.println("Nome: " + espaco.getNome());
            System.out.println("Categoria: " + espaco.getCategoria());
            System.out.println("Lotação Máxima: " + espaco.getLotacaoMaxima());
            System.out.println("-------------------------");
        }
    }
}
