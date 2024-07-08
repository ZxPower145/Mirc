package server;

import utils.*;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 2222;
    private static final String STOP_STRING = "@kill";
    private static final String CREATE_CHANNEL_COMMAND = "/create";
    private static final String CONNECT_TO_CHANNEL = "/connect";
    private static final String DISCONNECT_FROM_CHANNEL = "/disconnect";
    private ServerSocket serverSocket;
    private static final ConcurrentHashMap<Channel, List<ClientHandler>> channels = new ConcurrentHashMap<>();
    private static final Channel global = new Channel("global", "global");

    public void start() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        channels.putIfAbsent(global, Collections.synchronizedList(new ArrayList<>()));
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress());
            new ClientHandler(clientSocket).start();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private User currentUser;
        private Channel currentChannel = global;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void createChannel(User user, String name, String password) {
            for (Channel channel : channels.keySet()) {
                if (channel.getName().equals(name)){
                    out.println("Channel already exists!");
                    return;
                }
            }
            Channel channel = new Channel(name, password);
            channels.putIfAbsent(channel, Collections.synchronizedList(new ArrayList<>()));
            connectToChannel(user, channel, true);
            out.println("Channel created: " + name);
        }

        public void connectToChannel(User user, Channel channel, boolean status) {
            List<ClientHandler> handlers = channels.get(channel);
            if (handlers != null) {
                synchronized (handlers) {
                    if (!handlers.contains(this)){
                        handlers.add(this);
                        channels.put(channel, handlers);
                        if (currentChannel != null && !currentChannel.equals(channel))
                            disconnectFromChannel(user, currentChannel);
                        this.currentChannel = channel;
                        out.println("Connected to " + channel.getName());
                        if (status) {
                            out.println("HTTP_STATUS_200");
                        }
                        broadcastMessage(user.getUserName() + " has joined the channel " + channel.getName(), channel);
                    }
                }
            } else {
                out.println("Channel doesn't exist!");
            }
        }

        public void disconnectFromChannel(User user, Channel channel) {
            List<ClientHandler> handlers = channels.get(channel);
            if (handlers != null) {
                synchronized (handlers) {
                    handlers.remove(this);
                    channels.put(channel, handlers);
                }
                broadcastMessage(user.getUserName() + " has left the channel " + channel.getName(), channel);
                out.println("HTTP_STATUS_200");
            } else {
                out.println("You can't disconnect from a channel that doesn't exist!");
            }
        }

        public void broadcastMessage(String message, Channel channel) {
            List<ClientHandler> handlers = channels.get(channel);
            synchronized (handlers) {
                for (ClientHandler handler : handlers) {
                    if (!message.isEmpty() && !message.equals(" "))
                        handler.out.println(message);
                }
            }
        }


        public void run() {
            try {
                this.out = new PrintWriter(clientSocket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    if (STOP_STRING.equals(message)) {
                        break;
                    }

                    String[] rawMessage = message.split(": ", 2);
                    if (rawMessage.length < 2) {
                        out.println("Invalid message format!");
                        continue;
                    }

                    String username = rawMessage[0];
                    message = rawMessage[1];

                    if (currentUser == null) {
                        currentUser = new User(username);
                    }

                    if (message.startsWith("/")) {
                        String[] command = message.split(" ");
                        if (command.length > 0) {
                            switch (command[0]) {
                                case CREATE_CHANNEL_COMMAND:
                                    if (command.length == 3){
                                        disconnectFromChannel(currentUser, currentChannel);
                                        createChannel(currentUser , command[1], command[2]);
                                    } else {
                                        out.println("Invalid create command format!");
                                    }
                                    break;
                                case CONNECT_TO_CHANNEL:
                                    if (command.length == 3) {
                                        boolean found = false;
                                        for (Channel c : channels.keySet()) {
                                            if (c.getName().equals(command[1]) && c.getPassword().equals(command[2])) {
                                                disconnectFromChannel(currentUser, currentChannel);
                                                connectToChannel(currentUser, c, true);
                                                found = true;
                                                break;
                                            }
                                        }
                                        if (!found) {
                                            out.println("Channel not found!");
                                        }
                                    } else {
                                        out.println("Invalid connect command format!");
                                    }
                                    break;
                                case DISCONNECT_FROM_CHANNEL:
                                    boolean disconnected = false;
                                    for (Channel c : channels.keySet()) {
                                        List<ClientHandler> handlers = channels.get(c);
                                        if (handlers.contains(this)) {
                                            disconnectFromChannel(currentUser, c);
                                            disconnected = true;
                                            break;
                                        }
                                    }
                                    if (!disconnected) {
                                        out.println("You are not in any channel!");
                                    }
                                    break;
                                default:
                                    out.println("Command unknown!");
                            }
                        }
                    } else {
                        if (!message.equals(" "))
                            broadcastMessage(currentUser.getUserName() + ": " + message, currentChannel);
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
