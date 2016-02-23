package helpers;

import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * Created by stalker on 22.02.16.
 */
public class FileSystem {
    public static final String INDEX_DIR = "/index.html";
    public static final String DOCUMENT_ROOT = "/home/stalker/myproject1/frontend-stub-1/public_html/";
    private File file;

    public FileSystem(String path){
        file = new File(DOCUMENT_ROOT + path);
        if(file.isDirectory()){
            StringBuilder builder = new StringBuilder();
            builder.append(DOCUMENT_ROOT).append(path).append(INDEX_DIR);
            file = new File(builder.toString());
        }
    }

    public BufferedReader getFile() throws FileNotFoundException{
        FileInputStream in = new FileInputStream(file.getAbsoluteFile());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return reader;
    }

    public boolean isFileExists(){
        if(file.exists()){
            return true;
        }
        return false;
    }

    public long fileSize(){
        return file.length();
    }

    @Nullable
    public String getContentType(){
        String path = file.getAbsolutePath();
        int index = path.lastIndexOf('.');
        if(index == -1) {
            return null;
        }
        String contentType = path.substring(index+1);
        String result = null;
        if(contentType.equals("html")){
            result = "text/html";
        }
        if(contentType.equals("css")){
            result = "text/css";
        }
        if(contentType.equals("js")){
            result = "text/javascript";
        }
        if(contentType.equals("jpg")){
            result = "image/jpeg";
        }
        if(contentType.equals("jpeg")){
            result = "image/jpeg";
        }
        if(contentType.equals("png")){
            result = "image/png";
        }
        if(contentType.equals("gif")){
            result = "image/gif";
        }
        if(contentType.equals("swf")){
            result = "application/x-shockwave-flash";
        }
        return result;
    }

}
