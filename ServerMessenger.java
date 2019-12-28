package javamessenger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMessenger {

    public static void main(String[] args) {
        Socket clientSocket = null;
        ServerSocket listenSocket = null;

        //open the listening socket for connections
        try {
            listenSocket = new ServerSocket(8000);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        
        System.out.println("Server has started.");

        //loop through infinitely accepting new clients
        while (true) {
            try {
                clientSocket = listenSocket.accept();

                //create and start new thread for each client
                ClientThread clientThread = new ClientThread(clientSocket);
                clientThread.start();

            } catch (IOException ex) {
                System.out.println(ex);
            }
        }//end server while

    }
}
