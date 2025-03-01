package peer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class ShardReceiver implements Runnable{
    private Socket socket;

    public ShardReceiver(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            String fileName = dataInputStream.readUTF();

            long fileSize = dataInputStream.readLong();

            File shardDir = new File("received_shards");
            if(!shardDir.exists()){
                shardDir.mkdirs();
            }


            File shardFile = new File(shardDir,fileName);



            FileOutputStream fileOutputStream = new FileOutputStream(shardFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalRead = 0;

            while (totalRead<fileSize && (bytesRead = dataInputStream.read(buffer))!=-1){
                fileOutputStream.write(buffer,0,bytesRead);
                totalRead+= bytesRead;
            }

            System.out.println("Received shard:"+fileName);


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}
