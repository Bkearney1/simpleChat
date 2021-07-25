import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;

public class MultiServer{
    private ServerSocketChannel serverSocketChannel;
    private ServerSocket serverSocket;
    private Socket insocket;
    private int connectionPort;
    private String ipAddr;
    public ArrayList<ClientThread> clients;


    public MultiServer(int incomingPort, String ip) throws IOException {
        clients = new ArrayList<ClientThread>();
        this.ipAddr=ip;
        this.connectionPort=incomingPort;
        serverSocket = new ServerSocket(connectionPort);
    }

    public void listen(){
        while(true){
            try {

                insocket = serverSocket.accept();
                System.out.println("With client on "+ insocket.getRemoteSocketAddress());

                ClientThread t = new ClientThread(insocket);
                clients.add(t);
                //add this client to arraylist
                t.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcast(String message){
        System.out.println("Client pool: "+clients.size() );
        for (ClientThread t : clients){
            try{
                DataOutputStream dos =  new DataOutputStream(new BufferedOutputStream(t.getSocket().getOutputStream()));
                dos.writeUTF(message);
                dos.flush();
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    class ClientThread extends Thread{


        //al.add(this);
        private Socket socket;
        ClientThread(Socket socket)  {
            this.socket = socket;
        }
        public Socket getSocket(){
            return this.socket;
        }
        public void run() {
            try {
                DataInputStream dis  = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                while (!socket.isClosed()) {
                    String fromUser = dis.readUTF();
                    System.out.println(fromUser);
                    broadcast(fromUser);

                    //dos.writeUTF(fromUser);
                    //dos.flush();
                   /* if (fromUser!=null){
                        if (fromUser.contains("q_*_disconnect_*_")) {
                            System.out.println("User left");
                            socket.close();
                            dis.close();
                            al.remove(this);

                        } else

                            dos.writeBytes(fromUser+"\n");
                            dos.flush();

                        //broadcast(fromUser);

                    }*/
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
        MultiServer server = new MultiServer( 7001, "localhost");
        server.listen();
    }
}

