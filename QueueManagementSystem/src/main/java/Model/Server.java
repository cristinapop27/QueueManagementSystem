package Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private final BlockingQueue<Client> clients;
    private AtomicInteger waitingPeriod;
    private volatile boolean running;
    private Client currentClient = null;
    public Server() {
        this.clients = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
        this.running = true;
    }

    public void addClient(Client client) {
        clients.add(client);
        waitingPeriod.addAndGet(client.getServiceTime());
    }

    public void stop(){
        running=false;
    }

    public String getFullQueueString() {
        StringBuilder sb = new StringBuilder();
        if (currentClient != null) {
            sb.append("(")
                    .append(currentClient.getID()).append(",")
                    .append(currentClient.getArrivalTime()).append(",")
                    .append(currentClient.getServiceTime()).append("); ");
        }

        for (Client c : clients) {
            sb.append("(")
                    .append(c.getID()).append(",")
                    .append(c.getArrivalTime()).append(",")
                    .append(c.getServiceTime()).append("); ");
        }

        return sb.toString().trim();
    }
    public int getTotalQueueSize() {
        int size = clients.size();
        if (currentClient != null) {
            size++;
        }
        return size;
    }

    public void run(){
        while(running || !clients.isEmpty()){
            try{
                //Client client = clients.take();
                Client client = clients.poll(1, java.util.concurrent.TimeUnit.SECONDS);

                if(client != null){
                    currentClient = client;

                    Thread.sleep(client.getServiceTime()*1000L);

                    waitingPeriod.addAndGet(-client.getServiceTime());
                    currentClient = null;
                }
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }

    public Client getCurrentClient() {
        return currentClient;
    }

    public int getQueueSize() {
        return clients.size();
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }
}
