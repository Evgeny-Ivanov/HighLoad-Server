package helpers;

import http.Request;
import http.Response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by stalker on 03.03.16.
 */
public class PrintWriter {
    private int memory = 4096;
    public void read(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buf = ByteBuffer.allocateDirect(memory);
            int num = 0;
            if ((num = channel.read(buf)) < 0) {

            } else {
                buf.flip();
                StringBuilder builder = (StringBuilder) key.attachment();
                if (builder == null) builder = new StringBuilder();
                builder.append(Charset.defaultCharset().decode(buf));
                System.out.println(builder.toString());
                key.attach(builder);
                buf.clear();
                //key.interestOps(SelectionKey.OP_READ);

                Request request = new Request();
                request.addHeaders(key.attachment().toString());
                Response response = new Response(request);
                key.attach(response);

                key.interestOps(SelectionKey.OP_WRITE);
            }
        }catch (IOException e ){
            e.printStackTrace();
        }
    }

    public void write(SelectionKey key) throws IOException{
            Response response = (Response)key.attachment();
            response.writeResponse(key);
    }

}
