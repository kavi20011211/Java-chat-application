import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main (String[] args){
        try{
            Socket socket = new Socket(SERVER_ADDRESS,SERVER_PORT);
            System.out.println("Connected to the chat Server!");

            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(()->{
                try{
                    String serverResponse;
                    while ((serverResponse = in.readLine())!=null){
                        System.out.println(serverResponse);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }).start();

            Scanner scanner = new Scanner(System.in);
            String userInput;
            while(true){
                userInput = scanner.nextLine();
                out.println(userInput);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
