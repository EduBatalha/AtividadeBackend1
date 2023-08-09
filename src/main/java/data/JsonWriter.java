package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import resources.LocalDateAdapter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class JsonWriter {
    private Gson gson;
    private final String JSON_FILE_PATH = "registros.json";

    public JsonWriter() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    public void writeToFile(String fileName, Object data) {
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendToJSONFile(String fileName, Map<String, Object> data) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            gson.toJson(data, writer);
            writer.append(System.lineSeparator()); // Adicionar uma nova linha após cada registro
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
