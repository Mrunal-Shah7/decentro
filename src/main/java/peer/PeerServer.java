package peer;

import java.net.ServerSocket;
import java.net.Socket;

public class PeerServer {

    private String peerId;


    public PeerServer(String peerId){
        this.peerId = peerId;
    }

    public void start(){
        new Thread(()->{

            try {

                ServerSocket serverSocket = new ServerSocket(5165);
                System.out.println("Listening on port 5165 for receivers");

                while (true){
                    Socket socket = serverSocket.accept();
                    new Thread(new ShardReceiver(socket)).start();
                }

            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }).start();
    }






}
