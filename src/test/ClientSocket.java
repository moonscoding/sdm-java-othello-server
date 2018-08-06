package test;

import common.Define;
import util.Request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;

public class ClientSocket {

    /* Field */
    private static ClientSocket instance;
    public SocketChannel socketChannel;
    private Queue<String> postQueue = new LinkedList<>(); // 전송할 데이터를 담아두는 Queue
    private boolean busy = false; // Queue를 사용하고 있는지

    /* Constructor */
    public ClientSocket(String host, short port) throws RuntimeException {
        // == singleton ==
        if(instance != null) return;
        instance = this;
        startSocket(host, port);
    }

    /* GetInstance */
    public static ClientSocket getInstance() {
        return instance;
    }

    /* StartSocket */
    public void startSocket(String host, short port) throws RuntimeException {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    socketChannel = SocketChannel.open();

                    // ### 블로킹방식 (명시적) ###
                    socketChannel.configureBlocking(true);

                    // ### connect(new InetSocketAddress) ###
                    socketChannel.connect(new InetSocketAddress(host, port));

                } catch (IOException err) {
                    // TODO 오류 - 서버
                    err.printStackTrace();
                }
                receive();
            }
        };
        thread.start();
    }

    /* StopSocket */
    public void stopSocket() {
        try {
            if(socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
        } catch (IOException e) {
            // TODO 오류 - 서버
            e.printStackTrace();
        }
    }

    /* Receive */
    public void receive() {
        while(true) {
            try {

                // == read ==
                ByteBuffer byteBuffer = ByteBuffer.allocate(Define.CLIENT_BUFFER_SIZE); // 1024
                int byteCount = socketChannel.read(byteBuffer);
                if(byteCount == -1) throw new IOException();

                byteBuffer.flip();
                String response = Define.CHARSET.decode(byteBuffer).toString();

                // == route ==
                route(response);
            } catch (IOException e) {
                // TODO 오류 - 서버
                e.printStackTrace();
                break;
            }
        }
    }

    /* Route */
    private void route ( String response ) {

        System.out.println("[테스터] " + response);

        // == Routing ==
        switch (Request.cutHeader(response)) {
            case Define.URL_REG_URSE:
                break;
            // == URL_REG_CREATE ==
            case Define.URL_REG_CREATE:
                break;
            // == URL_REQ_ENTRY ==
            case Define.URL_REQ_ENTRY:
                break;
            // == URL_REG_LEAVE ==
            case Define.URL_REG_LEAVE:
                break;
            // == URL_REQ_READY  ==
            case Define.URL_REQ_READY :
                break;
            // == URL_REG_TURN  ==
            case Define.URL_REG_TURN :
                break;
            // == URL_REG_RESULT  ==
            case Define.URL_REG_RESULT :
                break;
            // == URL_REG_CHAT ==
            case Define.URL_REG_CHAT:
                break;
            default:
                break;
        }
    }

    /* Send */
    public void send( String request ) {
        postQueue.offer(request);
        if(!busy) {
            busy = true;
            post();
        }
    }

    /* Post */
    private void post() {
        String request = postQueue.poll();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    try {
                        // == write ==
                        System.out.println(request);
                        ByteBuffer byteBuffer = Define.CHARSET.encode( request );
                        socketChannel.write(byteBuffer);

                        // == queue ==
                        if(postQueue.size() > 0) {
                            post();
                        } else {
                            busy = false;
                        }
                    } catch (NotYetConnectedException errA) {
                        errA.printStackTrace();
                    }
                } catch (IOException errB) {
                    // TODO 오류 - 서버
                    errB.printStackTrace();
                }
            }
        };
        thread.start();
    }

}
