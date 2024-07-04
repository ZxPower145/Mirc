package server;

import java.io.*;

public class Channel implements Serializable {
    private final String name;
    private final String password;

    public Channel(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }
}
