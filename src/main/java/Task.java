import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.RecursiveAction;

/**
 * Created by stalker on 21.02.16.
 */
public class Task extends RecursiveAction {
    Socket socket;
    InputStream in;
    OutputStream out;
    String response = "OK";

    public Task(Socket socket) throws IOException{
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    @Override
    protected void compute(){
        try {
            byte[] buf = response.getBytes();
            StringBuilder message = new StringBuilder();
            int c;
            while ((c = in.read()) != -1) {
                System.out.print((char)c);
                message.append((char) c);
            }
            out.write(buf);
            System.out.println(message.toString());
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
