package client;

import utils.User;

import java.io.IOException;
import java.util.Scanner;

public class StartClient {
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        // Connect to server
        client.startConnection("127.0.0.1", 2222);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            client.sendMessage(scanner.nextLine(), new User("TestUser"));
        }
    }
}

class StartClient2 {
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        // Connect to server
        client.startConnection("127.0.0.1", 2222);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            client.sendMessage(scanner.nextLine(), new User("TestUser2"));
        }
    }
}
