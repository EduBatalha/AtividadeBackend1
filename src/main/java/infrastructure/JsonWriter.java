package infrastructure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import infrastructure.resources.LocalDateTimeAdapter;
import infrastructure.resources.LocalDateAdapter;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class JsonWriter {
    private Gson gson;
    private final String JSON_FILE_PATH = "registros.json";

    public JsonWriter() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting() // Formatação bonita do JSON
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS") // Formato de data e hora para serialização
                .create();
    }

    public void writeToFile(String fileName, Map<Integer, Socio> data) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("["); // Abre a lista de registros
            int i = 0;
            for (Socio socio : data.values()) {
                if (i > 0) {
                    writer.write(",");
                }
                gson.toJson(socio, writer);
                i++;
            }
            writer.write("]"); // Fecha a lista de registros
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void appendToJSONFile(String fileName, Map<String, Object> data) {
        File file = new File(fileName);
        boolean fileExists = file.exists();

        try (FileWriter writer = new FileWriter(file, true)) {
            if (!fileExists) {
                // Se o arquivo não existe, escreva o '[' para abrir o array de registros
                writer.write("[");
            } else {
                try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                    long length = raf.length();
                    if (length > 1) {
                        raf.seek(length - 1);
                        char lastChar = (char) raf.read();
                        if (lastChar == ']') {
                            // Remove a vírgula e a quebra de linha antes do ']' anterior
                            raf.setLength(length - 1);
                        }
                    }
                }
                writer.write(","); // Adiciona uma vírgula para separar os registros
            }

            // Serializa o registro e escreve no arquivo
            gson.toJson(data, writer);
            writer.flush(); // Certifique-se de que os dados são escritos no arquivo

            if (fileExists) {
                writer.write("]"); // Fecha o array de registros
            } else {writer.write("]");}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

