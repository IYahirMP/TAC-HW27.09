package thread_management.executor_api;

import connection_pool.ConnectionPool;
import thread_management.task_provider.RunnableService;
import thread_management.ThreadManager;

import java.util.concurrent.*;

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
            executor.execute(RunnableService.createAbusiveThread(pool));
        }
    }

    private void initializeWaitingThreads(ConnectionPool pool){
        for(int i = 0; i < 2; i++) {
            executor.execute(RunnableService.createWaitingThread(pool));
        }
    }

    public void testPool(ConnectionPool pool) throws InterruptedException {
        initializeAbusiveThreads(pool);
        Thread.sleep(1000);
        initializeWaitingThreads(pool);

        ((ExecutorService)executor).shutdown();
    }
}
