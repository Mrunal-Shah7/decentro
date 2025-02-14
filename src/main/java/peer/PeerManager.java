package peer;

// The methods of this class have been moved to the methods of the PeerDiscovery class.
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PeerManager {
    private String peerId;

    public PeerManager(String peerId){
        this.peerId = peerId;
    }

    //Check if peer is alive
    public boolean isPeerAlive(String ipAddress){
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, 8000), 3000);
            if (socket.isConnected() == true) {
                return true;
            } else {
                return false;
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
            return false;
        }

    }


}