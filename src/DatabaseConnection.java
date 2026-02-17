import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/banking_system";

    private static final String USER = "root";

    private static final String PASSWORD = "Karthi@12300";

    // Method to get connection
    public static Connection getConnection() throws SQLException {

        try {
            // Load MySQL JDBC Driver 
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {

            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();

        }

        // Return connection
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
