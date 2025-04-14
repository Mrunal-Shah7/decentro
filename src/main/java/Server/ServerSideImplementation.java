package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSideImplementation implements Runnable {
    private int port;
    private ConcurrentHashMap<String, List<String>> hashTable;

    public ServerSideImplementation(int port) {
        this.port = port;
        this.hashTable = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("DHT Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
            
            String operation = (String) in.readObject();
            String key = (String) in.readObject();

            switch (operation) {
                case "PUT":
                    String value = (String) in.readObject();
                    hashTable.compute(key, (k, v) -> {
                        List<String> list = (v == null) ? new ArrayList<>() : v;
                        if (!list.contains(value)) list.add(value);
                        return list;
                    });
                    out.writeObject("PUT successful");
                    break;

                case "GET":
                    List<String> valueList = hashTable.getOrDefault(key, new ArrayList<>());
                    out.writeObject(valueList);
                    break;

                case "DELETE":
                    hashTable.remove(key);
                    out.writeObject("DELETE successful");
                    break;

                default:
                    out.writeObject("Unknown operation");
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}