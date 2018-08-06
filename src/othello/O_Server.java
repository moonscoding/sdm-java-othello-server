package othello;

import common.Util;
import structure.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/* O_Server - singleton */
public class O_Server extends Server<O_Client> {

    public RoomManager roomManager;

    public O_Server(short port) {
        super(port);
        this.roomManager = new RoomManager();
    }

    @Override
    protected void startClient(SelectionKey selectionKey) {
        try {
            // == accept ==
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();

            InetSocketAddress isa = (InetSocketAddress) socketChannel.getRemoteAddress();
            Util.log("[서버] 새로운 클라이언트접속 " + isa.getHostName() );

            // == client create ==
            O_Client client = new O_Client( socketChannel , selector, executorService );
            clients.add(client);

            Util.log("[서버] 현재접속 클라이언트 수 : " + clients.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopClients() {
        try {
            Iterator<O_Client> iterator = clients.iterator();
            while(iterator.hasNext()) {
                O_Client client = iterator.next();
                client.socketChannel.close();
                iterator.remove();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
