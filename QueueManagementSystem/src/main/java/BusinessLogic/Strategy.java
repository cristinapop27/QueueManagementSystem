package BusinessLogic;

import Model.Client;
import Model.Server;

import java.util.List;

public interface Strategy {

    public Server getCurrentClientServer();

    public void addTask(List<Server> servers, Client client);

    public enum SelectionPolicy{
        SHORTEST_QUEUE, SHORTEST_TIME
    }
}

