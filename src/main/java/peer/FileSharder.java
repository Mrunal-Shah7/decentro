package peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileSharder {
    private PeerDiscovery peerDiscovery;

    public FileSharder(PeerDiscovery peerDiscovery){
        this.peerDiscovery = peerDiscovery;
    }



    public  List<File> splitFile(File file) throws IOException{
        List<File> shards = new ArrayList<>();
        int numShards = peerDiscovery.getActivePeers().size();

        if(numShards == 0){
            System.out.println("No active peers in the network");
        }

        long fileSize = file.length();
        long shardSize = fileSize/numShards;

            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[(int)shardSize];
            int partCounter = 1;
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1){
                File shard = new File("shards/"+file.getName()+".part"+partCounter++);
                    FileOutputStream fileOutputStream = new FileOutputStream(shard);
                    fileOutputStream.write(buffer,0,bytesRead);
                    shards.add(shard);
            }
            return shards;
    }

    public void distributeShards(List<File> shards){
        List<String> peers = new ArrayList<>(peerDiscovery.getActivePeers());

        if(peers.size()<2){
            System.out.println("Not enough peers for redudancy bruh");
            return;
        }

        Random random = new Random();
        for(File shard:shards){
            int index1 = random.nextInt(peers.size());
            String peer1 = peers.get(index1);
            int index2 = random.nextInt(peers.size());
            while (true){
                if(index1!=index2){
                    break;
                }
            }
            String peer2 = peers.get(index2);
            System.out.println("Shard:"+shard.getName() + "--> Peer 1:"+peer1+" Peer 2: "+peer2);
        }



    }



}
