import connection_pool.ConnectionPool;
import thread_management.*;
import thread_management.future_api.CompletableFutureManager;

public class Main {
    volatile static ConnectionPool pool = new ConnectionPool();

    public static void main(String[] args) throws InterruptedException {
        pool.initialize(5);

        ThreadManager threadManager = new CompletableFutureManager(5);
        threadManager.testPool(pool);
    }
}
