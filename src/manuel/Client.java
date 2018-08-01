package manuel;

import java.nio.channels.SocketChannel;

public interface Client {
    public abstract void receive();
    public abstract void send();
    public void terminate();
}
