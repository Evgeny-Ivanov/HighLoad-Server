package helpers;

import java.io.*;

/**
 * Created by stalker on 22.02.16.
 */
public class FileSystem {

    public static final String DOCUMENT_ROOT = "/home/stalker";
    private File file;

    public FileSystem(String path){
        file = new File(DOCUMENT_ROOT + path);
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

}
