package Client;

import com.google.gson.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            System.out.println("Sent:");
            gson.toJson(gson.fromJson(message, JsonObject.class), System.out);
            System.out.println();
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void receiveMessage() {
        try {
            String message = bufferedReader.readLine();
            System.out.println("Received: ");
            gson.toJson(gson.fromJson(message, JsonObject.class), System.out);
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Client started!");
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket);

        String command = new CommandParser(args).serializeToJson();
        client.sendMessage(command);
        client.receiveMessage();
        client.closeEverything();
    }
}
