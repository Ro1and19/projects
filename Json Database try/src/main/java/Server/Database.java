package Server;

import com.google.gson.*;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class Database {
    private JsonObject database;
    private Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

    public Database(File database) {
        try (FileReader reader = new FileReader(database)) {
            this.database = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(JsonObject jsonObject) {

        Object key = jsonObject.get("key");
        if (key instanceof JsonArray keyArr) {

            if (keyArr.size() == 1) {
                database.add(gson.fromJson(keyArr.get(0), String.class), jsonObject.get("value"));
                return;
            }

            Deque<JsonObject> objects = new ArrayDeque<>();
            for (int i = keyArr.size()-1; i > 0; i--) {

                if (i == keyArr.size()-1) {

                    JsonObject object = new JsonObject();
                    object.add(gson.fromJson(keyArr.get(i), String.class), jsonObject.get("value"));
                    objects.push(object);
                } else {

                    JsonObject object = new JsonObject();
                    object.add(gson.fromJson(keyArr.get(i), String.class), objects.pop());
                    objects.push(object);
                }
            }

            database.add(gson.fromJson(keyArr.get(0), String.class), objects.pop());

        } else if (key instanceof JsonPrimitive keyObj){
            database.add(gson.fromJson(keyObj, String.class), jsonObject.get("value"));
        }

    }

    public JsonElement get(JsonObject jsonObject) {

        Object key = jsonObject.get("key");
        if (key instanceof JsonArray keyArr) {

            JsonObject object = database.getAsJsonObject(gson.fromJson(keyArr.get(0), String.class));
            if (keyArr.size() == 1) {
                return object;
            }

            for (int i = 1; i < keyArr.size()-1; i++) {
                object = object.getAsJsonObject(gson.fromJson(keyArr.get(i), String.class));
            }

            return object.get(gson.fromJson(keyArr.get(keyArr.size()-1), String.class));

        } else if (key instanceof JsonPrimitive keyObj){
            return database.get(gson.fromJson(keyObj, String.class));
        }

        return null;
    }

    public JsonElement delete(JsonObject jsonObject) {

        Object key = jsonObject.get("key");
        if (key instanceof JsonArray keyArr) {

            JsonObject object = database.getAsJsonObject(gson.fromJson(keyArr.get(0), String.class));
            if (keyArr.size() == 1) {
                return database.remove(gson.fromJson(keyArr.get(0), String.class));
            }

            for (int i = 1; i < keyArr.size()-1; i++) {
                object = object.getAsJsonObject(gson.fromJson(keyArr.get(i), String.class));
            }

            return object.remove(gson.fromJson(keyArr.get(keyArr.size()-1), String.class));

        } else if (key instanceof JsonPrimitive keyObj){
            return database.remove(gson.fromJson(keyObj, String.class));
        }

        return null;
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter("db.json")) {
            gson.toJson(database, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
