package BusinessLogic;

import Model.Client;
import Model.Server;

import java.util.List;

public class TimeStrategy implements Strategy {
    private Server currentClientServer = null;
    @Override
    public void addTask(List<Server> servers, Client client){
        Server minTimeServer = servers.get(0);
        int minTime = minTimeServer.getWaitingPeriod();
        for (Server server : servers) {
            if (server.getWaitingPeriod() < minTime) {
                minTime = server.getWaitingPeriod();
                minTimeServer = server;
            }
        }
        minTimeServer.addClient(client);
        currentClientServer = minTimeServer;
    }

    public Server getCurrentClientServer() {
        return currentClientServer;
    }
}
