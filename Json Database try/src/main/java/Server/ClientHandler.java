package Server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Database database;
    private Gson gson = new Gson();

    public ClientHandler(Socket socket, ServerSocket serverSocket, Database database) {
        try {
            this.database = database;
            this.serverSocket = serverSocket;
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            closeEverything();
        }
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void closeEverything() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            JsonObject request = gson.fromJson(bufferedReader.readLine(), JsonObject.class);
            JsonObject response = new JsonObject();

            switch (gson.fromJson(request.get("type"), String.class)) {
                case "exit" -> {
                    response.addProperty("response", "OK");
                    sendMessage(response.toString());
                    closeEverything();
                    closeServerSocket();
                }
                case "set" -> {
                    database.set(request);
                    response.addProperty("response", "OK");
                    database.save();
                }
                case "get" -> {
                    JsonElement value = database.get(request);
                    if (value == null) {
                        response.addProperty("response", "ERROR");
                        response.addProperty("reason", "No such key");
                        break;
                    }
                    response.addProperty("response", "OK");;
                    response.add("value", value);
                }
                case "delete" -> {
                    JsonElement value = database.delete(request);
                    if (value == null) {
                        response.addProperty("response", "ERROR");
                        response.addProperty("reason", "No such key");
                        break;
                    }
                    database.save();
                    response.addProperty("response", "OK");;
                }
            }
            sendMessage(response.toString());

            closeEverything();

        } catch (IOException e) {
            closeEverything();
        }
    }
}
