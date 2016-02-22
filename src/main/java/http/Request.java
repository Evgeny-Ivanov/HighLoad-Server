package http;

/**
 * Created by stalker on 22.02.16.
 */
public class Request {
    private String pathFile = null;

    public Request(){

    }

    public void newHeader(String line){
        String nameHeader = getNameHeader(line);
        if(nameHeader != null){
            if(nameHeader.equals("GET") || nameHeader.equals("HEAD")){
                parseMethod(line);
            }
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
        String[] array = line.split(" ");
        pathFile = array[1];
    }

    public String getPathFile(){
        return pathFile;
    }

}
