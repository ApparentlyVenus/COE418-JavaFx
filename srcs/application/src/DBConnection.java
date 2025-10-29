package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Alert;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/student_db"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "YOUR_PASSWORD";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Ensure database exists
                Connection tempConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASSWORD);
                tempConn.createStatement().execute("CREATE DATABASE IF NOT EXISTS student_db");
                tempConn.close();
                
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connected to MySQL successfully!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            showAlert("Database Error", "Cannot connect to MySQL. Make sure MySQL is running on port 3306.");
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