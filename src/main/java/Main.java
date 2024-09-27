import connection_pool.ConnectionPool;
import connection_pool.Connection;

public class Main {
    volatile static ConnectionPool pool = new ConnectionPool();

    public static void main(String[] args) throws InterruptedException {
        pool.initialize();

        Thread [] threads = new Thread[5];

        for(int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                try{
                    Connection newConnection = pool.borrow();
                    System.out.println("Thread Waiting two seconds...");
                    Thread.sleep(2000);
                    pool.release(newConnection);
                }
                catch(InterruptedException e){
                    System.out.println(e.getMessage());
                }
            });

            threads[i].start();
        }

        Thread.sleep(1000);

        for(int i = 0; i < 2; i++) {
            new Thread(() -> {
                while(true) {
                    try{
                        Connection newConnection = pool.borrow();

                        System.out.println("Connection could be acquired!");
                        pool.release(newConnection);
                        break;
                    }
                    catch(Exception e){
                        System.out.println("Connection could not be retrieved!");
                        try {
                            Thread.sleep(1000);
                        }catch(InterruptedException e1) {
                            System.out.println("Waiting one second to retry...");
                        }
                    }
                }

            }).start();
        }
    }
}
