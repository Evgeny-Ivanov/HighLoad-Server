package server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Created by stalker on 03.03.16.
 */
public class AddressService {
    private ForkJoinPool pool;
    private ArrayList<Server> servers = new ArrayList<>();
    private int currentIndex = 0;
    private int size;
    public AddressService(int size){
        this.size = size;
        pool = new ForkJoinPool(size);
    }
    public void addServer(Server server){
        servers.add(server);
        pool.execute(server);
    }
    public void registerSocket(Socket socket){
        try {
            servers.get(currentIndex).register(socket);
            System.out.println("Register new socket in server " + currentIndex);
            currentIndex++;
            if(currentIndex == size){
                currentIndex = 0;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
