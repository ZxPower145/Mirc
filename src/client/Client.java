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
    private BufferedReader in;
    private Thread receiveThread;

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
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        receiveThread.start();
    }

    public void sendMessage(String message, User user) {
        out.println(user.getUserName() + ": " + message);
    }

    public void stopConnection() throws IOException {
        try {
            if (receiveThread != null) {
                receiveThread.interrupt();
            }
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
