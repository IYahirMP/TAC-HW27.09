import connection_pool.ConnectionPool;
import thread_management.ClassicThreadManager;

public class Main {
    volatile static ConnectionPool pool = new ConnectionPool();

    public static void main(String[] args) throws InterruptedException {
        pool.initialize(5);
        ClassicThreadManager threadManager = new ClassicThreadManager(5);
        threadManager.someFunction(pool);


    }
}
