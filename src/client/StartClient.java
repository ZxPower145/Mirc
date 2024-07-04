package client;

import java.io.IOException;

public class StartClient {
    public static void main(String[] args) throws IOException {
        // new Gui();
        // Connect to server
        // In GUI constructor
        Client client = new Client();
        client.startConnection("34.159.211.187", 2222);
    }
}
