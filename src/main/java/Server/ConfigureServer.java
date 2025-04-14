package Server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.net.httpserver.HttpServer;

public class ConfigureServer {
    public static ConcurrentHashMap<String, String> sockets = new ConcurrentHashMap<>();
    public static String ipAddress;
    public static int port;
    public static String serverId;
    public static ConcurrentHashMap<String, String> servers = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> serverNames = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Server.ConfigureServer serverX (X from 0 to 7)");
            return;
        }
        serverId = args[0];
        
        // Create shared_files directory
        Files.createDirectories(Paths.get("shared_files"));

        // Load configuration from config.xml
        String configPath = "config.xml"; // Update this path as needed
        XMLParser parser = new XMLParser(configPath);
        ipAddress = parser.getServerIP(serverId);
        port = parser.getServerPort(serverId);
        servers = parser.getServers();
        serverNames = parser.getServerNames();

        // Start server-side implementation
        ServerSideImplementation server = new ServerSideImplementation(port);
        Thread serverThread = new Thread(server);
        serverThread.start();

        // Start connecting to peers
        ConnectTOPeers connect = new ConnectTOPeers();
        Thread connectThread = new Thread(connect);
        connectThread.start();

        // Start file server
        HttpServer fileServer = HttpServer.create(new InetSocketAddress(8000 + Integer.parseInt(serverId.substring(6))), 0);
        fileServer.createContext("/files", new FileServer());
        fileServer.setExecutor(null);
        fileServer.start();
        System.out.println("File server started on port " + (8000 + Integer.parseInt(serverId.substring(6))));

        // Start client
        DHTClient client = new DHTClient();
        client.start();
    }
}