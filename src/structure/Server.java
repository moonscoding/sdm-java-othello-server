package structure;

import common.Util;
import othello.O_Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* O_Server - singleton */
public abstract class Server<T> {

    private static Server instance;

    /* Field */
    public ExecutorService executorService;
    public ServerSocketChannel serverSocketChannel;
    public Selector selector;
    public List<T> clients;

    /* Constructor */
    public Server(short port) {
        if (instance != null) return;
        instance = this;

        this.clients = new Vector<>();
        start(port);
    }

    /* GetInstance */
    public static Server getInstance() {
        return instance;
    }

    /* Start */
    public void start(short port) {

        // == 가용한 프로세서만큼 스레드 생성 ==
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // 4

        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();

            // == 넌블로킹설정 ==
            serverSocketChannel.configureBlocking(false);

            // == bind(port) ==
            serverSocketChannel.bind(new InetSocketAddress(port));

            // == register() ==
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("[서버] 서버실행");

            // == select() ==
            select();

        } catch (IOException e) {
            System.out.println("[서버] 같은 포트의 서버가 이미 실행중 일 수 있습니다.");
            // TODO [프로세스를 꺼버리고 다시 실행]
            e.printStackTrace();
        }
    }

    /* Stop */
    public void stop() {
        try {
            if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
                serverSocketChannel.close();
            }
            if (executorService != null && executorService.isShutdown()) {
                executorService.shutdown();
            }
            Util.log("[서버] 서버종료");
        } catch (IOException e) {
        }
    }

    /* Select - 기다려서 분기해주는 메소드 */
    protected void select() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        // == select() ==
                        int keyCount = selector.select();
                        if (keyCount == 0) {
                            continue;
                        }

                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();
                        // Util.log("[서버] SIZE : " + Thread.currentThread().getName() );

                        while (iterator.hasNext()) {
                            SelectionKey selectionKey = iterator.next();

                            // == accept ==
                            if (selectionKey.isAcceptable()) {
                                startClient(selectionKey);
                            }
                            // == read ==
                            else if (selectionKey.isReadable()) {
                                O_Client client = (O_Client) selectionKey.attachment();
                                client.receive();
                            }
                            // == write ==
                            else if (selectionKey.isWritable()) {
                                O_Client client = (O_Client) selectionKey.attachment();
                                client.post();
                            }

                            // == (처리완료) Key remove()  ==
                            iterator.remove();
                        }
                    } catch (IOException e) {
                        if (serverSocketChannel.isOpen()) stop();
                        break;
                    }
                }
            }
        };
        executorService.submit(runnable);
    }

    /* startClient */
    protected abstract void startClient(SelectionKey selectionKey);

    /* stopClient */
    public abstract void stopClients();

}
