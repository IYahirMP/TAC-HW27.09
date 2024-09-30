package thread_management.task_provider;

import connection_pool.Connection;
import connection_pool.ConnectionPool;

import java.util.NoSuchElementException;
import java.lang.Boolean;
import java.util.function.Supplier;

public class SupplierService {

    public static Supplier<Boolean> createAbusiveThread(ConnectionPool pool){
        Supplier<Boolean> newSupplier = () -> {
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

        return newSupplier;
    }

    public static Supplier<Boolean> createWaitingThread(ConnectionPool pool){
        Supplier<Boolean> newSupplier = () -> {
            while (true) {
                try {
                    Connection newConnection = pool.borrow();
                    System.out.printf("%s: Connection could be finally acquired!\n", Thread.currentThread().getName());
                    pool.release(newConnection);
                    System.out.printf("%s: Connection has been released\n", Thread.currentThread().getName());
                    return true;
                } catch (NoSuchElementException e) {
                    System.out.printf("%s: Connection could not be retrieved!\n", Thread.currentThread().getName());
                    System.out.printf("%s: Waiting one second to retry...\n", Thread.currentThread().getName());
                    waitConnection(2500);
                }
            }
        };
        return newSupplier;
    }

    private static void waitConnection(int time){
        try{
            Thread.sleep(time);
        }catch(InterruptedException e2){
            System.out.println("Thread interrupted!");
        }
    }
}
