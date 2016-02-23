import helpers.*;
import helpers.stolen.CommandLineParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by stalker on 21.02.16.
 */
public class Main {
    private static final int PORT = 80;
    private static final int QUEUE = 100;
    private static final int POOL_SIZE = 1000;

    public static void main(String[] args) throws IOException{
        CommandLineParser parser = new CommandLineParser(args);
        String ROOTDIR = parser.getValue("r");
        int NCPU = Integer.parseInt(parser.getValue("c"));

        FileSystem.DOCUMENT_ROOT = ROOTDIR;
        ServerSocket serverSocket = new ServerSocket(PORT,QUEUE);
        ForkJoinPool pool = new ForkJoinPool(POOL_SIZE);
        System.out.println((new StringBuilder())
                .append("Server start").append('\n')
                .append("ROOTDIR: ").append(ROOTDIR).append('\n')
                .append("NCPU: ").append(NCPU).append('\n')
                .toString());

        while(true){
            Socket socket = serverSocket.accept();
            pool.execute(new Task(socket));
        }

    }
}
