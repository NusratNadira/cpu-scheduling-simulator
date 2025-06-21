 import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class CPUSchedulerUpdatedDesign {
    private JFrame frame;
    private JTextField processIDField, arrivalTimeField, burstTimeField, priorityField;
    private JTable inputTable, outputTable;
    private JTextField avgWaitingTimeField, avgTurnaroundTimeField;
    private JPanel ganttChartPanel;
    private JRadioButton fcfsButton, sjfPreemptiveButton, sjfNonPreemptiveButton, roundRobinButton, priorityPreemptiveButton, priorityNonPreemptiveButton;
    private ButtonGroup algorithmGroup;
    private DefaultTableModel inputTableModel, outputTableModel;

    private java.util.List<Process> processList = new ArrayList<>();
    private int timeQuantum = 2; // Default time quantum for Round Robin

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CPUSchedulerUpdatedDesign().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("CPU Scheduling Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLayout(null);

        JLabel processLabel = new JLabel("Process ID:");
        processLabel.setBounds(20, 20, 100, 20);
        frame.add(processLabel);

        processIDField = new JTextField();
        processIDField.setBounds(120, 20, 100, 20);
        frame.add(processIDField);

        JLabel arrivalLabel = new JLabel("Arrival Time:");
        arrivalLabel.setBounds(20, 50, 100, 20);
        frame.add(arrivalLabel);

        arrivalTimeField = new JTextField();
        arrivalTimeField.setBounds(120, 50, 100, 20);
        frame.add(arrivalTimeField);

        JLabel burstLabel = new JLabel("Burst Time:");
        burstLabel.setBounds(20, 80, 100, 20);
        frame.add(burstLabel);

        burstTimeField = new JTextField();
        burstTimeField.setBounds(120, 80, 100, 20);
        frame.add(burstTimeField);

        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setBounds(20, 110, 100, 20);
        frame.add(priorityLabel);

        priorityField = new JTextField();
        priorityField.setBounds(120, 110, 100, 20);
        frame.add(priorityField);

        JButton addButton = new JButton("Add Process");
        addButton.setBounds(20, 150, 120, 25);
        frame.add(addButton);

        JButton simulateButton = new JButton("Simulate");
        simulateButton.setBounds(150, 150, 100, 25);
        frame.add(simulateButton);

        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(90, 190, 100, 25);
        frame.add(clearButton);

        
        JLabel algorithmLabel = new JLabel("Algorithm:");
        algorithmLabel.setBounds(600, 20, 100, 20);
        frame.add(algorithmLabel);

        fcfsButton = new JRadioButton("FCFS");
        fcfsButton.setBounds(700, 20, 150, 20);
        sjfPreemptiveButton = new JRadioButton("SJF PREEMPTIVE");
        sjfPreemptiveButton.setBounds(700, 50, 150, 20);
        sjfNonPreemptiveButton = new JRadioButton("SJF NON-PREEMPTIVE");
        sjfNonPreemptiveButton.setBounds(700, 80, 200, 20);
        roundRobinButton = new JRadioButton("ROUND ROBIN");
        roundRobinButton.setBounds(700, 110, 150, 20);
        priorityPreemptiveButton = new JRadioButton("PRIORITY PREEMPTIVE");
        priorityPreemptiveButton.setBounds(700, 140, 200, 20);
        priorityNonPreemptiveButton = new JRadioButton("PRIORITY NON-PREEMPTIVE");
        priorityNonPreemptiveButton.setBounds(700, 170, 200, 20);

        algorithmGroup = new ButtonGroup();
        algorithmGroup.add(fcfsButton);
        algorithmGroup.add(sjfPreemptiveButton);
        algorithmGroup.add(sjfNonPreemptiveButton);
        algorithmGroup.add(roundRobinButton);
        algorithmGroup.add(priorityPreemptiveButton);
        algorithmGroup.add(priorityNonPreemptiveButton);

        frame.add(fcfsButton);
        frame.add(sjfPreemptiveButton);
        frame.add(sjfNonPreemptiveButton);
        frame.add(roundRobinButton);
        frame.add(priorityPreemptiveButton);
        frame.add(priorityNonPreemptiveButton);
        
        inputTableModel = new DefaultTableModel(new String[]{"Process ID", "Arrival Time", "Burst Time", "Priority"}, 0);
        inputTable = new JTable(inputTableModel);
        JScrollPane inputScrollPane = new JScrollPane(inputTable);
        inputScrollPane.setBounds(20, 250, 300, 150);
        frame.add(inputScrollPane);

        outputTableModel = new DefaultTableModel(new String[]{"Process ID", "Arrival Time", "Burst Time", "Priority", "Complete Time", "Waiting Time", "Turnaround Time"}, 0);
        outputTable = new JTable(outputTableModel);
        JScrollPane outputScrollPane = new JScrollPane(outputTable);
        outputScrollPane.setBounds(350, 250, 500, 150);
        frame.add(outputScrollPane);


        JLabel ganttChartLabel = new JLabel("Gantt Chart");
        ganttChartLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ganttChartLabel.setBounds(20, 420, 100, 20);
        frame.add(ganttChartLabel);

        ganttChartPanel = new JPanel();
        ganttChartPanel.setBounds(20, 450, 830, 50);
        ganttChartPanel.setLayout(new GridLayout(1, 0, 5, 5));
        ganttChartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        frame.add(ganttChartPanel);

        JLabel avgWaitingLabel = new JLabel("Average Waiting Time:");
        avgWaitingLabel.setBounds(20, 520, 150, 20);
        frame.add(avgWaitingLabel);

        avgWaitingTimeField = new JTextField();
        avgWaitingTimeField.setBounds(170, 520, 100, 20);
        avgWaitingTimeField.setEditable(false);
        frame.add(avgWaitingTimeField);

        JLabel avgTurnaroundLabel = new JLabel("Average Turn Around Time:");
        avgTurnaroundLabel.setBounds(350, 520, 200, 20);
        frame.add(avgTurnaroundLabel);

        avgTurnaroundTimeField = new JTextField();
        avgTurnaroundTimeField.setBounds(550, 520, 100, 20);
        avgTurnaroundTimeField.setEditable(false);
        frame.add(avgTurnaroundTimeField);

        
        addButton.addActionListener(e -> addProcess());
        simulateButton.addActionListener(e -> simulateScheduling());
        clearButton.addActionListener(e -> clearAll());

        frame.setVisible(true);
    }

    private void addProcess() {
        try {
            String id = processIDField.getText();
            int arrival = Integer.parseInt(arrivalTimeField.getText());
            int burst = Integer.parseInt(burstTimeField.getText());
            int priority = Integer.parseInt(priorityField.getText());
            processList.add(new Process(id, arrival, burst, priority));
            inputTableModel.addRow(new Object[]{id, arrival, burst, priority});
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter valid data.");
        }
    }

    private void simulateScheduling() {
        if (fcfsButton.isSelected()) {
            simulateFCFS();
        } else if (sjfPreemptiveButton.isSelected()) {
            simulateSJFPreemptive();
        } else if (sjfNonPreemptiveButton.isSelected()) {
            simulateSJFNonPreemptive();
        } else if (roundRobinButton.isSelected()) {
            simulateRoundRobin();
        } else if (priorityPreemptiveButton.isSelected()) {
            simulatePriorityPreemptive();
        } else if (priorityNonPreemptiveButton.isSelected()) {
            simulatePriorityNonPreemptive();
        }
    }

    private void clearAll() {
        processList.clear();
        inputTableModel.setRowCount(0);
        outputTableModel.setRowCount(0);
        ganttChartPanel.removeAll();
        ganttChartPanel.repaint();
        avgWaitingTimeField.setText("");
        avgTurnaroundTimeField.setText("");
    }

    
    private void simulateFCFS() {
        Collections.sort(processList, Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        outputTableModel.setRowCount(0);
        ganttChartPanel.removeAll();

        for (Process p : processList) {
            if (currentTime < p.arrivalTime) {
                currentTime = p.arrivalTime;
            }
            int waitingTime = currentTime - p.arrivalTime;
            int turnaroundTime = waitingTime + p.burstTime;
            currentTime += p.burstTime;

            totalWaitingTime += waitingTime;
            totalTurnaroundTime += turnaroundTime;

            
            outputTableModel.addRow(new Object[]{p.id, p.arrivalTime, p.burstTime, p.priority, currentTime, waitingTime, turnaroundTime});

            JLabel ganttLabel = new JLabel(" " + p.id + " ");
            ganttLabel.setOpaque(true);
            ganttLabel.setBackground(Color.CYAN);
            ganttChartPanel.add(ganttLabel);
        }

        avgWaitingTimeField.setText(String.format("%.2f", (float) totalWaitingTime / processList.size()));
        avgTurnaroundTimeField.setText(String.format("%.2f", (float) totalTurnaroundTime / processList.size()));
        ganttChartPanel.revalidate();
    }

   
    private void simulateSJFPreemptive() {
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.burstTime));
        int currentTime = 0, completed = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        outputTableModel.setRowCount(0);
        ganttChartPanel.removeAll();

        Map<String, Integer> remainingBurst = new HashMap<>();
        for (Process p : processList) remainingBurst.put(p.id, p.burstTime);

        while (completed < processList.size()) {
            for (Process p : processList) {
                if (p.arrivalTime <= currentTime && remainingBurst.get(p.id) > 0) {
                    pq.add(p);
                }
            }
            if (pq.isEmpty()) {
                currentTime++;
                continue;
            }
            Process current = pq.poll();
            remainingBurst.put(current.id, remainingBurst.get(current.id) - 1);

            JLabel ganttLabel = new JLabel(" " + current.id + " ");
            ganttLabel.setOpaque(true);
            ganttLabel.setBackground(Color.YELLOW);
            ganttChartPanel.add(ganttLabel);

            if (remainingBurst.get(current.id) == 0) {
                completed++;
                int turnaroundTime = currentTime + 1 - current.arrivalTime;
                int waitingTime = turnaroundTime - current.burstTime;

                totalWaitingTime += waitingTime;
                totalTurnaroundTime += turnaroundTime;

                outputTableModel.addRow(new Object[]{current.id, current.arrivalTime, current.burstTime, current.priority,
                        currentTime + 1, waitingTime, turnaroundTime});
            }
            currentTime++;
            pq.clear();
        }
        avgWaitingTimeField.setText(String.format("%.2f", (float) totalWaitingTime / processList.size()));
        avgTurnaroundTimeField.setText(String.format("%.2f", (float) totalTurnaroundTime / processList.size()));
        ganttChartPanel.revalidate();
    }

    private void simulateSJFNonPreemptive() {
        int n = processList.size();
        boolean[] isCompleted = new boolean[n];
        int currentTime = 0, completedProcesses = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;

        ganttChartPanel.removeAll();
        outputTableModel.setRowCount(0);

        while (completedProcesses < n) {
            int shortestIndex = -1;
            int minBurstTime = Integer.MAX_VALUE;

            
            for (int i = 0; i < n; i++) {
                if (!isCompleted[i] && processList.get(i).arrivalTime <= currentTime
                        && processList.get(i).burstTime < minBurstTime) {
                    minBurstTime = processList.get(i).burstTime;
                    shortestIndex = i;
                }
            }

            if (shortestIndex == -1) {
                currentTime++; 
                continue;
            }

            Process currentProcess = processList.get(shortestIndex);
            currentTime += currentProcess.burstTime;

            int turnaroundTime = currentTime - currentProcess.arrivalTime;
            int waitingTime = turnaroundTime - currentProcess.burstTime;

            totalTurnaroundTime += turnaroundTime;
            totalWaitingTime += waitingTime;

            isCompleted[shortestIndex] = true;
            completedProcesses++;

            
            JLabel ganttLabel = new JLabel(" " + currentProcess.id + " ");
            ganttLabel.setOpaque(true);
            ganttLabel.setBackground(Color.GREEN);
            ganttChartPanel.add(ganttLabel);

            
            outputTableModel.addRow(new Object[]{
                currentProcess.id,
                currentProcess.arrivalTime,
                currentProcess.burstTime,
                "-", // No priority used
                currentTime,
                waitingTime,
                turnaroundTime
            });
        }

        avgWaitingTimeField.setText(String.format("%.2f", (float) totalWaitingTime / n));
        avgTurnaroundTimeField.setText(String.format("%.2f", (float) totalTurnaroundTime / n));
        ganttChartPanel.revalidate();
    }

    private void simulateRoundRobin() {
        int n = processList.size();
        int[] remainingBurst = new int[n];
        int[] completionTime = new int[n];
        int[] waitingTime = new int[n];
        int[] turnaroundTime = new int[n];
        boolean[] isCompleted = new boolean[n];

        int timeQuantum = 2; 
        int currentTime = 0;
        int completedProcesses = 0;

        
        for (int i = 0; i < n; i++) {
            remainingBurst[i] = processList.get(i).burstTime;
        }

        Queue<Integer> queue = new LinkedList<>();
        ganttChartPanel.removeAll(); 
        outputTableModel.setRowCount(0); 

        for (int i = 0; i < n; i++) {
            if (processList.get(i).arrivalTime <= currentTime) {
                queue.add(i);
            }
        }
        while (completedProcesses < n) {
            if (queue.isEmpty()) {
                currentTime++;
                for (int i = 0; i < n; i++) {
                    if (processList.get(i).arrivalTime == currentTime && !isCompleted[i]) {
                        queue.add(i);
                    }
                }
                continue;
            }

            int currentProcessIndex = queue.poll();
            Process currentProcess = processList.get(currentProcessIndex);

            int executionTime = Math.min(remainingBurst[currentProcessIndex], timeQuantum);
            remainingBurst[currentProcessIndex] -= executionTime;
            currentTime += executionTime;

            
            addToGanttChart(currentProcess.id, currentTime);

            if (remainingBurst[currentProcessIndex] == 0) {
                isCompleted[currentProcessIndex] = true;
                completedProcesses++;
                completionTime[currentProcessIndex] = currentTime;
                turnaroundTime[currentProcessIndex] = completionTime[currentProcessIndex] - currentProcess.arrivalTime;
                waitingTime[currentProcessIndex] = turnaroundTime[currentProcessIndex] - currentProcess.burstTime;

                outputTableModel.addRow(new Object[] {
                    currentProcess.id,
                    currentProcess.arrivalTime,
                    currentProcess.burstTime,
                    currentProcess.priority,
                    completionTime[currentProcessIndex],
                    waitingTime[currentProcessIndex],
                    turnaroundTime[currentProcessIndex]
                });
            }

            for (int i = 0; i < n; i++) {
                if (!isCompleted[i] && processList.get(i).arrivalTime > currentTime - executionTime && processList.get(i).arrivalTime <= currentTime) {
                    queue.add(i);
                }
            }

            
            if (!isCompleted[currentProcessIndex]) {
                queue.add(currentProcessIndex);
            }
        }

        
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        for (int i = 0; i < n; i++) {
            totalWaitingTime += waitingTime[i];
            totalTurnaroundTime += turnaroundTime[i];
        }
        avgWaitingTimeField.setText(String.format("%.2f", (float) totalWaitingTime / n));
        avgTurnaroundTimeField.setText(String.format("%.2f", (float) totalTurnaroundTime / n));
    }

  
    private void simulatePriorityPreemptive() {
        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.priority));
      
            int currentTime = 0, completed = 0;
            int totalWaitingTime = 0, totalTurnaroundTime = 0;
            outputTableModel.setRowCount(0);
            ganttChartPanel.removeAll();

            Map<String, Integer> remainingBurst = new HashMap<>();
            for (Process p : processList) remainingBurst.put(p.id, p.burstTime);

            while (completed < processList.size()) {
                for (Process p : processList) {
                    if (p.arrivalTime <= currentTime && remainingBurst.get(p.id) > 0) {
                        pq.add(p);
                    }
                }
                if (pq.isEmpty()) {
                    currentTime++;
                    continue;
                }
                Process current = pq.poll();
                remainingBurst.put(current.id, remainingBurst.get(current.id) - 1);

                JLabel ganttLabel = new JLabel(" " + current.id + " ");
                ganttLabel.setOpaque(true);
                ganttLabel.setBackground(Color.YELLOW);
                ganttChartPanel.add(ganttLabel);

                if (remainingBurst.get(current.id) == 0) {
                    completed++;
                    int turnaroundTime = currentTime + 1 - current.arrivalTime;
                    int waitingTime = turnaroundTime - current.burstTime;

                    totalWaitingTime += waitingTime;
                    totalTurnaroundTime += turnaroundTime;

                    outputTableModel.addRow(new Object[]{current.id, current.arrivalTime, current.burstTime, current.priority,
                            currentTime + 1, waitingTime, turnaroundTime});
                }
                currentTime++;
                pq.clear();
            }
            avgWaitingTimeField.setText(String.format("%.2f", (float) totalWaitingTime / processList.size()));
            avgTurnaroundTimeField.setText(String.format("%.2f", (float) totalTurnaroundTime / processList.size()));
            ganttChartPanel.revalidate();
        
    }

    private void simulatePriorityNonPreemptive() {
        int n = processList.size();
        boolean[] isCompleted = new boolean[n];
        int currentTime = 0, completedProcesses = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;

        ganttChartPanel.removeAll();
        outputTableModel.setRowCount(0);

        while (completedProcesses < n) {
            int highestPriorityIndex = -1;
            int highestPriority = Integer.MAX_VALUE;

            
            for (int i = 0; i < n; i++) {
                if (!isCompleted[i] && processList.get(i).arrivalTime <= currentTime 
                        && processList.get(i).priority < highestPriority) {
                    highestPriority = processList.get(i).priority;
                    highestPriorityIndex = i;
                }
            }

            if (highestPriorityIndex == -1) {
                currentTime++; 
                continue;
            }

            Process currentProcess = processList.get(highestPriorityIndex);
            currentTime += currentProcess.burstTime;

            int turnaroundTime = currentTime - currentProcess.arrivalTime;
            int waitingTime = turnaroundTime - currentProcess.burstTime;

            totalTurnaroundTime += turnaroundTime;
            totalWaitingTime += waitingTime;

            isCompleted[highestPriorityIndex] = true;
            completedProcesses++;

            
            JLabel ganttLabel = new JLabel(" " + currentProcess.id + " ");
            ganttLabel.setOpaque(true);
            ganttLabel.setBackground(Color.CYAN);
            ganttChartPanel.add(ganttLabel);

            
            outputTableModel.addRow(new Object[]{
                currentProcess.id,
                currentProcess.arrivalTime,
                currentProcess.burstTime,
                currentProcess.priority,
                currentTime,
                waitingTime,
                turnaroundTime
            });
        }

        avgWaitingTimeField.setText(String.format("%.2f", (float) totalWaitingTime / n));
        avgTurnaroundTimeField.setText(String.format("%.2f", (float) totalTurnaroundTime / n));
        ganttChartPanel.revalidate();
    }

    private void addToGanttChart(String processId, int endTime) {
        JLabel ganttBar = new JLabel(processId, SwingConstants.CENTER);
        ganttBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ganttBar.setOpaque(true);
        ganttBar.setBackground(new Color(173, 216, 230)); 
        ganttBar.setPreferredSize(new Dimension(50, 50));
        ganttChartPanel.add(ganttBar);

        JLabel timeLabel = new JLabel(String.valueOf(endTime), SwingConstants.CENTER);
        timeLabel.setPreferredSize(new Dimension(50, 20));
        ganttChartPanel.add(timeLabel);
    }

   
    static class Process {
        String id;
        int arrivalTime, burstTime, priority;

        Process(String id, int arrivalTime, int burstTime, int priority) {
            this.id = id;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;
        }
    }
}
