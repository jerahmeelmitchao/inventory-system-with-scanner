package inventorysystem;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventorysystem/views/login.fxml"));
            Parent root = loader.load();

            // Create scene with fixed size
            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(getClass().getResource("/inventorysystem/assets/styles.css").toExternalForm());

            // Stage setup
            primaryStage.setTitle("Inventory Management System");
            // ✅ Add app icon here
            primaryStage.getIcons().add(
                    new Image(getClass().getResourceAsStream("/inventorysystem/assets/app_icon.png"))
            );
            primaryStage.setScene(scene);

            // Disable resizing to make it fixed
            primaryStage.setResizable(false);

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
