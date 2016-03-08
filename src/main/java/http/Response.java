package http;

import helpers.FileSystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by stalker on 22.02.16.
 */
public class Response {
    private FileSystem fileSystem = null;

    String server = null;//Информация о сервере
    Long contentLength = null;//Размер страницы в байтах
    String contentType = null;//Тип MIME страницы
    String date = null;//Дата и время отправки сообщения
    String connection = null;

    private static final String HTTP_VERSION = "HTTP/1.1";
    private String status = "200 OK";
    private Request request;

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
    }

    public void writeResponse(OutputStream out) throws IOException{
        writeHeaders(out);
        if(request.getMethod().equals("GET") && fileSystem.isFileExists()) {
            giveFile(out);
        }
    }

    private static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    private void writeHeaders(OutputStream out) throws IOException{
        StringBuilder buf = new StringBuilder();
        buf.append(HTTP_VERSION).append(' ').append(status).append("\r\n");
        out.write(buf.toString().getBytes());
        if(!isSupports()) return;

        if(contentLength != null){
            buf.setLength(0);
            buf.append("Content-Length:").append(' ').append(contentLength).append("\r\n");
            out.write(buf.toString().getBytes());
        }
        if(contentType != null){
            buf.setLength(0);
            buf.append("Content-Type:").append(' ').append(contentType).append("\r\n");
            out.write(buf.toString().getBytes());
        }
        if(date != null){
            buf.setLength(0);
            buf.append("Date:").append(' ').append(date).append("\r\n");
            out.write(buf.toString().getBytes());
        }
        if(server != null){
            buf.setLength(0);
            buf.append("Server:").append(' ').append(server).append("\r\n");
            out.write(buf.toString().getBytes());
        }
        if(connection != null){
            buf.setLength(0);
            buf.append("Connection:").append(' ').append(connection);
            out.write(buf.toString().getBytes());
        }
        out.write("\r\n\r\n".getBytes());
    }

    private boolean isSupports(){
        String method = request.getMethod();
        return method.equals("GET") ||
               method.equals("HEAD");

    }

    private void giveFile(OutputStream out) throws IOException{
        try {
            BufferedReader reader = fileSystem.getFile();
            String buf;
            while ((buf = reader.readLine()) != null) {
                out.write((buf + '\n').getBytes());
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
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


