package inventorysystem.controllers;

import inventorysystem.dao.ItemDAO;
import inventorysystem.models.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
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
    @FXML
    private Button scannedItemsBtn;
    @FXML
    private Button ReportsBtn;

    // 🔹 NEW: Borrow Records button
    @FXML
    private Button borrowRecordsBtn;

    private Map<Button, String> buttonFxmlMap;

    private StringBuilder barcodeBuffer = new StringBuilder();

    public void initialize() {
        // Map buttons to FXML files (ensure these files exist in views/)
        buttonFxmlMap = new HashMap<>();
        buttonFxmlMap.put(dashboardBtn, "dashboard2.fxml");
        buttonFxmlMap.put(itemsBtn, "items.fxml");
        buttonFxmlMap.put(categoriesBtn, "category.fxml");
        buttonFxmlMap.put(borrowersBtn, "BorrowerManagement.fxml");
        buttonFxmlMap.put(inchargesBtn, "InCharge.fxml");
        buttonFxmlMap.put(scannedItemsBtn, "scanned_items.fxml");
        // 🔹 NEW: borrow records view
        buttonFxmlMap.put(borrowRecordsBtn, "borrow_records.fxml");

        // Add click events
        buttonFxmlMap.keySet().forEach(btn -> btn.setOnAction(e -> loadView(btn)));

        // Logout
        logoutBtn.setOnAction(e -> handleLogout());

        // Load default view
        loadView(dashboardBtn);

        Platform.runLater(() -> {
            mainContent.requestFocus();
            setupBarcodeScanner();
        });
    }

    private void loadView(Button clickedButton) {
        // Highlight active button
        buttonFxmlMap.keySet().forEach(btn ->
                btn.setStyle(btn == clickedButton
                        ? "-fx-background-color: #2980b9;"
                        : "-fx-background-color: #34495e;")
        );

        String fxmlFile = buttonFxmlMap.get(clickedButton);

        try {
            Node view = FXMLLoader.load(getClass().getResource("/inventorysystem/views/" + fxmlFile));
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);

            Platform.runLater(() -> mainContent.requestFocus());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupBarcodeScanner() {
        Scene scene = mainContent.getScene();
        if (scene == null) {
            System.out.println("Scene not ready — scanner not connected yet.");
            return;
        }

        scene.setOnKeyPressed(event -> {
            String ch = event.getText();

            if (ch.matches("[A-Za-z0-9]")) {
                barcodeBuffer.append(ch);
            }

            if (event.getCode().toString().equals("ENTER")) {
                String scannedCode = barcodeBuffer.toString();
                barcodeBuffer.setLength(0);
                handleScannedBarcode(scannedCode);
            }
        });

        System.out.println("✔ Barcode scanner successfully connected.");
    }

    private void handleScannedBarcode(String barcode) {
        ItemDAO dao = new ItemDAO();
        Item item = dao.getItemByBarcode(barcode);

        if (item != null) {
            showScanPopup(item);
        } else {
            System.out.println("Item not found. Barcode: " + barcode);
        }
    }

    private void showScanPopup(Item item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventorysystem/views/scan_result.fxml"));
            Parent root = loader.load();

            ScanResultController controller = loader.getController();
            controller.setItem(item);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Item Details");

            stage.setAlwaysOnTop(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
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
                scene.getStylesheets().add(getClass()
                        .getResource("/inventorysystem/assets/styles.css")
                        .toExternalForm());

                stage.setScene(scene);
                stage.centerOnScreen();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
