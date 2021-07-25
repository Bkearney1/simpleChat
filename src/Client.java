import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Client {
    int port;
    private String ip;
    private Socket socket;
    private String username;
    Scanner keyboard;

    public Client(int port, String ip) {

        keyboard = new Scanner(System.in);
        System.out.print("Please input username: ");
        this.username = keyboard.nextLine();
        try {
            socket = new Socket(ip, port);
            readThread t = new readThread(socket);
            t.start();


            //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream((socket.getOutputStream()));

            while (!socket.isClosed()) {

                try {
                    System.out.print(">");
                    String user_in = keyboard.nextLine();

                    if (user_in.equalsIgnoreCase("q")) {
                        System.out.println("quitting");
                        user_in = username + "::__QUIT";
                        dos.writeUTF(user_in);
                        dos.flush();
                        t.stop();
                        socket.close();

                    } else {
                        dos.writeUTF("[" + username + "]: " + user_in);
                        //sleep(1);
                        dos.flush();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Client(7001, "localhost");

    }

    public class readThread extends Thread {
        private DataInputStream dis;

        public readThread(Socket _socket) throws IOException {
            dis = new DataInputStream(_socket.getInputStream());

        }


        public void run() {
            while (!socket.isClosed()) {
                try {
                    System.out.println("\r" + this.dis.readUTF() + "\r");
                    System.out.print(">");
                    //dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
