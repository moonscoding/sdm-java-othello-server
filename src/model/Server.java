package model;

import common.Define;
import common.Util;
import util.RoomManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* Server - singleton */
public class Server {

    private static Server instance;

    /* Field */
    public ExecutorService executorService;
    public ServerSocketChannel serverSocketChannel;
    public Selector selector;
    public List<Client> clients;
    public RoomManager roomManager;

    /* Constructor */
    public Server() {
        if(instance != null) return;
        instance = this;

        this.clients = new Vector<>();
        this.roomManager = new RoomManager();
        start();
    }

    /* GetInstance */
    public static Server getInstance() {
        return instance;
    }

    /* Start */
    public void start() {

        // == 가용한 프로세서만큼 스레드 생성 ==
        executorService = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() ); // 4

        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();

            // == 넌블로킹설정 ==
            serverSocketChannel.configureBlocking(false);

            // == bind(port) ==
            serverSocketChannel.bind( new InetSocketAddress(Define.PORT) );

            // == register() ==
            serverSocketChannel.register( selector, SelectionKey.OP_ACCEPT );
            System.out.println("[Othello서버] 서버실행");

            // == select() ==
            select();

        } catch (IOException e) {
            System.out.println("[Othello서버] 같은 포트의 서버가 이미 실행중 일 수 있습니다.");
            e.printStackTrace();
        }
    }

    /* Stop */
    public void stop() {
        try {
            Iterator<Client> iterator = clients.iterator();
            while(iterator.hasNext()) {
                Client client = iterator.next();
                client.socketChannel.close();
                iterator.remove();
            }
            if(serverSocketChannel != null && serverSocketChannel.isOpen()) {
                serverSocketChannel .close();
            }
            if(executorService != null && executorService.isShutdown()) {
                executorService.shutdown();
            }
            Util.log("[Othello서버] 서버종료");
        } catch (IOException e) {}
    }

    /* Select - 기다려서 분기해주는 메소드 */
    public void select() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {

                        // == select() ==
                        int keyCount = selector.select();
                        if(keyCount == 0) { continue; }

                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();
                        // Util.log("[Othello서버] 현스레드 : " + Thread.currentThread().getName() );

                        while(iterator.hasNext()) {
                            SelectionKey selectionKey = iterator.next();

                            // == accept ==
                            if(selectionKey.isAcceptable()) {
                                accept(selectionKey);
                            }
                            // == read ==
                            else if(selectionKey.isReadable()) {
                                Client client = (Client) selectionKey.attachment();
                                client.receive();
                            }
                            // == write ==
                            else if(selectionKey.isWritable()) { //
                                Client client = (Client) selectionKey.attachment();
                                client.send();
                            }

                            // == (처리완료) Key remove()  ==
                            iterator.remove();
                        }
                    } catch (IOException e) {
                        if(serverSocketChannel.isOpen()) stop();
                        break;
                    }
                }
            }
        };
        executorService.submit(runnable);
    }

    /* Accept */
    void accept(SelectionKey selectionKey) {
        try {
            // == accept ==
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();

            InetSocketAddress isa = (InetSocketAddress) socketChannel.getRemoteAddress();
            Util.log("[Othello서버] 새로운 클라이언트접속 " + isa.getHostName() );
            Util.log("[Othello서버] 현스레드 : " + Thread.currentThread().getName() );

            // == client create ==
            Client client = new Client( socketChannel );
            clients.add(client);

            Util.log("[Othello서버] 현재접속 클라이언트 수 : " + clients.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
