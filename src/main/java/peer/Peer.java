package peer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class Peer {
    private String peerId; //IP address or UID
//  private List<String> knownPeers; // List of known peers in the network
    private PeerDiscovery peerDiscovery; // To discover peers
//    private PeerManager peerManager; //For inter peer comn , and shard distro

    public Peer(){ //Default Constructor
        try {
            this.peerId = InetAddress.getLocalHost().getHostAddress();
        }catch (UnknownHostException e){
            System.out.println("Unknown host exception");
        }
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
        //Peer myPeer = new Peer();
        //System.out.println(myPeer.getPeerId());

    }



}
