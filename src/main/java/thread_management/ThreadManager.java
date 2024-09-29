package thread_management;
import connection_pool.ConnectionPool;

public interface ThreadManager {
    void testPool(ConnectionPool pool) throws InterruptedException;
}
