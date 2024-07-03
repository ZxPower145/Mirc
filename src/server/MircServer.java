package server;

import java.net.*;
import java.io.*;

public class MircServer {
    private static final int PORT = 2222;
    private static final String STOP_STRING = "@kill";
    private ServerSocket serverSocket;

    public void start() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
        while (true)
            new MircServerHandler(serverSocket.accept(), this.serverSocket).start();
    }

    private static class MircServerHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private ServerSocket serverSocket;

        public MircServerHandler(Socket socket, ServerSocket serverSocket) {
            this.clientSocket = socket;
            this.serverSocket = serverSocket;
        }

        public void stopServer() throws IOException {
            serverSocket.close();
        }

        public void run() {
            try {
                this.out = new PrintWriter(clientSocket.getOutputStream(), false);
                this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String message = in.readLine();

                while (!message.isEmpty()) {
                    if (STOP_STRING.equals(message)) {
                        break;
                    }
                    out.println(message);
                }
                in.close();
                out.close();
                clientSocket.close();
                stopServer();
                interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

