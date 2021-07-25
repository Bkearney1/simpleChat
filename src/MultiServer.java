import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;

public class MultiServer {
    private ServerSocket serverSocket;
    private String ipAddr;
    public ArrayList<ClientThread> clients;


    public MultiServer(int incomingPort, String ip) throws IOException {
        clients = new ArrayList<ClientThread>();
        this.ipAddr = "localhost";
        serverSocket = new ServerSocket(incomingPort);
    }

    //This method uses an infinite loop to listen for incoming connection requests
    public void listen() {
        while (true) {
            try {
                //free up the listening socket and hand off to other thread
                Socket comm_socket = serverSocket.accept();
                System.out.println("With new client on " + comm_socket.getRemoteSocketAddress());

                ClientThread t = new ClientThread(comm_socket);
                clients.add(t);

                //Start the client thread, start listening for messages from the client
                t.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Each time a user sends a message, the server calls broadcast() to relay the message to all other clients in the pool
    public void broadcast(String message) {
        for (ClientThread t : clients) {
            try {
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(t.getSocket().getOutputStream()));
                dos.writeUTF(message);
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class ClientThread extends Thread {

        private Socket socket;

        ClientThread(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return this.socket;
        }

        public void run() {
            try {
                DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                while (!socket.isClosed()) {
                    String fromUser = dis.readUTF();
                    System.out.println(fromUser);
                    if (fromUser.contains("::__QUIT")) {
                        broadcast(fromUser.split("::__")[0] + " is now leaving the chat.\nGoodbye!");
                        clients.remove(this);
                        dis.close();
                        socket.close();
                        this.stop();
                    } else {
                        broadcast(fromUser);
                    }
                    System.out.println("Client pool: " + clients.size());
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        MultiServer server = new MultiServer(7001, "localhost");
        server.listen();
    }
}

