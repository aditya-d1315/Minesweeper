package Minesweeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * This is the UI's driving class.
 * @author Annie Thach
 */
public class MinesweeperUI extends Application {
    public void start(Stage primaryStage) {
        try {
            GridPane root = (GridPane)FXMLLoader.load(
                    getClass().getResource("MinesweeperUI.fxml")); // Create root panel
            Scene scene = new Scene(root);          // Create scene
            primaryStage.setScene(scene);           // Set stage
            primaryStage.setTitle("Minesweeper");   // Change stage title
            primaryStage.setResizable(false);       // Disable resizing.
            primaryStage.show();                    // Show stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}