import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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

            DataOutputStream dos = new DataOutputStream((socket.getOutputStream()));

            while(!socket.isClosed()){
                System.out.print(">");
                String user_in =   keyboard.nextLine();
                if(!user_in.equalsIgnoreCase("Q")) {
                    dos.write(("["+username+"]: " + user_in +"\n").getBytes());
                    dos.flush();
                }
                if(user_in.equalsIgnoreCase("Q")){
                    dos.write(("["+username+"]: " + user_in +"_*_disconnect_*_\n").getBytes());
                    dos.close();
                    socket.close();
                    System.out.println("Quitting");
                }
            }


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String [] args){
        new Client(6999,"localhost");

    }

}
