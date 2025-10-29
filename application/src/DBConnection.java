package application.src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Alert;

public class DBConnection {
    // Read from Docker environment variables or use defaults
    private static final String DB_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    private static final String DB_PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
    private static final String DB_NAME = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "student_db";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "rootpassword";
    
    private static final String URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?allowPublicKeyRetrieval=true&useSSL=false";

    private static Connection connection = null;    

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connected to MariaDB successfully!");
                System.out.println("   Host: " + DB_HOST + ":" + DB_PORT);
                System.out.println("   Database: " + DB_NAME);
            }
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            System.out.println("   URL: " + URL);
            showAlert("Database Error", "Cannot connect to MariaDB.\n" +
                     "Host: " + DB_HOST + ":" + DB_PORT + "\n" +
                     "Database: " + DB_NAME + "\n" +
                     "Error: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void showAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}