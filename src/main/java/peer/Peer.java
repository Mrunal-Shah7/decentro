package peer;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class Peer {
    private String peerId;
    private PeerDiscovery peerDiscovery;

    public Peer(String peerId){
        this.peerId = peerId;
        this.peerDiscovery = new PeerDiscovery(peerId);
    }

    public String getPeerId(){
        return peerId;
    }

    public void start(){
        peerDiscovery.start();
    }

    public void addKnownPeer(String peerAddress){
        peerDiscovery.addPeer(peerAddress);
    }


    public static void main(String[] args) throws Exception{
        Peer myPeer = new Peer(InetAddress.getLocalHost().getHostAddress());
        System.out.println(myPeer.getPeerId());

        System.out.println(myPeer.peerDiscovery.isPeerAlive("192.168.0.133"));


    }



}
