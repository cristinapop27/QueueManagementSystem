package BusinessLogic;

import Model.Client;
import Model.Server;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers = new ArrayList<Server>();
    private Strategy strategy;
    private List<Thread> serverThreads = new ArrayList<>();
    public Scheduler(int maxNoServers, int maxClientsPerServer) {
        for ( int i=0 ; i<maxNoServers ; i++) {
            Server newServer = new Server();
            servers.add(newServer);
            Thread t = new Thread(newServer);
            t.start();
            serverThreads.add(t);
        }
    }

    public void changeStrategy(Strategy.SelectionPolicy policy){
        if (policy == Strategy.SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ShortestQueueStrategy();
        }
        if (policy == Strategy.SelectionPolicy.SHORTEST_TIME){
            strategy = new TimeStrategy();
        }
    }
    public Server dispatchClient(Client client)
    {
        strategy.addTask(servers,client);
        return strategy.getCurrentClientServer();
    }

    public List<Server> getServers(){
        return servers;
    }

    public void stopAllServers() {
        for (Server s : servers) {
            s.stop();
        }
        for (Thread t : serverThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
