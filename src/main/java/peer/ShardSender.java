package peer;

import java.io.*;
import java.net.Socket;

public class ShardSender {

    public static void sendShard(String peerAddress , File shard){
        try {
            Socket socket = new Socket(peerAddress,5165);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            FileInputStream fileInputStream = new FileInputStream(shard);

            dataOutputStream.writeUTF(shard.getName());
            dataOutputStream.writeLong(shard.length());


            byte[] buffer = new byte[4096];
            int bytesRead;
            while((bytesRead=fileInputStream.read(buffer))!=-1){
                dataOutputStream.write(buffer,0,bytesRead);
            }

            System.out.println("Sent shard: "+shard.getName()+"to"+peerAddress+":5165");
            socket.close();
            System.out.println("Socket closed:"+socket.isClosed());

        }catch (Exception e){
            System.out.println(e.getMessage());

        }

    }

}
