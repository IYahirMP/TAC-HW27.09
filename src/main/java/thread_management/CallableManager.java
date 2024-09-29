package thread_management;

import connection_pool.Connection;
import connection_pool.ConnectionPool;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.lang.Boolean;

public class CallableManager {

    public static Callable<Boolean> createAbusiveThread(ConnectionPool pool){
        Callable<Boolean> newCallable = () -> {
            try{
                Connection newConnection = pool.borrow();
                System.out.printf("%s: Borrowed a connection\n", Thread.currentThread().getName());
                System.out.printf("%s: Thread Waiting five seconds...\n", Thread.currentThread().getName());
                Thread.sleep(5000);
                pool.release(newConnection);
                return true;
            }
            catch(NoSuchElementException e){
                System.out.printf("An error has occurred in %s. Connection couldn't be borrowed.\n", Thread.currentThread().getName());
            }catch(InterruptedException e){
                System.out.printf("An error has occurred in %s. Thread interrupted.\n", Thread.currentThread().getName());
            }

            return false;
        };

        return newCallable;
    }

    public static Callable<Boolean> createWaitingThread(ConnectionPool pool){
        Callable<Boolean> newCallable = () -> {
            while (true) {
                try {
                    Connection newConnection = pool.borrow();
                    System.out.printf("%s: Connection could be finally acquired!\n", Thread.currentThread().getName());
                    pool.release(newConnection);
                    System.out.printf("%s: Connection has been released\n", Thread.currentThread().getName());
                    return true;
                } catch (NoSuchElementException e) {
                    System.out.println("Connection could not be retrieved!");
                    System.out.println("Waiting one second to retry...");
                    waitConnection(1000);
                }
            }
        };
        return newCallable;
    }

    private static void waitConnection(int time){
        try{
            Thread.sleep(time);
        }catch(InterruptedException e2){
            System.out.println("Thread interrupted!");
        }
    }
}
