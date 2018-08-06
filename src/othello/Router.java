package othello;

import common.Define;
import common.Util;
import model.Room;
import util.Request;

import java.util.List;

public class Router {

    /* routeSetName */
    public static void routeSetName(O_Client client, String request) {
        List<String> body = Request.cutBody(request, Define.URL_REG_URSE_PROTOCOL);
        client.name = body.get(0);

        Util.log("[서버] 유저이름 : " + client.name);
    }

    /* routeSetName */
    public static void routeRoomUpdate() {
        RoomManager.getInstance().setUpdate(true);
    }

    /* routeChat */
    public static void routeChat(O_Client client, String request) {
        if(client.isStandBy()) {
            // TODO [채팅정보를 전송합니다. (전체)]

        } else {
            // TODO [채팅정보를 전송합니다. (게임)]
            client.room.getOppenent(client).send(request);
        }
    }

    /* routeCreateRoom */
    public static void routeCreateRoom(O_Client client, String request) {
        if(RoomManager.getInstance().rooms.size() == Define.MAX_ROOM) return;

        if (client.isStandBy()) {
            List<String> body = Request.cutBody(request, Define.URL_REG_CREATE_PROTOCOL);

            // == 방개설로직 ==
            RoomManager.getInstance().create(client, body.get(0));

            Util.log("[서버] 방개설 : " + client.name);
        }
    }

    /* routeEntryRoom */
    public static void routeEntryRoom(O_Client client, String request) {
        //System.out.println("routeEntryRoom call");
        if (client.isStandBy()) {
            List<Room> rooms = RoomManager.getInstance().rooms;
            List<String> body = Request.cutBody(request, Define.URL_REQ_ENTRY_PROTOCOL);
            for (int i = 0; i < rooms.size(); i++) {
                if (
                        rooms.get(i).getId().equals(body.get(1))
                                &&
                                rooms.get(i).isFull() == false
                                &&
                                rooms.get(i).getGuardian() != client
                        ) {

                    // == 방입장로직 ==
                    client.room = rooms.get(i);
                    rooms.get(i).entry(client);

                    Util.log("[서버] 방입장 : " + client.name);
                    break; // for
                }
            }
            // TODO 만약입장하지못하면 (누군가먼저들어갔거나, 방이삭제됬거나)

        }
    }

    /* routeLeaveRoom */
    public static void routeLeaveRoom(O_Client client) {
        System.out.println("routeLeaveRoom call");
        if (!client.isStandBy()) {

            // == 방탈출로직 ==
            client.room.leave(client.room.isGuardian(client));
            client.room = null;

            Util.log("[서버] 방떠남 : " + client.name);
        }
    }

    /* routeReadyGame */
    public static void routeReadyGame(O_Client client) {
        System.out.println("routeReadyGame call");
        if(!client.isStandBy() && !client.room.isPlay()) {
            if(client.room.isGuardian(client)) {
                // == 방장 ==
                if(client.room.isFull() && client.room.getChallenger().isReady()) {
                    client.ready = true;

                    // == 게임스타트 ==
                    client.room.start();
                    client.room.getGuardian().send(Define.URL_RES_START);
                    client.room.getChallenger().send(Define.URL_RES_START);
                } else {
                    // TODO [준비되징 않은 유저가 있습니다.]
                    client.send(Define.URL_RES_READY_REJECT);
                }
            } else {
                // == 손님 ==
                client.ready = true;

                // TODO [방장에게 준비권한을 전달합니다.]
                client.room.getGuardian().send(Define.URL_RES_READY_ACCEPT);
            }
        }
    }

    /* routeTurnGame */
    public static void routeTurnGame(O_Client client, String request) {
        if(!client.isStandBy() && client.room.isPlay())
            client.room.getOppenent(client).send(request);
    }

}
