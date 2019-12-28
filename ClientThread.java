package javamessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {

    public static ArrayList<ClientThread> listOfClients = new ArrayList<>();
    private Socket socket = null;
    private final BufferedReader clientIn;
    private final PrintWriter clientOut;

    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;

        //set new ins and outs for sending messages
        clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        clientOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        String message; //the message that is received from this client
        ClientThread friend; //the client being sent to

        //synchronize the threads
        synchronized (listOfClients) {
            listOfClients.add(this);
        }

        try {
            //receive message from the client
            while ((message = clientIn.readLine()) != null) {

                //handle case when only one client is connected
                if (listOfClients.size() < 2) {
                    System.out.println("Client: " + message);
                    this.clientOut.println("SERVER: Friend not connected.");
                    this.clientOut.flush();
                    continue;
                }

                //assign friend
                //pre-condition: only 2 clients connected
                //prints message to server console
                if (listOfClients.get(0) == this) { //first index is me
                    friend = listOfClients.get(1);
                    System.out.println("Client 1: " + message);
                } else {  //first index is friend
                    friend = listOfClients.get(0);
                    System.out.println("Client 2: " + message);
                }

                //send message here
                synchronized (listOfClients) {
                    friend.clientOut.println("Friend: " + message);
                    friend.clientOut.flush();
                }

            }
        } catch (IOException ex) {
            System.out.println(ex);;
        } finally {

            //close out this thread and remove from list of clients
            try {
                clientIn.close();
                clientOut.close();
                socket.close();
            } catch (IOException ex) {
                System.out.println(ex);
            } finally {
                synchronized (listOfClients) {
                    listOfClients.remove(this);
                }
            }

        }

    }

}

