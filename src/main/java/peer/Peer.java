package peer;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class Peer {
    private String peerId;
    //private String name;
    private PeerDiscovery peerDiscovery;
    private PeerServer peerServer;
    private FileSharder fileSharder;

    public Peer(String peerId){
        this.peerId = peerId;
        this.peerDiscovery = new PeerDiscovery(peerId);
        this.peerServer = new PeerServer(peerId);
        this.fileSharder = new FileSharder(this.peerDiscovery);
    }

    public String getPeerId(){
        return peerId;
    }

    public void start(){
        peerDiscovery.start();
        peerServer.start();

    }

    public void addKnownPeer(String peerAddress){
        peerDiscovery.addPeer(peerAddress);
    }

    public void uploadFile(String filePath){
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("File not found at path:" + filePath);
            }

            List<File> shards = fileSharder.splitFile(file);
            fileSharder.distributeShards(shards);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }




    public static void main(String[] args) throws Exception{
        //Testing logic
        Peer peer1 = new Peer(InetAddress.getLocalHost().getHostAddress());
//        System.out.println(peer1.getPeerId());
    }
}
