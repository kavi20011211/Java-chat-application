import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final int PORT = 5000;
    private static CopyOnWriteArrayList<ClientHandler>clients = new CopyOnWriteArrayList<ClientHandler>();

    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is running on port "+PORT);

        while (true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected "+clientSocket);

            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clients.add(clientHandler);
            new Thread(clientHandler).start();

        }
    }

    // Broadcast a message to all clients except the sender
    public static void broadcast(String message, ClientHandler sender)
    {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    private static class ClientHandler implements Runnable{
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket){
            this.clientSocket = socket;

            try{
                out = new PrintWriter(clientSocket.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            }catch (IOException e){
                e.printStackTrace();
            }
        }

        private String getUsername()throws IOException{
          out.println("Enter username:");
          return in.readLine();
        };

        public void sendMessage(String message){
            out.println(message);
            out.println("Type your message");
        }

        @Override
        public void run() {
            try{
                String username = getUsername();
                System.out.println("User "+username+" connected.");

                out.println("Welcome to the chat app, "+username+"!");
                out.println("Type your message");
                String inputLine;

                while((inputLine = in.readLine())!=null){
                    System.out.println("["+username+"]: "+inputLine);
                    broadcast("["+username+"]:" + inputLine,this);
                }

                clients.remove(this);

                in.close();
                out.close();
                clientSocket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
