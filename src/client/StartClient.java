package client;

import utils.User;

import java.io.IOException;
import java.util.Scanner;

public class StartClient {
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        // Connect to server
        client.startConnection("34.159.211.187", 2222);
        Scanner scanner = new Scanner(System.in);
        //TODO: pass username from gui
        String username = "UserName";
        User user = new User(username);

        while (true) {
            client.sendMessage(scanner.nextLine(), user);
        }
    }
}
