package BusinessLogic;

import Model.Client;
import Model.Server;

import java.util.List;

public class ShortestQueueStrategy implements Strategy{

    private Server currentClientServer = null;
    @Override
    public void addTask(List<Server> servers, Client client){
        Server min = servers.get(0);
        for(Server s : servers){
            if (s.getTotalQueueSize() < min.getTotalQueueSize())
            {
                min=s;
            }
        }
        min.addClient(client);
        currentClientServer = min;
    }

    @Override
    public Server getCurrentClientServer() {
        return currentClientServer;
    }
}
