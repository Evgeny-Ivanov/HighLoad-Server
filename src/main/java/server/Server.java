package server;

import helpers.PrintWriter;
import http.Request;
import http.Response;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

/**
 * Created by stalker on 02.03.16.
 */
public class Server extends RecursiveAction {
    Selector selector;
    PrintWriter printWriter;
    public Server() {
        try {
            selector = Selector.open();
            printWriter = new PrintWriter();
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void register(Socket socket) throws IOException {
        SocketChannel channel = socket.getChannel();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }

    @Override
    protected void compute(){
        while (true){
            try {
                int count = selector.selectNow();
                if (count == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while(keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        printWriter.read(key);
                    }
                    if(key.isWritable()){
                        printWriter.write(key);
                    }

                }
                keyIterator.remove();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
