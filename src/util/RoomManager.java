package util;

import model.Client;
import model.Room;

import java.util.List;
import java.util.Vector;

/* Room Model 관리객체 - singleton */
public class RoomManager {

    /* Field */
    private static RoomManager instance;
    public List<Room> rooms;

    /* Constructor */
    public RoomManager() {
        if (instance != null) return;
        instance = this;

        rooms = new Vector<>();
    }

    /* GetInstance */
    public static RoomManager getInstance() {
        return instance;
    }

    /* Create */
    public void create(Client client, String title) {
        Room room = new Room(client, title);
        rooms.add(room);

        // == [send] room update ==

    }

    /* Destroy */
    public void destroy() {

        // == [send] room update ==

    }

    /* clear */
    public void clear() {

    }

    /* Init - 초기입장유저에게 보내는데이터 */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (rooms.size() > 0) {
            for (Room room : rooms) {
                sb.append(
                        "\"" +
                        room.id + "_" +
                        room.title + "_" +
                        room.wins + "_" +
                        room.guardian.name + "_" +
                        (room.opponent == null ? 1 : 2) + "\","
                );
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
