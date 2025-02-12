package peer;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Sharding {

    public static List<File> sharding(File file , int chunkSize) throws IOException{


            List<File> shards = new ArrayList<>();
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[chunkSize];
            int bytesRead, partCounter = 1;

            while ((bytesRead=bufferedInputStream.read(buffer))>0){
                File chunk = new File("src/main/java/shards/"+file.getName()+".moham"+partCounter++);
                FileOutputStream fileOutputStream = new FileOutputStream(chunk);
                fileOutputStream.write(buffer,0,bytesRead);
                shards.add(chunk);
            }
            return shards;

    }

    public static void main(String[] args) {
        try {
            sharding(new File("src/main/java/files/IMG_1134.MOV"),1024000);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }


}
