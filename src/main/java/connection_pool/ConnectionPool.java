package connection_pool;

import java.util.NoSuchElementException;
import java.util.concurrent.*;

public class ConnectionPool implements Pool<Connection>{
    private ConcurrentLinkedQueue<Connection> availableConnections = new ConcurrentLinkedQueue<>();
    private volatile int maxConnections = 5;
    private volatile int borrowedConnections = 0;

    @Override
    public synchronized Connection borrow(){
        if (!availableConnections.isEmpty() && borrowedConnections < maxConnections){
            borrowedConnections++;
            return availableConnections.poll();
        }else{
            throw new NoSuchElementException("No connections available");
        }
    }

    @Override
    public synchronized void release(Connection obj) {
        availableConnections.add(obj);
        borrowedConnections--;
    }

    @Override
    public void shutDown() {
        availableConnections.clear();
    }

    @Override
    public void initialize() {
        availableConnections = new ConcurrentLinkedQueue<>();
        borrowedConnections = 0;
    }

    public synchronized void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public synchronized int getMaxConnections() {
        return maxConnections;
    }

    public synchronized void addConnection() throws Exception{
        if (availableConnections.size() < maxConnections) {
            availableConnections.add(new Connection());
        }else{
            throw new Exception("Maximum number of connections reached");
        }
    }
}
