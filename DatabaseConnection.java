import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * A dedicated class to handle the connection to the MySQL database.
 * This ensures that connection details are managed in one single place.
 */
public class DatabaseConnection {

    // The full address (URL) to your local MySQL server and the 'school' database.
    private static final String URL = "jdbc:mysql://localhost:3306/school";
    
    // The username for your database. 'root' is the default administrator.
    private static final String USER = "root";
    
    // =======================================================================
    // == CRITICAL STEP: The password you type here MUST MATCH the password ==
    // ==            you set during the MySQL installation/reconfiguration.  ==
    // =======================================================================
    private static final String PASSWORD = "Letmepass@5"; 

    /**
     * Attempts to establish and return a connection to the database.
     * @return a Connection object if successful, otherwise null.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Step 1: Load the MySQL JDBC driver class into memory.
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Step 2: Use the DriverManager to get a connection using your details.
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException | SQLException e) {
            // If any error occurs, show a pop-up dialog to the user.
            JOptionPane.showMessageDialog(null, "Database Connection Failed!\n" + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            // Also print the detailed error to the console for debugging purposes.
            e.printStackTrace(); 
        }
        return connection;
    }
}

