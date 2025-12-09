/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package inventorysystem.controllers;

import inventorysystem.dao.UserDAO;
import inventorysystem.models.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jaret
 */
public class LoginController implements Initializable {

    @FXML
    private PasswordField loginPassword;
    @FXML
    private Button loginBtn;
    @FXML
    private TextField loginUsername;
    @FXML
    private Text signInBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Press ENTER in password field → login
        loginPassword.setOnAction(e -> login(new ActionEvent(loginBtn, null)));

        // Press ENTER in username field → focus password OR login if password filled
        loginUsername.setOnAction(e -> {
            if (!loginPassword.getText().isEmpty()) {
                login(new ActionEvent(loginBtn, null));
            } else {
                loginPassword.requestFocus();
            }
        });
    }

    @FXML
    private void login(ActionEvent event) {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        User user = UserDAO.getUser(username, password);

        if (user != null) {

            // --- Show Access Granted popup then switch screen ---
            showAccessGrantedPopup(user.getUsername(), () -> {

                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/inventorysystem/views/dashboard.fxml")
                    );
                    Parent dashboardRoot = loader.load();

                    // Get stage safely!
                    Stage stage = getStage();

                    Scene dashboardScene = new Scene(dashboardRoot, 1200, 750);
                    dashboardScene.getStylesheets().add(
                            getClass().getResource("/inventorysystem/assets/styles.css").toExternalForm()
                    );

                    // Pass user info
                    ItemController.setLoggedUsername(user.getUsername());
                    ItemController.setLoggedUserNames(user.getFirstName(), user.getLastName());

                    stage.setScene(dashboardScene);
                    stage.setResizable(true);
                    stage.centerOnScreen();
                    stage.getIcons().add(
                            new Image(getClass().getResourceAsStream("/inventorysystem/assets/app_icon.png"))
                    );

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } else {
            showAlert("Access Denied", "Invalid username or password.");
            System.out.println("Invalid username or password.");
        }
    }

    private Stage getStage() {
        if (loginBtn.getScene() != null) {
            return (Stage) loginBtn.getScene().getWindow();
        }
        // Fallback in case loginBtn has no scene yet
        return (Stage) loginBtn.getParent().getScene().getWindow();
    }

    @FXML
    private void switchToSignUp(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventorysystem/views/signup.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(getClass().getResource("/inventorysystem/assets/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAccessGrantedPopup(String username, Runnable onClose) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(javafx.stage.StageStyle.UNDECORATED);
        popup.setAlwaysOnTop(true);

        VBox root = new VBox(12);
        root.setAlignment(Pos.CENTER);
        root.setStyle(
                "-fx-background-color: white;"
                + "-fx-padding: 22;"
                + "-fx-border-color: #dcdcdc;"
                + "-fx-border-width: 1;"
                + "-fx-background-radius: 12;"
                + "-fx-border-radius: 12;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.20), 12, 0, 0, 3);"
        );

        Label icon = new Label("✔");
        icon.setStyle("-fx-font-size: 40px; -fx-text-fill: #2ecc71;");

        Label title = new Label("Access Granted");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label msg = new Label("Welcome, " + username + "!");
        msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        root.getChildren().addAll(icon, title, msg);

        Scene scene = new Scene(root, 300, 180);
        popup.setScene(scene);

        // Auto-close popup after 1.5 seconds
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignored) {
            }
            javafx.application.Platform.runLater(() -> {
                popup.close();
                if (onClose != null) {
                    onClose.run();
                }
            });
        }).start();

        popup.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void exitApp(MouseEvent event) {
        System.exit(0);
    }

}
