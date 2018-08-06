package test;

import common.Define;
import util.Response;

import java.util.Scanner;

public class ClientMain {

    static ClientSocket mySocket;
    static String username = "hello";

    public static void main(String[] args) {

        System.out.println(Define.isDebug);
        if(Define.isDebug) {
            // == Debug ==
            mySocket = new ClientSocket(Define.HOST, Define.PORT_DEBUG);
        } else {
            // == Production ==
            mySocket = new ClientSocket(Define.HOST, Define.PORT_PROP);
        }
        Scanner stdIn = new Scanner(System.in);

        // == 이름우선전송 ==
        mySocket.send(Define.URL_REG_URSE + Response.fullBlank(username, Define.SIZE_USER_NAME));

        outer:
        while(true) {
            System.out.println("============================");
            System.out.println("0. 종료");
            System.out.println("1. 유저이름변경");
            System.out.println("2. 방개설하기");
            System.out.println("3. 방입장하기");
            System.out.println("4. 방나가기");
            System.out.println("5. 게임준비하기");
            System.out.println("6. 게임시작하기");
            System.out.println("6. 게임잔행하기");
            System.out.println("7. 결과전달하기");

            int type = stdIn.nextInt();

            switch (type) {
                case 0:
                    break outer;
                case 1:
                    // == 이름변경 ==
                    mySocket.send(Define.URL_REG_URSE + Response.fullBlank("world", Define.SIZE_USER_NAME));
                    break;
                case 2:
                    // == 생성 ==
                    mySocket.send(Define.URL_REG_CREATE + Response.fullBlank("room", Define.SIZE_ROOM_TITLE));
                    break;
                case 3:
                    // == 입장 ==
                    mySocket.send(Define.URL_REQ_ENTRY + Response.fullBlank("fix", Define.SIZE_ROOM_ID));
                    break;
                case 4:
                    // == 떠남 ==
                    mySocket.send(Define.URL_REG_LEAVE);
                    break;
                case 5:
                    // == 준비 ==
                    mySocket.send(Define.URL_REQ_READY);
                    break;
                case 6:
                    // == 진행 ==
                    mySocket.send(Define.URL_REG_TURN + new String(new byte[]{ 1, 2 }));
                    break;
                case 7:
                    // == 결과 ==
                    mySocket.send(Define.URL_REQ_READY);
                    break;
            }
        }
        System.out.println("종료");
    }
}
