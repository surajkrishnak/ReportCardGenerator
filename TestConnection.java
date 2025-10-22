import java.sql.Connection;

/**
 * A simple, dedicated class to test only the database connection.
 * This helps isolate connection problems from the main application logic.
 */
public class TestConnection {

    public static void main(String[] args) {
        System.out.println("Attempting to connect to the database...");

        // Get a connection from our dedicated connection class.
        // The try-with-resources statement ensures the connection is closed automatically.
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Check if the connection object is valid.
            if (conn != null) {
                System.out.println("✅✅✅ Database connection successful! ✅✅✅");
                System.out.println("The password in DatabaseConnection.java is correct.");
            } else {
                // The getConnection method already shows a JOptionPane error,
                // but we'll print a console message too.
                System.err.println("❌❌❌ Failed to get a database connection. ❌❌❌");
                System.err.println("Please check the error pop-up and ensure your MySQL server is running and the password is correct.");
            }
            
        } catch (Exception e) {
            // Catch any other unexpected errors during the connection attempt.
            System.err.println("An unexpected error occurred.");
            e.printStackTrace();
        }
    }
}

