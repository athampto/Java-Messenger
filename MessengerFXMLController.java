package javamessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MessengerFXMLController implements Initializable {

    @FXML
    private TextArea messageArea;
    @FXML
    private TextField textInput;
    @FXML
    private Button sendButton;

    PrintWriter toServer;
    BufferedReader fromServer;
    Socket serverSocket;
    serverThread sThread;
    String msgToServer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //create the socket and set up the I/O systems
        try {
            serverSocket = new Socket("localhost", 8000);
            fromServer = new BufferedReader(new InputStreamReader(System.in));
            toServer = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
        } catch (IOException ex) {
            System.out.println(ex);;
        }

        //start the thread to handle incoming messages
        sThread = new serverThread(serverSocket);
        sThread.start();

    }

    @FXML
    private void sendMessage(ActionEvent event) {

        //pull from text field here
        msgToServer = textInput.getText().trim();

        //reset text in text field
        textInput.setText("");

        //append the requisite message to chat window
        messageArea.appendText("You: " + msgToServer + "\n");

        //send to server
        toServer.println(msgToServer);
        toServer.flush();

    }

    //this class will serve as a thread
    //to handle incoming messages from the server
    class serverThread extends Thread {

        Socket socket;
        BufferedReader fromServer = null;
        String msgFromServer = null;

        serverThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try {
                fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //receive messages from the server
                while ((msgFromServer = fromServer.readLine()) != null) {

                    //append to text area
                    messageArea.appendText(msgFromServer + "\n");
                }

                //close out once finished
                fromServer.close();
                socket.close();
                System.exit(0);

            } catch (IOException ex) {
                System.out.println(ex);
            }

        }

    }

}
