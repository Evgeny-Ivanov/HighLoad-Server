import helpers.*;
import helpers.stolen.CommandLineParser;
import server.AddressService;
import server.Server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by stalker on 21.02.16.
 */
public class Main {
    private static final int PORT = 80;
    private static final int QUEUE = 100;
    private static final int POOL_SIZE = 3;

    public static void main(String[] args) throws IOException{
        CommandLineParser parser = new CommandLineParser(args);
        String ROOTDIR = parser.getValue("r");
        int NCPU = Integer.parseInt(parser.getValue("c"));

        FileSystem.DOCUMENT_ROOT = ROOTDIR;

        System.out.println((new StringBuilder())
                .append("Server start").append('\n')
                .append("ROOTDIR: ").append(ROOTDIR).append('\n')
                .append("NCPU: ").append(NCPU).append('\n')
                .toString() );

        AddressService addressService = new AddressService(POOL_SIZE);
        for (int i=0;i<POOL_SIZE;i++){
            Server server = new Server();
            addressService.addServer(server);
        }

        Selector selector = Selector.open();//выполнен на основе epoll
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking( false );//конфигурируем его как nonblocking
        ssc.register( selector, SelectionKey.OP_ACCEPT );//SelectionKey.OP_ACCEPT сообщает Selector'у, что мы хотим ожидать только входящие соединения, а не обычные данные
        ServerSocket serverSocket = ssc.socket();
        serverSocket.bind(new InetSocketAddress(PORT));


        while (true) {
            int num = selector.select();
            if (num == 0) {
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            for(SelectionKey key : keys) {
                if (key.isAcceptable()) {
                    Socket s = serverSocket.accept();
                    addressService.registerSocket(s);
                }
            }
            keys.clear();
        }
    }

}
