package utils;

import server.Channel;

public class User<T> {
    private final String userName;
    private Channel currentRoom;

    public User(T userName) {
        assert userName != null;
        this.userName = userName.toString();
    }

    public void setCurrentRoom(Channel channel) {
        assert channel != null;
        this.currentRoom = channel;
    }

    public Channel getCurrentRoom() { return this.currentRoom; }

    public String getUserName() {
        return this.userName;
    }
}
