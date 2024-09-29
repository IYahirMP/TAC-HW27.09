package thread_management;

import connection_pool.ConnectionPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceThreadManager implements ThreadManager {
    private ExecutorService executor;
    private int threadNumber;

    public ExecutorServiceThreadManager(int threadNumber){
        this.threadNumber = threadNumber;
        executor = Executors.newFixedThreadPool(threadNumber + 2);
    }

    private void initializeAbusiveThreads(ConnectionPool pool){
        //Creates threads that are busy
        for(int i = 0; i < threadNumber; i++) {
            executor.submit(RunnableManager.createAbusiveThread(pool));
        }
    }

    private void initializeWaitingThreads(ConnectionPool pool){
        for(int i = 0; i < 2; i++) {
            executor.submit(RunnableManager.createWaitingThread(pool));
        }
    }

    public void testPool(ConnectionPool pool) throws InterruptedException {
        initializeAbusiveThreads(pool);
        Thread.sleep(1000);
        initializeWaitingThreads(pool);

        executor.close();
    }
}
