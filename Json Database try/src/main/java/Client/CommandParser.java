package Client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class CommandParser {
    @Parameter(names = {"-v", "--value"}, description = "Value to save in database")
    private String value;
    @Parameter(names = {"-t", "--type"}, description = "Type of the request")
    private String type;
    @Parameter(names = {"-k", "--key"}, description = "Key")
    private String key;
    @Parameter(names = {"-in"}, description = "Input file", converter = FileConverter.class)
    private File in;
    private Gson gson = new Gson();

    public CommandParser(String[] args) {
        JCommander.newBuilder()
                .addObject(this)
                .build()
                .parse(args);
    }

    public String serializeToJson() {
        Map<String, String> command = new HashMap<>();
        if (in != null) {
            try (FileReader reader = new FileReader(in)) {
                return gson.fromJson(reader, JsonObject.class).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        command.put("type", type);
        command.put("key", key);
        command.put("value", value);
        return gson.toJson(command);
    }
}
