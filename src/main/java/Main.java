import helpers.*;
import helpers.stolen.CommandLineParser;
import server.Server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by stalker on 21.02.16.
 */
public class Main {
    private static final int PORT = 80;
    private static final int QUEUE = 100;
    private static final int POOL_SIZE = 3000;

    public static void main(String[] args) throws IOException {
        CommandLineParser parser = new CommandLineParser(args);
        String ROOTDIR = parser.getValue("r");
        int NCPU = Integer.parseInt(parser.getValue("c"));

        FileSystem.DOCUMENT_ROOT = ROOTDIR;

        System.out.println((new StringBuilder())
                .append("Server start").append('\n')
                .append("ROOTDIR: ").append(ROOTDIR).append('\n')
                .append("NCPU: ").append(NCPU).append('\n')
                .toString() );

        ForkJoinPool pool = new ForkJoinPool(POOL_SIZE);

        Selector selector = Selector.open();//выполнен на основе epoll
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//конфигурируем его как nonblocking
        ssc.register(selector, SelectionKey.OP_ACCEPT);//SelectionKey.OP_ACCEPT сообщает Selector'у, что мы хотим ожидать только входящие соединения, а не обычные данные
        ServerSocket serverSocket = ssc.socket();
        serverSocket.bind(new InetSocketAddress(PORT));


        while (true) {
            System.out.print("hello");
            int num = selector.select();
            if (num == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    Socket s = serverSocket.accept();
                    SocketChannel channel = s.getChannel();
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                } else if (key.isWritable()) {
                    pool.execute(new Server(SelectionKey.OP_WRITE, key));
                } else if (key.isReadable()) {
                    pool.execute(new Server(SelectionKey.OP_READ, key));
                }


            }
            keyIterator.remove();

        }

    }
}
