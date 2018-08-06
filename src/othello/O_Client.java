package othello;

import common.Define;
import common.Util;
import model.Room;
import structure.Client;
import util.Request;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class O_Client extends Client {

    /* Field */
    public String name; // 유저이름
    public Room room; // 소속방
    public boolean ready; // 시작준비
    private List<String> body; // 수신데이터

    /* Constructor */
    public O_Client(SocketChannel socketChannel, Selector selector, ExecutorService executorService) {
        super(socketChannel, selector, executorService);
        this.ready = false;
        this.room = null;
    }

    /* isStandBy - 대기방에 있는지 여부 */
    public boolean isStandBy() {
        return room == null;
    }

    /* isReady */
    public boolean isReady() {
        return ready;
    }

    /* Route */
    @Override
    protected void route(String request) {
        try {
            // == Routing ==
            switch (Request.cutHeader(request)) {
                // == URL_REG_URSE ==
                case Define.URL_REG_URSE:
                    Router.routeSetName(this, request);
                    break;
                // == URL_REQ_UPDATE ==
                case Define.URL_REQ_UPDATE:
                    Router.routeRoomUpdate();
                    break;
                // == URL_REG_CREATE ==
                case Define.URL_REG_CREATE:
                    Router.routeCreateRoom(this, request);
                    break;
                // == URL_REQ_ENTRY ==
                case Define.URL_REQ_ENTRY:
                    Router.routeEntryRoom(this, request);
                    break;
                // == URL_REG_LEAVE ==
                case Define.URL_REG_LEAVE:
                    Router.routeLeaveRoom(this);
                    break;
                // == URL_REQ_READY  ==
                case Define.URL_REQ_READY :
                    Router.routeReadyGame(this);
                    break;
                // == URL_REG_TURN  ==
                case Define.URL_REG_TURN :
                    Router.routeTurnGame(this, request);
                    break;
                // == URL_REG_CHAT ==
                case Define.URL_REG_CHAT:
                    Router.routeChat(this, request);
                    break;
                default:
                    Util.log("[서버] Header의 상태플래그가 올바르지 않습니다. : " + Request.cutHeader(request));
            }

            // == wakeup() ==
            Util.log(String.format("[서버] 유저수 : %d,  방갯수 : %d", O_Server.getInstance().clients.size(), RoomManager.getInstance().rooms.size()));
            selector.wakeup();

        } catch (Exception errA) {
            Util.log("[서버] 올바른 데이터 형식이 아닙니다. : " + request);
            errA.printStackTrace();
        }
    }

    /* terminate - client 종료로직 */
    public void terminate() {
        if (!isStandBy()) this.room.leave(this.room.isGuardian(this));
        try {
            Util.log("[서버] 통신두절 : " + this.name);
            O_Server.getInstance().clients.remove(this);
            socketChannel.close();

            Util.log(String.format("[서버] 유저수 : %d,  방갯수 : %d", O_Server.getInstance().clients.size(), RoomManager.getInstance().rooms.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
