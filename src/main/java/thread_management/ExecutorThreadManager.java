package thread_management;

import connection_pool.ConnectionPool;

import java.util.concurrent.*;
import java.lang.Runnable;

public class ExecutorThreadManager implements ThreadManager {
    private Executor executor;
    private int threadNumber;

    public ExecutorThreadManager(int threadNumber){
        this.threadNumber = threadNumber;
        executor = Executors.newFixedThreadPool(threadNumber + 2);
    }

    private void initializeAbusiveThreads(ConnectionPool pool){
        //Creates threads that are busy
        for(int i = 0; i < threadNumber; i++) {
            executor.execute(RunnableManager.createAbusiveThread(pool));
        }
    }

    private void initializeWaitingThreads(ConnectionPool pool){
        for(int i = 0; i < 2; i++) {
            executor.execute(RunnableManager.createWaitingThread(pool));
        }
    }

    public void testPool(ConnectionPool pool) throws InterruptedException {
        initializeAbusiveThreads(pool);
        Thread.sleep(1000);
        initializeWaitingThreads(pool);

        ((ExecutorService)executor).shutdown();
    }
}
