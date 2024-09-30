package thread_management.task_provider;

import connection_pool.Connection;
import connection_pool.ConnectionPool;

import java.util.NoSuchElementException;

public class RunnableService {

    public static Runnable createAbusiveThread(ConnectionPool pool){
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

    public static Runnable createWaitingThread(ConnectionPool pool){
        Runnable newRunnable = () -> {
            while (true) {
                try {
                    Connection newConnection = pool.borrow();
                    System.out.printf("%s: Connection could be finally acquired!\n", Thread.currentThread().getName());
                    pool.release(newConnection);
                    System.out.printf("%s: Connection has been released\n", Thread.currentThread().getName());
                    break;
                } catch (NoSuchElementException e) {
                    System.out.println("Connection could not be retrieved!");
                    System.out.println("Waiting one second to retry...");
                    waitConnection(1000);
                }


            }
        };

        return newRunnable;
    }

    private static void waitConnection(int time){
        try{
            Thread.sleep(time);
        }catch(InterruptedException e2){
            System.out.println("Thread interrupted!");
        }
    }
}
