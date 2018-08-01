package model;

import common.Define;
import common.Util;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import util.RoomManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {

    /*  Field */
    public SocketChannel socketChannel;
    public SelectionKey selectionKey;
    public String name;
    public String response;
    public Room room;
    public boolean play;
    public boolean team;
    private Charset charset = Charset.forName("UTF-8");

    /* Constructor */
    public Client(SocketChannel socketChannel) {
        try {
            this.socketChannel = socketChannel;
            this.play = false;
            this.team = false;

            // == OP_READ - SelectionKey 추가 ==
            this.socketChannel.configureBlocking(false);
            this.selectionKey = this.socketChannel.register(Server.getInstance().selector, SelectionKey.OP_READ);
            this.selectionKey.attach(this);

            // == Send (init) ==
            //            this.response = String.format(
            //                    "{\"%s\":\"%s\",\"%s\":\"[%s]\"}",
            //                    Define.DATA_TYPE,
            //                    Define.URL_INIT,
            //                    Define.DATA_ROOMS,
            //                    RoomManager.getInstance().toString());
            //            send();
            // Server.getInstance().selector.wakeup();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Receive */
    public void receive() {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(Define.BUFFER_SIZE);

            int byteCount = socketChannel.read(byteBuffer);
            if (byteCount == -1) throw new IOException(); // == close() 호출 (정상종료) ==

            // == 메세지 ==
            byteBuffer.flip();
            Charset cs = Charset.forName("UTF-8");
            String request = cs.decode(byteBuffer).toString();

            // == route() ==
            route(request);

        } catch (IOException errB) {
            terminate();
        }
    }

    /* Route */
    private void route(String request) {
        try {
            // @Test
            // System.out.println(request);

            // == JSON Parser ==
            JSONParser jsonParser = new JSONParser();
            JSONObject token = (JSONObject) jsonParser.parse(request);
            String type = token.get(Define.DATA_TYPE).toString();

            // @Test
            Util.log("[Othello서버] 수신 URL : " + type);
            // Util.log("[Othello서버] 현스레드 : " + Thread.currentThread().getName() );

            // == Routing ==
            switch (type) {
                case Define.URL_USER:
                    name = token.get(Define.DATA_USER_NAME).toString();
                    Util.log("[Othello서버] 이름 : " + name);
                    break;
                // == URL_CREATE ==
                case Define.URL_CREATE:
                    if (room == null) {
                        this.team = true;
                        RoomManager.getInstance().create(Client.this, token.get(Define.DATA_ROOM_TITLE).toString() );
                        Util.log("[Othello서버] 방개설 - 개수 : " + RoomManager.getInstance().rooms.size());
                    }
                    break;
                // == URL_ENTRY ==
                case Define.URL_ENTRY:
                    if (room == null) {
                        this.team = false;
                        for (int i = 0; i < RoomManager.getInstance().rooms.size(); i++) {
                            if (RoomManager.getInstance().rooms.get(i).id.equals(token.get(Define.DATA_ROOM_ID).toString())) {
                                RoomManager.getInstance().rooms.get(i).entry(Client.this);
                                Util.log("[Othello서버] 방입장 : " + name);
                                break;
                            }
                        }
                    }
                    break;
                // == URL_END ==
                case Define.URL_END:
                    if (room != null) {
                        Client.this.room.leave(team);
                        Util.log("[Othello서버] 방탈출 : " + RoomManager.getInstance().rooms.size());
                    }
                    break;
                // == URL_READY  ==
                case Define.URL_READY :

                    break;
                // == URL_START  ==
                case Define.URL_START :

                    break;
                // == URL_TURN  ==
                case Define.URL_TURN :

                    break;
                // == URL_RESULT  ==
                case Define.URL_RESULT :

                    break;
                // == URL_CHAT ==
                case Define.URL_CHAT:

                    break;
                default:
                    Util.log("[Othello서버] 메소드가 올바르지 않습니다. : " + socketChannel.getRemoteAddress());
            }

            // == wakeup() ==
            Server.getInstance().selector.wakeup();

        } catch (Exception errA) {
            Util.log("[Othello서버] 올바른 데이터 형식이 아닙니다. : " + request);
            errA.printStackTrace();
        }
    }

    /* Send */
    public void send() {
        try {
            // Util.log("[Othello서버] 현스레드 : " + Thread.currentThread().getName() );

            // == write() ==
            selectionKey.interestOps(SelectionKey.OP_WRITE);
            Util.log("[Othello서버] 송신 : " + this.response);
            ByteBuffer byteBuffer = charset.encode(this.response);
            socketChannel.write(byteBuffer);

            // == wakeUp() ==
            selectionKey.interestOps(SelectionKey.OP_READ);
            Server.getInstance().selector.wakeup();
        } catch (IOException e) {
            terminate();
        }
    }

    /* Terminate */
    void terminate() {
        try {
            Util.log("[Othello서버] 통신두절 : " + socketChannel.getRemoteAddress());
            if (room != null) Client.this.room.leave(team);
            Server.getInstance().clients.remove(Client.this);
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
