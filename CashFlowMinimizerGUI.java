import javax.swing.*;
import java.awt.*;
import java.util.*;

public class CashFlowMinimizerGUI extends JFrame {

    private DefaultListModel<String> peopleModel = new DefaultListModel<>();
    private JList<String> peopleList;
    private JTextField nameField, payerField, receiverField, amountField;
    private JTextArea resultArea;
    private Map<String, Integer> netBalance = new HashMap<>();

    public CashFlowMinimizerGUI() {
        setTitle("Cash Flow Minimizer - DSA Project by Sujal");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Cash Flow Minimizer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);

        // Left Panel - Input Section
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Add People & Transactions"));
        leftPanel.setPreferredSize(new Dimension(320, 600));

        // Add Person Section
        JPanel addPersonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameField = new JTextField(18);
        JButton addPersonBtn = new JButton("Add Person");
        addPersonBtn.addActionListener(e -> addPerson());

        addPersonPanel.add(new JLabel("Person Name: "));
        addPersonPanel.add(nameField);
        addPersonPanel.add(addPersonBtn);

        // People List
        JLabel peopleLabel = new JLabel("People List:");
        peopleList = new JList<>(peopleModel);
        JScrollPane listScroll = new JScrollPane(peopleList);
        listScroll.setPreferredSize(new Dimension(280, 150));

        // Add Transaction Section
        JPanel transPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        payerField = new JTextField(12);
        receiverField = new JTextField(12);
        amountField = new JTextField(8);

        transPanel.add(new JLabel("Payer:"));
        transPanel.add(payerField);
        transPanel.add(new JLabel("Receiver:"));
        transPanel.add(receiverField);
        transPanel.add(new JLabel("Amount ₹:"));
        transPanel.add(amountField);

        JButton addTransBtn = new JButton("Add Transaction");
        addTransBtn.addActionListener(e -> addTransaction());
        transPanel.add(addTransBtn);
        transPanel.add(new JLabel("")); // spacer

        // Add all components to left panel
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(addPersonPanel);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(peopleLabel);
        leftPanel.add(listScroll);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(transPanel);

        // Right Panel - Results
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Output & Results"));

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        resultArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane resultScroll = new JScrollPane(resultArea);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton showNetBtn = new JButton("Show Net Balances");
        JButton minimizeBtn = new JButton("Minimize Cash Flow");
        JButton clearBtn = new JButton("Clear All");

        showNetBtn.addActionListener(e -> showNetBalances());
        minimizeBtn.addActionListener(e -> minimizeCashFlow());
        clearBtn.addActionListener(e -> clearAll());

        buttonPanel.add(showNetBtn);
        buttonPanel.add(minimizeBtn);
        buttonPanel.add(clearBtn);

        rightPanel.add(resultScroll, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add panels to main frame
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Welcome Message
        resultArea.setText("✅ Welcome to Cash Flow Minimizer!\n\n" +
                "How to use:\n" +
                "1. Add people using 'Add Person'\n" +
                "2. Add transactions (who paid whom)\n" +
                "3. Click 'Show Net Balances' or 'Minimize Cash Flow'\n\n" +
                "This project uses Greedy Algorithm + PriorityQueue (Heaps)\n");
    }

    private void addPerson() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!peopleModel.contains(name)) {
            peopleModel.addElement(name);
            netBalance.put(name, 0);
            nameField.setText("");
            resultArea.append("✓ Added person: " + name + "\n");
        } else {
            JOptionPane.showMessageDialog(this, "This person already exists!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addTransaction() {
        String payer = payerField.getText().trim();
        String receiver = receiverField.getText().trim();
        String amtStr = amountField.getText().trim();

        if (payer.isEmpty() || receiver.isEmpty() || amtStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!peopleModel.contains(payer) || !peopleModel.contains(receiver)) {
            JOptionPane.showMessageDialog(this, "Payer or Receiver not found in people list!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int amount = Integer.parseInt(amtStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            netBalance.put(payer, netBalance.get(payer) - amount);
            netBalance.put(receiver, netBalance.get(receiver) + amount);

            resultArea.append(payer + " paid " + receiver + " ₹" + amount + "\n");

            // Clear fields
            payerField.setText("");
            receiverField.setText("");
            amountField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showNetBalances() {
        resultArea.append("\n=== Current Net Balances ===\n");

        String[] people = new String[peopleModel.getSize()];
        for (int i = 0; i < peopleModel.getSize(); i++) {
            people[i] = peopleModel.getElementAt(i);
        }

        for (String person : people) {
            int bal = netBalance.getOrDefault(person, 0);
            String sign = bal >= 0 ? " (has received)" : " (has paid)";
            resultArea.append(person + ": ₹" + bal + sign + "\n");
        }
        resultArea.append("------------------------------------\n");
    }

    private void minimizeCashFlow() {
        resultArea.append("\n=== Minimum Transactions to Settle ===\n");

        PriorityQueue<Person> maxHeap = new PriorityQueue<>((a, b) -> b.amount - a.amount); // Creditors
        PriorityQueue<Person> minHeap = new PriorityQueue<>((a, b) -> a.amount - b.amount); // Debtors

        String[] people = new String[peopleModel.getSize()];
        for (int i = 0; i < peopleModel.getSize(); i++) {
            people[i] = peopleModel.getElementAt(i);
        }

        for (String name : people) {
            int amt = netBalance.getOrDefault(name, 0);
            if (amt != 0) {
                Person p = new Person(name, amt);
                if (amt > 0) maxHeap.add(p);
                else minHeap.add(p);
            }
        }

        int transactionCount = 0;

        while (!maxHeap.isEmpty() && !minHeap.isEmpty()) {
            Person creditor = maxHeap.poll();
            Person debtor = minHeap.poll();

            int settleAmount = Math.min(creditor.amount, -debtor.amount);

            resultArea.append(creditor.name + "  → will  pay  " + debtor.name + "  ₹" + settleAmount + "\n");
            transactionCount++;

            creditor.amount -= settleAmount;
            debtor.amount += settleAmount;

            if (creditor.amount > 0) maxHeap.add(creditor);
            if (debtor.amount < 0) minHeap.add(debtor);
        }

        resultArea.append("\nTotal minimum transactions needed: " + transactionCount + "\n");
        resultArea.append("All debts settled successfully! ✅\n");
        resultArea.append("------------------------------------\n");
    }

    private void clearAll() {
        peopleModel.clear();
        netBalance.clear();
        resultArea.setText("All data has been cleared.\n\nReady for new entries...\n");
    }

    // Inner Person class
    static class Person {
        String name;
        int amount;

        Person(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CashFlowMinimizerGUI().setVisible(true);
        });
    }
}