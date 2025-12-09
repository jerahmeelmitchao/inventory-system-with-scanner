package inventorysystem;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;   // <-- REQUIRED

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load Login Window
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/inventorysystem/views/login.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(
                    getClass().getResource("/inventorysystem/assets/styles.css").toExternalForm()
            );

            // REMOVE DEFAULT WINDOW FRAME (X, minimize, maximize)
            primaryStage.initStyle(StageStyle.UNDECORATED);

            primaryStage.setTitle("Inventory Management System");

            // App Icon
            primaryStage.getIcons().add(
                    new Image(getClass().getResourceAsStream("/inventorysystem/assets/app_icon.png"))
            );

            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
