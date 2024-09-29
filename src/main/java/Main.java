import connection_pool.ConnectionPool;
import thread_management.*;

public class Main {
    volatile static ConnectionPool pool = new ConnectionPool();

    public static void main(String[] args) throws InterruptedException {
        pool.initialize(5);

        ThreadManager threadManager = new ExecutorThreadManager(5);
        threadManager.testPool(pool);
    }
}
