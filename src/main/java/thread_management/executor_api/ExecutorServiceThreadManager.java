package thread_management.executor_api;

import connection_pool.ConnectionPool;
import thread_management.task_provider.CallableService;
import thread_management.ThreadManager;

import java.util.concurrent.*;

public class ExecutorServiceThreadManager implements ThreadManager {
    private ExecutorService executor;
    private ConcurrentHashMap<Integer, Future<Boolean>> threadFutures;
    private ConcurrentHashMap<Integer, Boolean> successfulTries;
    private int threadNumber;

    public ExecutorServiceThreadManager(int threadNumber){
        this.threadNumber = threadNumber;
        threadFutures = new ConcurrentHashMap<>();
        successfulTries = new ConcurrentHashMap<>();
        executor = Executors.newFixedThreadPool(threadNumber + 2);
    }

    private void initializeAbusiveThreads(ConnectionPool pool) throws InterruptedException {
        //Creates threads that are busy
        for(int i = 0; i < threadNumber; i++) {
            threadFutures.put(i, executor.submit(CallableService.createAbusiveThread(pool)));
        }
    }

    private void initializeWaitingThreads(ConnectionPool pool){
        for(int i = 0; i < 2; i++) {
            threadFutures.put(
                    threadNumber + i,
                    executor.submit(CallableService.createWaitingThread(pool)));
        }
    }

    private void waitThreads() {
        try {
            boolean threadOne = threadFutures.get(threadNumber).get();
            boolean threadTwo = threadFutures.get(threadNumber + 1).get();

            if(threadOne && threadTwo) {
                System.out.println("All threads have been released!");
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void testPool(ConnectionPool pool) throws InterruptedException {
        initializeAbusiveThreads(pool);
        Thread.sleep(100);
        initializeWaitingThreads(pool);

        long start = System.nanoTime() / 1000000;
        waitThreads();
        long end = System.nanoTime() / 1000000;

        System.out.printf("The thread was blocked for %d miliseconds\n", end - start);
        executor.close();
    }
}
