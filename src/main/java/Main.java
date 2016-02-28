import helpers.*;
import helpers.stolen.CommandLineParser;

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
import java.util.concurrent.ForkJoinPool;

/**
 * Created by stalker on 21.02.16.
 */
public class Main {
    private static final int PORT = 8076;
    private static final int QUEUE = 100;
    private static final int POOL_SIZE = 1000;

    public static void main(String[] args) throws IOException{
        //CommandLineParser parser = new CommandLineParser(args);
        //String ROOTDIR = parser.getValue("r");
        //int NCPU = Integer.parseInt(parser.getValue("c"));

        //FileSystem.DOCUMENT_ROOT = ROOTDIR;

        //ForkJoinPool pool = new ForkJoinPool(POOL_SIZE);
        //System.out.println((new StringBuilder())
        //        .append("Server start").append('\n')
        //        .append("ROOTDIR: ").append(ROOTDIR).append('\n')
        //        .append("NCPU: ").append(NCPU).append('\n')
        //        .toString());

        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking( false );//конфигурируем его как nonblocking
        ssc.register( selector, SelectionKey.OP_ACCEPT );//SelectionKey.OP_ACCEPT сообщает Selector'у, что мы хотим ожидать только входящие соединения, а не обычные данные
        ServerSocket serverSocket = ssc.socket();
        serverSocket.bind(new InetSocketAddress(PORT));

        while (true) {
            // Проверяем, если ли какие-либо активности -
            // входящие соединения или входящие данные в
            // существующем соединении.
            int num = selector.select();

            // Если никаких активностей нет, выходим из цикла
            // и снова ждём.
            if (num == 0) {
                continue;
            }

            // Получим ключи, соответствующие активности,
            // которые могут быть распознаны и обработаны один за другим.
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                // Получим ключ, представляющий один из битов
                // активности ввода/вывода.
                SelectionKey key = (SelectionKey)it.next();

                // ... работаем с SelectionKey ...
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) ==
                        SelectionKey.OP_ACCEPT) {
                    // Принимаем входящее соединение
                    Socket s = serverSocket.accept();

                    // Необходимо сделать его неблокирующим,
                    // чтобы использовать Selector для него.
                    SocketChannel sc = s.getChannel();
                    sc.configureBlocking( false );

                    // Регистрируем его в Selector для чтения.
                    sc.register( selector, SelectionKey.OP_READ );
                } else if ((key.readyOps() & SelectionKey.OP_READ) ==
                        SelectionKey.OP_READ) {
                    SocketChannel sc = (SocketChannel)key.channel();
                    processInput( sc );
                }
            }
            // Удаляем выбранные ключи, поскольку уже отработали с ними.
            keys.clear();
        }

    }

    public static void processInput(SocketChannel channel){
        ByteBuffer buf = ByteBuffer.allocateDirect(10);
        int num = 0;
        try {
            num = channel.read(buf);
        }catch (IOException e){
            e.printStackTrace();
        }
        buf.rewind();//ставим позицию на 0
        //System.out.print(new String(buf.array()));
        for(int i=0;i<num;i++){
            System.out.print((char)buf.get());
        }
        System.out.println();
    }
}
