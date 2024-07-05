package client;

import utils.User;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    BufferedReader in;
    private Thread receiveThread;
    private MessageHandler messageHandler;
    private JTextArea textArea;

    public interface MessageHandler {
        void handleMessage(String message);
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void startConnection(String ip, int port) throws IOException {
        this.clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        if (messageHandler != null) {
//                            if (response.equals("HTTP_STATUS_200")) {
//                                clearMessages(textArea);
//                            } else {
                                messageHandler.handleMessage(response);
//                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        receiveThread.start();
    }

    public void clearMessages(JTextArea textArea) {
        textArea.setText(null);
    }

    public void sendMessage(String message, User user) {
        String fullMessage = user.getUserName() + ": " + message;
        out.println(fullMessage);
    }
}
