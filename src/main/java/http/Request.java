package http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by stalker on 22.02.16.
 */
public class Request {
    private String pathFile = null;
    private String method = null;

    public Request(){

    }

    public void newHeader(String line){
        String nameHeader = getNameHeader(line);
        if(nameHeader != null){
            if(nameHeader.equals("GET")) method = "GET";
            if(nameHeader.equals("HEAD")) method = "HEAD";
            if(nameHeader.equals("POST")) method = "POST";
            if(isMethod(nameHeader)) parseMethod(line);
            //...
        }
    }

    public String getNameHeader(String line){
        int index = line.indexOf(' ');
        if(index != -1){
            return line.substring(0,index);
        }
        return null;
    }

    public void parseMethod(String line){

        int indexSpace1 = line.indexOf(' ');
        String buf = line.substring(indexSpace1+1);
        int indexSpace2 = buf.indexOf(' ');
        pathFile = buf.substring(0,indexSpace2);

        try {
            pathFile = URLDecoder.decode(pathFile, "UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        int index2 = pathFile.indexOf('?');
        if(index2 != -1)
            pathFile = pathFile.substring(0,index2);

        int index3 = pathFile.indexOf('#');
        if(index3 != -1)
            pathFile = pathFile.substring(0,index3);

        if(pathFile.contains("../"))
            pathFile = null;
    }

    public String getPathFile(){
        return pathFile;
    }

    public String getMethod(){
        return method;
    }

    private boolean isMethod(String nameHeader){
        return nameHeader.equals("GET") ||
               nameHeader.equals("HEAD") ||
               nameHeader.equals("POST");
    }

}
