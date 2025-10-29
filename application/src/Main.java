package application.src;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Try multiple paths to find the FXML file
            FXMLLoader loader = new FXMLLoader();
            
            // Option 1: Try as a file in src/
            File fxmlFile = new File("src/manual.fxml");
            if (fxmlFile.exists()) {
                loader.setLocation(fxmlFile.toURI().toURL());
            } else {
                // Option 2: Try as a resource
                loader.setLocation(getClass().getResource("manual.fxml"));
            }
            
            if (loader.getLocation() == null) {
                System.err.println("❌ Could not find manual.fxml!");
                System.err.println("Looking in: " + new File(".").getAbsolutePath());
                System.exit(1);
            }
            
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("MySQL Database Manager - Manual Table Operations");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("✅ JavaFX application started successfully!");
        } catch (Exception e) {
            System.err.println("❌ Error starting application:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}