package peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class PeerDiscovery{
    private String peerId;
    private Set<String> activePeers = new HashSet<>();


    public PeerDiscovery(String peerId){
        this.peerId = peerId;
    }


    public synchronized void addPeer(String peerAddress){
        if(!peerAddress.equals(peerId)){
            activePeers.add(peerAddress);
            System.out.println("Peer with address "+peerAddress+" added to the network");
        }
    }



    public synchronized void removePeer(String peerAddress){
        activePeers.remove(peerAddress);
        System.out.println("Peer with address "+peerAddress+" disconnected from the network");
    }



    private boolean isPeerAlive(String ipAdress){
        try{
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAdress,5165),3000);
            if(socket.isConnected()){return true;}
            else{return false;}
        }catch (IOException e){
            System.out.println(e.getMessage());
            return false;
        }
    }


    public void start(){
        new Thread(()->{
            int i =1;
            while (true){
                System.out.println("Value of i:"+i);
                i++;
                for(String peer: new HashSet<>(activePeers)){
                    if(!isPeerAlive(peer)){
                        removePeer(peer);
                    }
                }
                try {
                    Thread.sleep(5000);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }

            }
        }).start();
    }

    public Set<String> getActivePeers(){
        return activePeers;
    }
}