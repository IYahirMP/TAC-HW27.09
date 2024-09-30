package thread_management.future_api;
import connection_pool.ConnectionPool;
import thread_management.task_provider.SupplierService;
import thread_management.ThreadManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureManager implements ThreadManager {
    private volatile int threadNumber;
    private volatile ConcurrentHashMap<Integer, CompletableFuture<Boolean>> cfs;
    private ExecutorService executor;

    public CompletableFutureManager(int threadNumber) {
        this.threadNumber = threadNumber;
        cfs = new ConcurrentHashMap<>();
        executor = Executors.newFixedThreadPool(threadNumber + 2);
    }

    private void initializeAbusiveThreads(ConnectionPool pool){
        //Creates threads that are busy
        for(int i = 0; i < threadNumber; i++) {
            cfs.put(i,
                    CompletableFuture.supplyAsync(
                            SupplierService.createAbusiveThread(pool),
                            executor
                    )
            );
        }
    }

    private void initializeWaitingThreads(ConnectionPool pool){
        for(int i = 0; i < 2; i++) {
            cfs.put(threadNumber + i,
                    CompletableFuture.supplyAsync(
                            SupplierService.createWaitingThread(pool),
                            executor
                    )
            );
        }
    }

    private void waitThreads() {
    }

    public void testPool(ConnectionPool pool) throws InterruptedException {
        initializeAbusiveThreads(pool);
        Thread.sleep(300);

        initializeWaitingThreads(pool);
        Thread.sleep(1000);

        for(int i = 0; i < threadNumber; i++) {
            CompletableFuture<Boolean> cf = cfs.get(i);
            int tmp = i;
            cf.thenAccept((result) -> {
                if (tmp == 0) {
                    System.out.println("Abusive threads terminated in the following order:");
                }
                System.out.println("Thread " + tmp + ": " + result);
            });
        }

        executor.shutdown();
    }
}
