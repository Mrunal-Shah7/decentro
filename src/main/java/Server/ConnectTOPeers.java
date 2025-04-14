
package Server;

import java.io.IOException;
import java.net.Socket;

public class ConnectTOPeers implements Runnable {
    @Override
    public void run() {
        try {
            // Wait for servers to start
            Thread.sleep(50000);
            for (String server : ConfigureServer.servers.keySet()) {
                String[] parts = ConfigureServer.servers.get(server).split(":");
                String ip = parts[0];
                int port = Integer.parseInt(parts[1]);
                try {
                    Socket socket = new Socket(ip, port);
                    ConfigureServer.sockets.put(server, ConfigureServer.servers.get(server));
                    System.out.println("Connected to " + server + " at " + ip + ":" + port);
                } catch (IOException e) {
                    System.out.println("Failed to connect to " + server + ": " + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}