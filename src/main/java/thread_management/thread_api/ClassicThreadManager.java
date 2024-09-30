package thread_management.thread_api;

import java.lang.Thread;
import java.util.ArrayList;

import connection_pool.ConnectionPool;
import thread_management.task_provider.RunnableService;
import thread_management.ThreadManager;

public class ClassicThreadManager implements ThreadManager {
    private ArrayList<Thread> threads;
    private int threadNumber = 0;

    public ClassicThreadManager(int threadNumber){
        this.threadNumber = threadNumber;
        threads = new ArrayList<>();
    }

    private void initializeAbusiveThreads(ConnectionPool pool){
        //Creates threads that are busy
        for(int i = 0; i < threadNumber; i++) {
            Thread temp = new Thread(RunnableService.createAbusiveThread(pool));
            threads.add(temp);
            threads.getLast().start();
        }
    }

    private void initializeWaitingThreads(ConnectionPool pool){
        for(int i = 0; i < 2; i++) {
            Thread temp = new Thread(RunnableService.createWaitingThread(pool));
            threads.add(temp);
            threads.getLast().start();
        }
    }


    public void testPool(ConnectionPool pool) throws InterruptedException{
        initializeAbusiveThreads(pool);
        Thread.sleep(1000);
        initializeWaitingThreads(pool);
    }
}
