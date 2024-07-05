package client;

import utils.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    BufferedReader in; // Modified access level
    private Thread receiveThread;
    private MessageHandler messageHandler;

    public interface MessageHandler {
        void handleMessage(String message);
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    public void startConnection(String ip, int port) throws IOException {
        this.clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Start a new thread to handle incoming messages
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        if (messageHandler != null) {
                            messageHandler.handleMessage(response);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        receiveThread.start();
    }

    public void sendMessage(String message, User user) {
        String fullMessage = user.getUserName() + ": " + message;
        out.println(fullMessage);
    }

}
