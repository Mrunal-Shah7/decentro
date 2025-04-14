package Server;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DHTClient {
    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("DHT Client started. Commands: put <file_path>, get <file_id>, delete <file_id>, exit");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "put":
                        if (parts.length < 2) {
                            System.out.println("Usage: put <file_path>");
                            continue;
                        }
                        uploadFile(parts[1]);
                        break;
                    case "get":
                        if (parts.length < 2) {
                            System.out.println("Usage: get <file_id>");
                            continue;
                        }
                        downloadFile(parts[1]);
                        break;
                    case "delete":
                        if (parts.length < 2) {
                            System.out.println("Usage: delete <file_id>");
                            continue;
                        }
                        deleteFile(parts[1]);
                        break;
                    case "exit":
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Unknown command. Use: put, get, delete, exit");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private String computeFileId(String filePath) throws Exception {
        String fileName = Paths.get(filePath).getFileName().toString();
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(fileName.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void uploadFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("File not found: " + filePath);
        }

        // Copy file to shared_files
        String fileId = computeFileId(filePath);
        Files.copy(Paths.get(filePath), Paths.get("shared_files", fileId), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // Register in DHT (store on three peers for replication)
        String peerAddress = ConfigureServer.ipAddress + ":" + (8000 + Integer.parseInt(ConfigureServer.serverId.substring(6)));
        for (int i = 0; i < 3; i++) {
            int peerIndex = (myHashFunction(fileId) + i) % 8;
            String targetPeer = ConfigureServer.servers.get("server" + peerIndex);
            String[] peerParts = targetPeer.split(":");
            try (Socket socket = new Socket(peerParts[0], Integer.parseInt(peerParts[1]))) {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject("PUT");
                out.writeObject(fileId);
                out.writeObject(peerAddress);
                out.flush();

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                String response = (String) in.readObject();
                System.out.println("PUT response from server" + peerIndex + ": " + response);
            }
        }
        System.out.println("File uploaded with ID: " + fileId);
    }

    private void downloadFile(String fileId) throws Exception {
        // Query DHT (try up to three peers for replication)
        List<String> peerAddresses = null;
        for (int i = 0; i < 3; i++) {
            int peerIndex = (myHashFunction(fileId) + i) % 8;
            String targetPeer = ConfigureServer.servers.get("server" + peerIndex);
            String[] peerParts = targetPeer.split(":");
            try (Socket socket = new Socket(peerParts[0], Integer.parseInt(peerParts[1]))) {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject("GET");
                out.writeObject(fileId);
                out.flush();

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Object response = in.readObject();
                if (response instanceof List) {
                    peerAddresses = (List<String>) response;
                    if (!peerAddresses.isEmpty()) break;
                }
            }
        }

        if (peerAddresses == null || peerAddresses.isEmpty()) {
            throw new Exception("File not found in DHT: " + fileId);
        }

        // Download from first available peer
        for (String peerAddress : peerAddresses) {
            try {
                URL url = new URL("http://" + peerAddress + "/files/" + fileId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    Files.copy(conn.getInputStream(), Paths.get("shared_files", fileId + "_downloaded"), 
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File downloaded to shared_files/" + fileId + "_downloaded");
                    return;
                }
            } catch (IOException e) {
                System.out.println("Failed to download from " + peerAddress + ": " + e.getMessage());
            }
        }
        throw new Exception("Could not download file from any peer");
    }

    private void deleteFile(String fileId) throws Exception {
        // Delete from DHT (try all three replicas)
        for (int i = 0; i < 3; i++) {
            int peerIndex = (myHashFunction(fileId) + i) % 8;
            String targetPeer = ConfigureServer.servers.get("server" + peerIndex);
            String[] peerParts = targetPeer.split(":");
            try (Socket socket = new Socket(peerParts[0], Integer.parseInt(peerParts[1]))) {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject("DELETE");
                out.writeObject(fileId);
                out.flush();

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                String response = (String) in.readObject();
                System.out.println("DELETE response from server" + peerIndex + ": " + response);
            }
        }
        System.out.println("File mapping deleted from DHT: " + fileId);
    }

    private int myHashFunction(String key) {
        return Math.abs(key.hashCode() % 8);
    }
}