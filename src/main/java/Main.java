import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by stalker on 21.02.16.
 */
public class Main {
    private static final int PORT = 8123;
    private static final int QUEUE = 100;
    private static final int POOL_SIZE = 1000;
    private static final int TIME_SLEEP = 3000;

    public static void main(String[] args) throws IOException{

        ServerSocket serverSocket = new ServerSocket(PORT,QUEUE);
        ForkJoinPool pool = new ForkJoinPool(POOL_SIZE);

        while(true){
            Socket socket = serverSocket.accept();
            pool.execute(new Task(socket));
            try {
                Thread.sleep(TIME_SLEEP);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }
}
