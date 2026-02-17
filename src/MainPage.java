import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MainPage extends JFrame {

    private int userId;
    private JButton depositButton, withdrawButton, transferButton, historyButton, balanceButton;
    private JPanel panel;

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public MainPage(int userId) {

        this.userId = userId;

        setTitle("Online Banking System");
        setSize(400, 400);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        getContentPane().setBackground(Color.decode("#b5b3e8"));

        panel = new JPanel();
        panel.setBounds(700, 250, 500, 360);
        panel.setLayout(null);
        panel.setBackground(Color.decode("#6c67c9"));
        add(panel);

        JLabel titleLabel = new JLabel("Welcome to Your Banking Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(70, 20, 400, 30);
        panel.add(titleLabel);

        // Deposit
        depositButton = new JButton("Deposit");
        depositButton.setBounds(175, 80, 150, 30);
        depositButton.setBackground(Color.decode("#b5b3e8"));
        depositButton.setForeground(Color.WHITE);
        depositButton.setBorderPainted(false);
        depositButton.addActionListener(e -> showDepositDialog());
        panel.add(depositButton);

        // Withdraw
        withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(175, 130, 150, 30);
        withdrawButton.setBackground(Color.decode("#b5b3e8"));
        withdrawButton.setForeground(Color.WHITE);
        withdrawButton.setBorderPainted(false);
        withdrawButton.addActionListener(e -> showWithdrawDialog());
        panel.add(withdrawButton);

        // Transfer
        transferButton = new JButton("Transfer");
        transferButton.setBounds(175, 180, 150, 30);
        transferButton.setBackground(Color.decode("#b5b3e8"));
        transferButton.setForeground(Color.WHITE);
        transferButton.setBorderPainted(false);
        transferButton.addActionListener(e -> showTransferDialog());
        panel.add(transferButton);

        // History
        historyButton = new JButton("Transaction History");
        historyButton.setBounds(155, 230, 180, 30);
        historyButton.setBackground(Color.decode("#b5b3e8"));
        historyButton.setForeground(Color.WHITE);
        historyButton.setBorderPainted(false);
        historyButton.addActionListener(e -> showTransactionHistory());
        panel.add(historyButton);

        // Balance
        balanceButton = new JButton("Check Balance");
        balanceButton.setBounds(155, 280, 180, 30);
        balanceButton.setBackground(Color.decode("#b5b3e8"));
        balanceButton.setForeground(Color.WHITE);
        balanceButton.setBorderPainted(false);
        balanceButton.addActionListener(e -> showBalance());
        panel.add(balanceButton);

        setVisible(true);
    }

    // Database Connection
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Deposit Dialog
    private void showDepositDialog() {

        String amountStr = JOptionPane.showInputDialog("Enter amount to deposit:");

        if (amountStr != null) {

            try {

                double amount = Double.parseDouble(amountStr);

                if (amount > 0) {
                    deposit(amount);
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than 0");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Amount");
            }
        }
    }

    // Deposit Method
    private void deposit(double amount) {

        try (Connection conn = connect()) {

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET balance = balance + ? WHERE id = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            ps.executeUpdate();

            addTransaction("Deposit", amount);

            JOptionPane.showMessageDialog(this, "Deposit Successful");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Withdraw Dialog
    private void showWithdrawDialog() {

        String amountStr = JOptionPane.showInputDialog("Enter amount to withdraw:");

        if (amountStr != null) {

            try {

                double amount = Double.parseDouble(amountStr);

                if (amount > 0) {
                    withdraw(amount);
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than 0");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Amount");
            }
        }
    }

    // Withdraw Method
    private void withdraw(double amount) {

        try (Connection conn = connect()) {

            PreparedStatement check = conn.prepareStatement(
                    "SELECT balance FROM users WHERE id = ?");
            check.setInt(1, userId);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {

                double balance = rs.getDouble("balance");

                if (balance >= amount) {

                    PreparedStatement ps = conn.prepareStatement(
                            "UPDATE users SET balance = balance - ? WHERE id = ?");
                    ps.setDouble(1, amount);
                    ps.setInt(2, userId);
                    ps.executeUpdate();

                    addTransaction("Withdraw", amount);

                    JOptionPane.showMessageDialog(this, "Withdraw Successful");

                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient Balance");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Transfer Dialog
    private void showTransferDialog() {

        JTextField receiverField = new JTextField();
        JTextField amountField = new JTextField();

        Object[] message = {
                "Receiver User ID:", receiverField,
                "Amount:", amountField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Transfer", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {

            try {

                int receiverId = Integer.parseInt(receiverField.getText());
                double amount = Double.parseDouble(amountField.getText());

                transfer(receiverId, amount);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid Input");
            }
        }
    }

    // Transfer Method
    private void transfer(int receiverId, double amount) {

        try (Connection conn = connect()) {

            conn.setAutoCommit(false);

            PreparedStatement check = conn.prepareStatement(
                    "SELECT balance FROM users WHERE id = ?");
            check.setInt(1, userId);

            ResultSet rs = check.executeQuery();

            if (rs.next()) {

                double balance = rs.getDouble("balance");

                if (balance >= amount) {

                    PreparedStatement withdraw = conn.prepareStatement(
                            "UPDATE users SET balance = balance - ? WHERE id = ?");
                    withdraw.setDouble(1, amount);
                    withdraw.setInt(2, userId);
                    withdraw.executeUpdate();

                    PreparedStatement deposit = conn.prepareStatement(
                            "UPDATE users SET balance = balance + ? WHERE id = ?");
                    deposit.setDouble(1, amount);
                    deposit.setInt(2, receiverId);
                    deposit.executeUpdate();

                    addTransaction("Transfer Sent", amount);

                    conn.commit();

                    JOptionPane.showMessageDialog(this, "Transfer Successful");

                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient Balance");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Transaction History
    private void showTransactionHistory() {

        try (Connection conn = connect()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT type, amount, date FROM transactions WHERE user_id = ?");
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            StringBuilder history = new StringBuilder();

            while (rs.next()) {

                history.append(rs.getString("type"))
                        .append(" - ₹")
                        .append(rs.getDouble("amount"))
                        .append(" - ")
                        .append(rs.getTimestamp("date"))
                        .append("\n");
            }

            JOptionPane.showMessageDialog(this, history.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Show Balance
    private void showBalance() {

        try (Connection conn = connect()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT balance FROM users WHERE id = ?");
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                JOptionPane.showMessageDialog(this,
                        "Current Balance: ₹" + rs.getDouble("balance"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add Transaction Record
    private void addTransaction(String type, double amount) {

        try (Connection conn = connect()) {

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO transactions(user_id, type, amount) VALUES(?,?,?)");

            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setDouble(3, amount);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main method for testing
    public static void main(String[] args) {

        new MainPage(1); // example user id
    }
}
