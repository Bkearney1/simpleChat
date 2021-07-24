import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;

public class MultiServer{
    private ServerSocketChannel serverSocketChannel;
    private ServerSocket serverSocket;
    private Socket insocket;
    private int connectionPort;
    private String ipAddr;
    private ArrayList<ClientThread> al;


    public MultiServer(int incomingPort, String ip) throws IOException {
        al = new ArrayList<ClientThread>();
        this.ipAddr=ip;

        this.connectionPort=incomingPort;
        connectionPort=incomingPort;
        serverSocket = new ServerSocket(connectionPort);
    }

    public void listen(){
        while(true){
            try {

                insocket = serverSocket.accept();
                System.out.println("With client on "+ insocket.getRemoteSocketAddress());

                ClientThread t = new ClientThread(insocket);
                //add this client to arraylist
                al.add(t);
                t.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcast(){

    }

    class ClientThread extends Thread{
        private Socket socket;
        ClientThread(Socket socket) throws IOException {
            this.socket = socket;
        }
        public void run() {
            System.out.println("in the start method");
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (!socket.isClosed()) {
                    String fromUser = in.readLine();
                    if (fromUser != null) {
                        if (fromUser.contains("q_*_disconnect_*_")) {
                            System.out.println("USer left");
                            socket.close();
                            in.close();
                            al.remove(this);

                            for (ClientThread t: al){
                                System.out.println("x");
                            }
                        } else
                            System.out.println(fromUser);
                    }
                }

            }catch(Exception e ){
                e.printStackTrace();
            }finally {
                try {

                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String [] args) throws IOException {
        MultiServer server = new MultiServer( 6999, "localhost");
        server.listen();
    }
}

