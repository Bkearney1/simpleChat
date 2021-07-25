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

    public Client(int port, String ip){

        keyboard = new Scanner(System.in);
        System.out.print("Please input username: ");
        this.username=keyboard.nextLine();
        try {
            socket = new Socket(ip, port);
            readThread t = new readThread(socket);
            t.start();


            //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream((socket.getOutputStream()));

            while(true){

                try{
                    System.out.print(">");
                    String user_in =  keyboard.nextLine();
                    dos.writeUTF("["+username+"]: " + user_in);
                    //sleep(1);
                    dos.flush();


                }catch (Exception e){
                    e.printStackTrace();
                }



                ///if(!user_in.equalsIgnoreCase("Q")) {
               ///     dos.write(( +"\n").getBytes());
               ///     dos.flush();
               /// }

               /* if(user_in.equalsIgnoreCase("Q")){
                    dos.write(("["+username+"]: " + user_in +"_*_disconnect_*_\n").getBytes());
                    dos.close();
                    socket.close();
                    System.out.println("Quitting");
                }*/
            }


        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String [] args){
        new Client(7001,"localhost");

    }

    public class readThread extends Thread{
        private Socket socket;
        private DataInputStream dis;
        public readThread(Socket _socket) throws IOException {
            this.socket = _socket;
            dis = new DataInputStream(socket.getInputStream());

        }
        public void run() {
            while(true) {
                try {
                    System.out.println("\r"+this.dis.readUTF()+"\r");
                    System.out.print(">");
                    //dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
