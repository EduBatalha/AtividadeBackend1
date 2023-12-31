package infrastructure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import infrastructure.resources.LocalDateAdapter;
import infrastructure.resources.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class JsonReader {
    private Gson gson;

    public JsonReader() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public <T> List<T> readFromFile(String fileName, Type type) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean fileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }
}
