package Run;

import client.MircClient;

import java.io.IOException;

public class StartClient {
    public static void main(String[] args) throws IOException {
        MircClient client = new MircClient();
        client.startConnection("127.0.0.1", 2222);
        client.sendMessage(null);
    }
}
