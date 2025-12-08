package inventorysystem.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private StackPane mainContent;

    @FXML
    private Button dashboardBtn;
    @FXML
    private Button itemsBtn;
    @FXML
    private Button categoriesBtn;
    @FXML
    private Button borrowersBtn;
    @FXML
    private Button inchargesBtn;
    @FXML
    private Button logoutBtn;

    private Map<Button, String> buttonFxmlMap;
    @FXML
    private Button scannedItemsBtn;
    @FXML
    private Button ReportsBtn;

    public void initialize() {
        // Map buttons to FXML files (ensure these files exist in views/)
        buttonFxmlMap = new HashMap<>();
        buttonFxmlMap.put(dashboardBtn, "dashboard2.fxml"); // dashboard main content
        buttonFxmlMap.put(itemsBtn, "items.fxml");
        buttonFxmlMap.put(categoriesBtn, "category.fxml");
        buttonFxmlMap.put(borrowersBtn, "BorrowerManagement.fxml");
        buttonFxmlMap.put(inchargesBtn, "InCharge.fxml");
        buttonFxmlMap.put(scannedItemsBtn, "scanned_items.fxml"); //
//        buttonFxmlMap.put(ReportsBtn, "reports.fxml");

        // Add click events
        buttonFxmlMap.keySet().forEach(btn -> btn.setOnAction(e -> loadView(btn)));
        // ✔ Add logout action
        logoutBtn.setOnAction(e -> handleLogout());

        // Load default view
        loadView(dashboardBtn);
    }

    private void loadView(Button clickedButton) {
        // Highlight the active button
        buttonFxmlMap.keySet().forEach(btn
                -> btn.setStyle(btn == clickedButton
                        ? "-fx-background-color: #2980b9;" : "-fx-background-color: #34495e;")
        );

        // Load corresponding FXML
        String fxmlFile = buttonFxmlMap.get(clickedButton);
        URL fxmlUrl = getClass().getResource("/inventorysystem/views/" + fxmlFile);

        if (fxmlUrl == null) {
            System.err.println("❌ FXML file not found: " + fxmlFile);
            return;
        }

        try {
            Node view = FXMLLoader.load(fxmlUrl);
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("❌ Failed to load view: " + fxmlFile);
            e.printStackTrace();
        }
    }

    private void handleLogout() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be returned to the login screen.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventorysystem/views/login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) logoutBtn.getScene().getWindow();
                Scene scene = new Scene(root, 800, 500);
                scene.getStylesheets().add(getClass().getResource("/inventorysystem/assets/styles.css").toExternalForm());

                stage.setScene(scene);
                stage.centerOnScreen();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
