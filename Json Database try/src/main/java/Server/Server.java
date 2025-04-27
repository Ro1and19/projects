package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {

        System.out.println("Server started");
        try {
            Database database = new Database(new File("db.json"));

            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, serverSocket, database);

                Thread thread = new Thread(clientHandler);
                thread.start();

            }

        } catch (Exception e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}