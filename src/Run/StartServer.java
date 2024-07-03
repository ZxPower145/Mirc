package Run;

import server.MircServer;

import java.io.IOException;

public class StartServer {
    public static void main(String[] args) throws IOException {
        MircServer server = new MircServer();
        server.start();
    }
}
