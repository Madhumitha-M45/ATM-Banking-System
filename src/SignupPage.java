import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignupPage extends JFrame {

    JTextField usernameField, accountField, balanceField;
    JPasswordField passwordField;
    JButton signupButton, loginRedirectButton;
    JPanel panel;

    public SignupPage() {

        setTitle("Sign Up Page");
        setSize(400, 350);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        getContentPane().setBackground(Color.decode("#b5b3e8"));

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(700, 250, 400, 350);
        panel.setBackground(Color.decode("#6c67c9"));
        add(panel);

        // Title
        JLabel titleLabel = new JLabel("Create your account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(90, 20, 300, 30);
        panel.add(titleLabel);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 80, 120, 25);
        usernameLabel.setForeground(Color.WHITE);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 80, 200, 30);
        panel.add(usernameField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 120, 120, 25);
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 120, 200, 30);
        panel.add(passwordField);

        // Account Number
        JLabel accountLabel = new JLabel("Account No:");
        accountLabel.setBounds(30, 160, 120, 25);
        accountLabel.setForeground(Color.WHITE);
        panel.add(accountLabel);

        accountField = new JTextField();
        accountField.setBounds(150, 160, 200, 30);
        panel.add(accountField);

        // Balance
        JLabel balanceLabel = new JLabel("Current Balance:");
        balanceLabel.setBounds(30, 200, 120, 25);
        balanceLabel.setForeground(Color.WHITE);
        panel.add(balanceLabel);

        balanceField = new JTextField();
        balanceField.setBounds(150, 200, 200, 30);
        panel.add(balanceField);

        // Signup Button
        signupButton = new JButton("Sign Up");
        signupButton.setBounds(100, 260, 100, 35);
        signupButton.setBackground(Color.decode("#b5b3e8"));
        signupButton.setForeground(Color.WHITE);
        signupButton.setBorderPainted(false);

        signupButton.addActionListener(e -> {

            try {
                signUp();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error during signup");
            }

        });

        panel.add(signupButton);

        // Login Redirect Button
        loginRedirectButton = new JButton("Login");
        loginRedirectButton.setBounds(220, 260, 100, 35);
        loginRedirectButton.setBackground(Color.decode("#b5b3e8"));
        loginRedirectButton.setForeground(Color.WHITE);
        loginRedirectButton.setBorderPainted(false);

        loginRedirectButton.addActionListener(e -> {

            new LoginPage().setVisible(true);
            dispose();

        });

        panel.add(loginRedirectButton);
    }

    // Signup Logic
    private void signUp() throws SQLException {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String accountNo = accountField.getText().trim();
        String balanceText = balanceField.getText().trim();

        // Validation
        if (username.isEmpty() ||
                password.isEmpty() ||
                accountNo.isEmpty() ||
                balanceText.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "All fields are required!");

            return;
        }

        double balance;

        try {

            balance = Double.parseDouble(balanceText);

            if (balance < 0) {

                JOptionPane.showMessageDialog(this,
                        "Balance cannot be negative");

                return;
            }

        }
        catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(this,
                    "Invalid balance value");

            return;
        }

        Connection con = null;
        PreparedStatement userPst = null;
        PreparedStatement transactionPst = null;
        ResultSet rs = null;

        try {

            con = DatabaseConnection.getConnection();

            con.setAutoCommit(false);

            // Insert user
            String userQuery =
                    "INSERT INTO users(username, password, account_no, balance) VALUES (?, ?, ?, ?)";

            userPst = con.prepareStatement(
                    userQuery,
                    PreparedStatement.RETURN_GENERATED_KEYS);

            userPst.setString(1, username);
            userPst.setString(2, password);
            userPst.setString(3, accountNo);
            userPst.setDouble(4, balance);

            int rows = userPst.executeUpdate();

            if (rows == 0) {

                con.rollback();

                JOptionPane.showMessageDialog(this,
                        "Signup failed");

                return;
            }

            // Get user id
            rs = userPst.getGeneratedKeys();

            int userId = -1;

            if (rs.next()) {

                userId = rs.getInt(1);

            }

            if (userId == -1) {

                con.rollback();

                JOptionPane.showMessageDialog(this,
                        "User ID creation failed");

                return;
            }

            // Insert transaction
            String transactionQuery =
                    "INSERT INTO transactions(user_id, type, amount) VALUES (?, ?, ?)";

            transactionPst =
                    con.prepareStatement(transactionQuery);

            transactionPst.setInt(1, userId);
            transactionPst.setString(2, "Deposit");
            transactionPst.setDouble(3, balance);

            transactionPst.executeUpdate();

            con.commit();

            JOptionPane.showMessageDialog(this,
                    "Signup Successful!");

            new LoginPage().setVisible(true);

            dispose();

        }
        catch (Exception e) {

            if (con != null)
                con.rollback();

            e.printStackTrace();

            JOptionPane.showMessageDialog(this,
                    "Signup failed due to error");

        }
        finally {

            if (rs != null) rs.close();
            if (userPst != null) userPst.close();
            if (transactionPst != null) transactionPst.close();
            if (con != null) con.close();

        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
                new SignupPage().setVisible(true)
        );
    }
}
