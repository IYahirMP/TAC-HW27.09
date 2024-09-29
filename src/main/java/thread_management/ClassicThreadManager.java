package thread_management;

import connection_pool.Connection;
import java.lang.Thread;
import java.lang.Runnable;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import connection_pool.ConnectionPool;

public class ClassicThreadManager {
    private ArrayList<Thread> threads;
    private int threadNumber = 0;

    public ClassicThreadManager(int threadNumber){
        this.threadNumber = threadNumber;
        threads = new ArrayList<>();
    }

    private Runnable createAbusiveThread(ConnectionPool pool){
        Runnable newRunnable = () -> {
            try{
                Connection newConnection = pool.borrow();
                System.out.printf("%s: Borrowed a connection\n", Thread.currentThread().getName());
                System.out.printf("%s: Thread Waiting five seconds...\n", Thread.currentThread().getName());
                Thread.sleep(5000);
                pool.release(newConnection);
            }
            catch(NoSuchElementException e){
                System.out.printf("An error has occurred in %s. Connection couldn't be borrowed.\n", Thread.currentThread().getName());
            }catch(InterruptedException e){
                System.out.printf("An error has occurred in %s. Thread interrupted.\n", Thread.currentThread().getName());
            }
        };

        return newRunnable;
    }

    private Runnable createWaitingThread(ConnectionPool pool){
        Runnable newRunnable = new Thread(() -> {
            while (true) {
                try {
                    Connection newConnection = pool.borrow();
                    System.out.printf("%s: Connection could be finally acquired!\n", Thread.currentThread().getName());
                    pool.release(newConnection);
                    break;
                } catch (NoSuchElementException e) {
                    System.out.println("Connection could not be retrieved!");
                    System.out.println("Waiting one second to retry...");
                    waitConnection(1000);
                }


            }
        });

        return newRunnable;
    }

    public void waitConnection(int time){
        try{
            Thread.sleep(time);
        }catch(InterruptedException e2){
            System.out.println("Thread interrupted!");
        }
    }

    public void initializeAbusiveThreads(ConnectionPool pool){
        //Creates threads that are busy
        for(int i = 0; i < threadNumber; i++) {
            Thread temp = new Thread(createAbusiveThread(pool));
            threads.add(temp);
            threads.getLast().start();
        }
    }

    public void initializeWaitingThreads(ConnectionPool pool){
        for(int i = 0; i < 2; i++) {
            Thread temp = new Thread(createWaitingThread(pool));
            threads.add(temp);
            threads.getLast().start();
        }
    }


    public void someFunction(ConnectionPool pool) throws InterruptedException{
        initializeAbusiveThreads(pool);
        Thread.sleep(1000);
        initializeWaitingThreads(pool);
    }
}
