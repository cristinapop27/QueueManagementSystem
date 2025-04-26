package BusinessLogic;

import Model.Client;
import Model.Server;

import javax.swing.*;
import java.util.*;
import java.io.*;

public class SimulationManager implements Runnable {
    private final JTextArea logArea;
    public int timeLimit = 200;
    public int maxProcessingTime = 9;
    public int minProcessingTime = 3;
    public int maxArrivalTime = 100;
    public int minArrivalTime = 10;
    public int nrOfServers = 20;
    public int nrOfClients = 1000;
    private int totalWaitingTime = 0;
    private int totalServiceTime = 0;

    private Map<Integer, Integer> peakLoadMap = new HashMap<>();

    public Strategy.SelectionPolicy selectionPolicy = Strategy.SelectionPolicy.SHORTEST_TIME;

    private Scheduler scheduler;
    private List<Client> clients = new ArrayList<>();

    private BufferedWriter logWriter = null;
    private static SimulationManager instance;

    private SimulationManager(int nrOfClients, int nrOfServers, int timeLimit,
                             int minArrivalTime, int maxArrivalTime,
                             int minProcessingTime, int maxProcessingTime,
                             JTextArea logArea, Strategy.SelectionPolicy selectionPolicy) {
        this.nrOfClients = nrOfClients;
        this.nrOfServers = nrOfServers;
        this.timeLimit = timeLimit;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minProcessingTime = minProcessingTime;
        this.maxProcessingTime = maxProcessingTime;
        this.logArea = logArea;
        this.selectionPolicy = selectionPolicy;

        generateRandomClients();
        scheduler = new Scheduler(nrOfServers, nrOfClients);
        scheduler.changeStrategy(selectionPolicy);

        if (logArea == null) {
            try {
                logWriter = new BufferedWriter(new FileWriter("log.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static SimulationManager getInstance(int nrOfClients, int nrOfServers, int timeLimit,
                                                int minArrivalTime, int maxArrivalTime,
                                                int minProcessingTime, int maxProcessingTime,
                                                JTextArea logArea, Strategy.SelectionPolicy selectionPolicy) {
        if (instance == null) {
            instance = new SimulationManager(nrOfClients, nrOfServers, timeLimit,
                    minArrivalTime, maxArrivalTime, minProcessingTime, maxProcessingTime,
                    logArea, selectionPolicy);
        }
        return instance;
    }
    public void generateRandomClients() {
        for (int i = 0; i < nrOfClients; i++) {
            int processTime = minProcessingTime + (int)(Math.random() * ((maxProcessingTime - minProcessingTime) + 1));
            int arrivalTime = minArrivalTime + (int)(Math.random() * ((maxArrivalTime - minArrivalTime) + 1));
            Client client = new Client(i,processTime,arrivalTime);
            clients.add(client);
        }
        clients.sort(Comparator.comparingInt(client -> client.getArrivalTime()));

    }

    @Override
    public void run() {
    int currentTime = 0;
    while(currentTime <= timeLimit) {

        Iterator<Client> it = clients.iterator();
        while (it.hasNext()) {
            Client client = it.next();
            if (client.getArrivalTime() == currentTime) {
                Server currentServer = scheduler.dispatchClient(client);
                int waitingTime = currentServer.getWaitingPeriod();

                totalWaitingTime += waitingTime;
                totalServiceTime += client.getServiceTime();

                it.remove();
            }
        }

        logLine("---- Simulation Time: " + currentTime + "s ----");
        logLine("Waiting clients: " + getWaitingClientsString(currentTime));

        List<Server> servers = scheduler.getServers();
        for (int i = 0; i < servers.size(); i++) {
            Server s = servers.get(i);
            String queueState = s.getQueueSize() == 0 && s.getCurrentClient() == null
                    ? "closed"
                    : s.getFullQueueString();
            logLine("Queue " + (i + 1) + ": " + queueState);
        }
        logLine("");
        int activeClients = 0;
        for (Server s : scheduler.getServers()) {
            activeClients += s.getQueueSize();
            if (s.getCurrentClient() != null) activeClients++;
        }
        peakLoadMap.put(currentTime, activeClients);

        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        currentTime++;
    }

    scheduler.stopAllServers();

    double averageWait =  (double) totalWaitingTime / nrOfClients;
    double averageService =(double) totalServiceTime / nrOfClients;

    int peakHour = -1;
    int maxLoad = -1;
    for (Map.Entry<Integer, Integer> entry : peakLoadMap.entrySet()) {
        if (entry.getValue() > maxLoad) {
            maxLoad = entry.getValue();
            peakHour = entry.getKey();
        }
    }

    logLine("--- Simulation Summary ---");
    logLine("Average waiting time: " + String.format("%.2f", averageWait));
    logLine("Average service time: " + String.format("%.2f", averageService));
    logLine("Peak hour (max clients in queues): " + peakHour + "s with " + maxLoad + " clients");

        if (logWriter != null) {
            try {
                logWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String getWaitingClientsString(int currentTime) {
        StringBuilder sb = new StringBuilder();
        for (Client c : clients) {
            if (c.getArrivalTime() >= currentTime) {
                sb.append("(")
                        .append(c.getID()).append(",")
                        .append(c.getArrivalTime()).append(",")
                        .append(c.getServiceTime()).append("); ");
            }
        }
        return sb.toString().trim();
    }

    private void logLine(String line) {
        try {
            if (logWriter != null) {
                //for txt without gui
                logWriter.write(line);
                logWriter.newLine();
                logWriter.flush();
            }

            if (logArea != null) {
                //for gui
                SwingUtilities.invokeLater(() -> logArea.append(line + "\n"));
            } else {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
