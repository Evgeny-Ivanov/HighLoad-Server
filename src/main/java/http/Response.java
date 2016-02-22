package http;


import helpers.FileSystem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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

    String server = "myServer";//Информация о сервере
    Long contentLength = null;//Размер страницы в байтах
    String contentType = null;//Тип MIME страницы
    String date = null;//Дата и время отправки сообщения
    String connection = null;

    public Response(Request request){
        if(request.getPathFile() != null)
            fileSystem = new FileSystem(request.getPathFile());
        if(fileSystem != null)
            contentLength = fileSystem.fileSize();
        date = getServerTime();
    }

    public void writeResponse(OutputStream out){
        writeHeaders(out);
        try {
            BufferedReader reader = fileSystem.getFile();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    private static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    private void writeHeaders(OutputStream out){
    }
}


