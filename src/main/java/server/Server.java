package server;

import helpers.PrintWriter;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.concurrent.RecursiveAction;

/**
 * Created by stalker on 02.03.16.
 */
public class Server extends RecursiveAction {
    private int task;
    private SelectionKey key;
    public Server(int task,SelectionKey key) {
        this.task = task;
        this.key = key;
    }

    @Override
    protected void compute(){
        PrintWriter printWriter = new PrintWriter();

        if(task == SelectionKey.OP_READ){
            printWriter.read(key);
        }

        if(task == SelectionKey.OP_WRITE){
            try {
                printWriter.write(key);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
