package GUI;

import BusinessLogic.SimulationManager;
import BusinessLogic.Strategy;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class SimulationFrame extends JFrame {
    private JTextField clientsField = new JTextField(5);
    private JTextField queuesField = new JTextField(5);
    private JTextField simTimeField = new JTextField(5);
    private JTextField minArrivalField = new JTextField(5);
    private JTextField maxArrivalField = new JTextField(5);
    private JTextField minServiceField = new JTextField(5);
    private JTextField maxServiceField = new JTextField(5);
    private JTextArea logArea = new JTextArea(20, 50);
    private JButton saveLogButton = new JButton("Save Log to File");
    private JComboBox<String> strategyBox = new JComboBox<>(new String[]{"SHORTEST_QUEUE", "SHORTEST_TIME"});

    public SimulationFrame() {
        setTitle("Queue Simulation");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Settings"));

        inputPanel.add(new JLabel("Number of clients (N):"));
        inputPanel.add(clientsField);
        inputPanel.add(new JLabel("Number of queues (Q):"));
        inputPanel.add(queuesField);
        inputPanel.add(new JLabel("Simulation time (tMAX):"));
        inputPanel.add(simTimeField);
        inputPanel.add(new JLabel("Min arrival time:"));
        inputPanel.add(minArrivalField);
        inputPanel.add(new JLabel("Max arrival time:"));
        inputPanel.add(maxArrivalField);
        inputPanel.add(new JLabel("Min service time:"));
        inputPanel.add(minServiceField);
        inputPanel.add(new JLabel("Max service time:"));
        inputPanel.add(maxServiceField);
        inputPanel.add(new JLabel("Selection strategy:"));
        inputPanel.add(strategyBox);

        JButton startButton = new JButton("Start Simulation");
        startButton.addActionListener(e -> startSimulation());
        saveLogButton.addActionListener(e -> saveLogToFile());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(saveLogButton);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(10, 10));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Simulation Log"));

        getContentPane().setLayout(new BorderLayout(10, 10));
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void saveLogToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Simulation Log");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();
                if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));
                writer.write(logArea.getText());
                writer.close();
                JOptionPane.showMessageDialog(this, "Log saved successfully to:\n" + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving log: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void startSimulation() {
        try {
            int N = Integer.parseInt(clientsField.getText());
            int Q = Integer.parseInt(queuesField.getText());
            int tMax = Integer.parseInt(simTimeField.getText());
            int minArrival = Integer.parseInt(minArrivalField.getText());
            int maxArrival = Integer.parseInt(maxArrivalField.getText());
            int minService = Integer.parseInt(minServiceField.getText());
            int maxService = Integer.parseInt(maxServiceField.getText());
            String selectedStrategy = (String) strategyBox.getSelectedItem();
            Strategy.SelectionPolicy policy = Strategy.SelectionPolicy.valueOf(selectedStrategy);

            logArea.setText("");

            SimulationManager sim = SimulationManager.getInstance(
                    N, Q, tMax, minArrival, maxArrival, minService, maxService, logArea, policy
            );

            new Thread(sim).start();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers for all fields.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulationFrame().setVisible(true));
    }
}
