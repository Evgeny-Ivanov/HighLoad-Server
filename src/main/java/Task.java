
import http.Request;
import http.Response;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.RecursiveAction;

/**
 * Created by stalker on 21.02.16.
 */
public class Task extends RecursiveAction {
    Socket socket;
    InputStream in;
    OutputStream out;
    BufferedReader br;
    BufferedWriter writer;

    public Task(Socket socket) throws IOException{
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
        br = new BufferedReader(new InputStreamReader(in));
        writer = new BufferedWriter(new OutputStreamWriter(out));
    }

    @Override
    protected void compute(){
        try {
            StringBuilder message = new StringBuilder();
            Request request = new Request();
            while (true) {
                String buf = br.readLine();
                if(buf == null || buf.trim().isEmpty()){
                    break;
                }
                System.out.println(buf);
                message.append(buf);
                request.newHeader(buf);
            }
            Response response = new Response(request);
            response.writeResponse(out);
            socket.close();
            System.out.println(message.toString());

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
