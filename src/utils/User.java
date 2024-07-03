package utils;

public class User<T> {
    private final String userName;
    private String currentRoom;

    public User(T userName) {
        assert userName != null;
        this.userName = userName.toString();
    }

    public void setCurrentRoom(T room) {
        assert room != null;
        this.currentRoom = room.toString();
    }

    public String getCurrentRoom() { return this.currentRoom; }

    public String getUserName() {
        return this.userName;
    }
}
