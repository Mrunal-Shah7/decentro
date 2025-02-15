package peer;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class Peer {
    private String peerId; //IP address or UID
//  private List<String> knownPeers; // List of known peers in the network
    private PeerDiscovery peerDiscovery; // To discover peers
//    private PeerManager peerManager; //For inter peer comms , and shard distro (Moved to PeerDiscovery )

    public Peer(String peerId){ //Default Constructor

        this.peerId = peerId;
        this.peerDiscovery = new PeerDiscovery(peerId);
//        this.peerManager = new PeerManager(peerId);
    }

    public String getPeerId(){
        return peerId;
    }

    public void start(){ //To start discovery and communication
        peerDiscovery.start();
      //  peerManager.start();
    }

    public void addKnownPeer(String peerAddress){
        peerDiscovery.addPeer(peerAddress);
    }


    public static void main(String[] args) throws Exception{
        Peer myPeer = new Peer(InetAddress.getLocalHost().getHostAddress());
        System.out.println(myPeer.getPeerId());
    }



}
