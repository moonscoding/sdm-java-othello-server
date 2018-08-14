package othello;

import common.Define;
import model.Room;
import util.Response;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

/* Room Model 관리객체 - singleton */
public class RoomManager {

    /* Field */
    private static RoomManager instance;
    public List<Room> rooms;
    private boolean update = false;
    private boolean updating = false;

    /* Constructor */
    public RoomManager() {
        if (instance != null) return;
        instance = this;
        update = false;
        rooms = new Vector<>();
    }

    /* GetInstance */
    public static RoomManager getInstance() {
        return instance;
    }

    /* isUpdate - 방에대한 업데이트가 있었는지 여부 */
    public boolean isUpdate() {
        return update;
    }

    /* setUpdate */
    public void setUpdate(boolean update) {
        this.update = update;
        if(this.update) update();
    }

    /* toString - rooms to string */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (rooms.size() > 0) {
            for (Room room : rooms) {
                sb.append(
                    Response.fullBlank(room.getId(), Define.SIZE_ROOM_ID) +
                    Response.fullBlank(room.getTitle(), Define.SIZE_ROOM_TITLE) +
                    Response.fullBlank(room.getWins() + "", Define.SIZE_ROOM_WINS) +
                    Response.fullBlank(room.getGuardian().name, Define.SIZE_USER_NAME) +
                    (room.getChallenger() == null ? 1 : 2)
                );
            }
        }
        return sb.toString();
    }

    /* Create */
    public void create(O_Client creater, String title) {
        Room room = new Room(creater, title);
        creater.room = room;
        rooms.add(room);

        // == 브로드캐스트호출 ==
        setUpdate(true);
    }

    /* update */
    public void update() {
        if(updating) return;
        updating = true;
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(Define.WAIT_TIME);
                    postUpdateRoom();
                    update = false; updating = false;
                } catch (InterruptedException e) {
                    // TODO
                }
            }
        };
        runnable.run();
    }

    /* postUpdateRoom */
    public void postUpdateRoom() {
        for (int i = 0; i < O_Server.getInstance().clients.size(); i++) {
            O_Client client = (O_Client) O_Server.getInstance().clients.get(i);
            if(client.isStandBy()) {
                client.send(Define.URL_RES_UPDATE + this.toString());
            }
        }
    }

    /* getUniqueId */
    public static String getUniqueId(String id) {
        for(Room room : RoomManager.getInstance().rooms) {
            if(room.getId().equals(id)) {
               return getUniqueId(UUID.randomUUID().toString());
            }
        }
        return id;
    }
}
