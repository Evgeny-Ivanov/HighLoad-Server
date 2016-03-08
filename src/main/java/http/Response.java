package http;

import helpers.FileSystem;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by stalker on 22.02.16.
 */
public class Response {
    private FileSystem fileSystem = null;
    private int memory = 4096;

    String server = null;//Информация о сервере
    Long contentLength = null;//Размер страницы в байтах
    String contentType = null;//Тип MIME страницы
    String date = null;//Дата и время отправки сообщения
    String connection = null;
    boolean isWriteHeadersComplet = false;

    private static final String HTTP_VERSION = "HTTP/1.1";
    private String status = "200 OK";
    private Request request;
    private String response;
    private ReadableByteChannel channel;
    FileChannel fileChannel = null;


    public Response(Request request){
        this.request = request;
        if(request.getPathFile() != null)
            fileSystem = new FileSystem(request.getPathFile());
        if(fileSystem != null) {
            contentLength = fileSystem.fileSize();
            contentType = fileSystem.getContentType();
        }
        setStatus(request);
        date = getServerTime();
        server = "myServer";
        connection = "close";

        response = prepareResponse();
        InputStream stream = new ByteArrayInputStream(response.getBytes());
        channel = Channels.newChannel(stream);
        try {//с этим надо поосторожнее
            fileChannel = fileSystem.getFile();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeResponse(SelectionKey key) throws IOException{
        if(!isWriteHeadersComplet) writeHeaders(key);
        if(request.getMethod().equals("GET") && fileSystem.isFileExists()) {
            giveFile(key);
        } else {
            SocketChannel socketChannel = (SocketChannel)key.channel();
            socketChannel.close();
        }
    }

    private static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public void writeHeaders(SelectionKey key) throws IOException{
        SocketChannel socketChannel = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(memory);
        if (channel.read(buffer) < 0) {
            isWriteHeadersComplet = true;
        } else {
            buffer.rewind();
            socketChannel.write(buffer);
        }
    }

    private String prepareResponse(){
        StringBuilder buf = new StringBuilder();
        buf.append(HTTP_VERSION).append(' ').append(status).append("\r\n");
        if(!isSupports()) return buf.toString();

        if(contentLength != null){
            buf.append("Content-Length:").append(' ').append(contentLength).append("\r\n");
        }
        if(contentType != null){
            buf.append("Content-Type:").append(' ').append(contentType).append("\r\n");
        }
        if(date != null){
            buf.append("Date:").append(' ').append(date).append("\r\n");
        }
        if(server != null){
            buf.append("Server:").append(' ').append(server).append("\r\n");
        }
        if(connection != null){
            buf.append("Connection:").append(' ').append(connection);
        }
        buf.append("\r\n\r\n");
        return buf.toString();
    }

    private boolean isSupports(){
        String method = request.getMethod();
        return method.equals("GET") ||
               method.equals("HEAD");

    }

    private void giveFile(SelectionKey key) throws IOException{
        SocketChannel socketChannel = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(memory);
        if(fileChannel.read(buffer) < 0){
            socketChannel.close();
            fileChannel.close();
        } else {
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }
    }

    private void setStatus(Request request){
        if(fileSystem != null && !fileSystem.canRead()){
            status = "403 Forbidden";
        }
        if(fileSystem == null || !fileSystem.isFileExists()) {
            status = "404 Not Found";
        }
        if(request.getMethod().equals("POST")){
            status = "405 Method Not Allowed";
        }
        if(fileSystem != null && fileSystem.isIndexDir() && !fileSystem.isFileExists()){
            status = "403 Forbidden";
        }

    }
}


