package structure;

import common.Define;
import common.Util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public abstract class Client {

    /*  Field */
    public SocketChannel socketChannel;
    public SelectionKey selectionKey;
    public ExecutorService executorService;
    public Selector selector;
    public Queue<String> response;
    public boolean busy;

    /* Constructor */
    public Client(SocketChannel socketChannel, Selector selector, ExecutorService executorService) {
        try {
            this.busy = false;
            this.socketChannel = socketChannel;
            this.selector = selector;
            this.executorService = executorService;
            this.response = new LinkedList();

            // == OP_READ - SelectionKey 추가 ==
            this.socketChannel.configureBlocking(false);
            this.selectionKey = this.socketChannel.register(this.selector, SelectionKey.OP_READ);
            this.selectionKey.attach(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Receive */
    public void receive() {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            // == Request ==
            int byteCount = socketChannel.read(byteBuffer);
            if (byteCount == -1) throw new IOException(); // == close() 호출 (정상종료) ==
            byteBuffer.flip();
            String reqeust = Define.CHARSET.decode(byteBuffer).toString();
            Util.log("[서버] 수신 : " + reqeust);

            // == route ==
            route(reqeust);

        } catch (IOException errB) {
            terminate();
        }
    }

    /* Route */
    protected abstract void route(String request);

    /* Send */
    public void send(String msg) {
        this.response.add(msg);
        if (this.busy) return;
        this.busy = true;

        selectionKey.interestOps(SelectionKey.OP_WRITE);
        this.selector.wakeup();
        // post();
    }

    /* Post */
    public void post() {
        /**
         *  Send
         * => reponse를 큐로 이용하긴 해야함
         *
         * - 직접호출 방식 (O)
         * - Selector이용 방식 (X)
         * */

        try {
            Util.log("[서버] 송신 : " + Client.this.response);

            // == write ==
            String msg = Client.this.response.remove();
            ByteBuffer byteBuffer = Define.CHARSET.encode(msg);
            socketChannel.write(byteBuffer);

            // == queue ==
            if (Client.this.response.size() > 0) {

                // == wakeup to WRITE ==
                post();
                Client.this.selector.wakeup();
            }
            else {
                Client.this.busy = false;

                // == wakeup to READ ==
                selectionKey.interestOps(SelectionKey.OP_READ);
                Client.this.selector.wakeup();
            }
        } catch (IOException e) {
            terminate();
        }

        //        Thread thread = new Thread() {
        //            @Override
        //            public void run() {
        //
        //            }
        //        };
        //        executorService.submit(thread);
    }

    /* isBusy */
    public boolean isBusy() {
        return busy;
    }

    /* Terminate */
    protected abstract void terminate();

}
