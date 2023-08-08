package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import resources.LocalDateAdapter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class JsonWriter {
    private Gson gson;

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
}
