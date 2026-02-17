import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton, signupRedirectButton;
    JPanel panel;

    public LoginPage() {

        setTitle("Login Page");
        setSize(400, 500);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        getContentPane().setBackground(Color.decode("#b5b3e8"));

        panel = new JPanel();
        panel.setBounds(700, 250, 340, 350);
        panel.setLayout(null);
        panel.setBackground(Color.decode("#6c67c9"));
        add(panel);

        JLabel titleLabel = new JLabel("Login to Your Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(20, 20, 300, 30);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(20, 80, 100, 25);
        usernameLabel.setForeground(Color.WHITE);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(130, 80, 180, 30);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 130, 100, 25);
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(130, 130, 180, 30);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(60, 200, 100, 40);
        loginButton.setBackground(Color.decode("#b5b3e8"));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);

        loginButton.addActionListener(e -> login());

        panel.add(loginButton);

        signupRedirectButton = new JButton("Sign Up");
        signupRedirectButton.setBounds(180, 200, 100, 40);
        signupRedirectButton.setBackground(Color.decode("#b5b3e8"));
        signupRedirectButton.setForeground(Color.WHITE);
        signupRedirectButton.setBorderPainted(false);

        signupRedirectButton.addActionListener(e -> {
            new SignupPage().setVisible(true);
            dispose();
        });

        panel.add(signupRedirectButton);
    }

    private void login() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please enter username and password");

            return;
        }

        try {

            Connection con = DatabaseConnection.getConnection();

            String query =
                    "SELECT id FROM users WHERE username=? AND password=?";

            PreparedStatement pst =
                    con.prepareStatement(query);

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                int userId = rs.getInt("id");

                JOptionPane.showMessageDialog(this,
                        "Login Successful!");

                new MainPage(userId).setVisible(true);

                dispose();

            } else {

                JOptionPane.showMessageDialog(this,
                        "Invalid Username or Password");

            }

            rs.close();
            pst.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(this,
                    "Database Error");

        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new LoginPage().setVisible(true);

        });
    }
}
