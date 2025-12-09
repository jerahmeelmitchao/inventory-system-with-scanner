package inventorysystem.controllers;

import inventorysystem.dao.AuditLogDAO;
import inventorysystem.dao.UserDAO;
import inventorysystem.models.User;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SignUpController implements Initializable {

    @FXML
    private TextField signInFirstName;

    @FXML
    private TextField signInLastName;

    @FXML
    private TextField signInUsername;

    @FXML
    private PasswordField signInPassword;

    @FXML
    private PasswordField signInConfirmPassword;

    @FXML
    private Button signUpBtn;

    private void logAction(String action, String details) {
        AuditLogDAO.log(ItemController.getLoggedUsername(), action, details);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void handleSignUp() {

        String firstName = signInFirstName.getText().trim();
        String lastName = signInLastName.getText().trim();
        String username = signInUsername.getText().trim();
        String password = signInPassword.getText().trim();
        String confirmPassword = signInConfirmPassword.getText().trim();

        // REQUIRED FIELD VALIDATION
        if (firstName.isEmpty() || lastName.isEmpty()
                || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {

            showAlert("Error", "All fields are required.");
            return;
        }

        // PASSWORD MATCH
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        // CREATE USER OBJECT
        User newUser = new User(username, password, firstName, lastName);

        boolean success = UserDAO.createUser(newUser);

        if (success) {
            showAlert("Success", "Account created successfully! Please log in.");
            switchToLoginAuto();
        } else {
            showAlert("Error", "Failed to create account. Username may already exist.");
        }
    }

    // AUTO SWITCH TO LOGIN PAGE
    private void switchToLoginAuto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventorysystem/views/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) signUpBtn.getScene().getWindow();
            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(getClass().getResource("/inventorysystem/assets/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToLoginPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventorysystem/views/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 800, 500);
            scene.getStylesheets().add(getClass().getResource("/inventorysystem/assets/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
