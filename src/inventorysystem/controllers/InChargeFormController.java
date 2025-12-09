package inventorysystem.controllers;

import inventorysystem.dao.CategoryDAO;
import inventorysystem.dao.InchargeDAO;
import inventorysystem.models.Incharge;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class InChargeFormController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField nameField, positionField, contactField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private Button saveButton;

    private final InchargeDAO dao = new InchargeDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private Map<String, Integer> categories = new HashMap<>();
    private Incharge currentIncharge = null;
    private Runnable onSaveCallback;

    @FXML
    public void initialize() {

        categoryDAO.getAllCategories().forEach(c -> {
            categories.put(c.getCategoryName(), c.getCategoryId());
        });

        categoryComboBox.setItems(FXCollections.observableArrayList(categories.keySet()));

        // ✔ Apply design AFTER scene is ready
        Platform.runLater(this::applyModernDesign);
    }

   private void applyModernDesign() {

    // Get root (main VBox of the form)
    VBox root = (VBox) saveButton.getScene().lookup("#rootContainer");
    if (root == null) {
        // fallback: use parent
        root = (VBox) saveButton.getParent().getParent();
    }

    // Increase form size to avoid cut buttons
    Stage stage = (Stage) saveButton.getScene().getWindow();
    stage.setWidth(480);   // wider
    stage.setHeight(420);  // taller (fixes cut buttons)

    // Style title
    titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

    // TextField / Combo style
    String inputStyle =
            "-fx-background-radius: 10;" +
            "-fx-border-radius: 10;" +
            "-fx-border-color: #d0d7de;" +
            "-fx-padding: 10;" +
            "-fx-font-size: 14px;";

    nameField.setStyle(inputStyle);
    positionField.setStyle(inputStyle);
    contactField.setStyle(inputStyle);
    categoryComboBox.setStyle(inputStyle);

    // Save button
    saveButton.setStyle(
            "-fx-background-color:#2c82d8;" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:10;" +
            "-fx-padding:10 25;" +
            "-fx-font-weight:bold;"
    );

    // Cancel button
    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(e -> handleCancel());
    cancelButton.setStyle(
            "-fx-background-color:#bdc3c7;" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:10;" +
            "-fx-padding:10 25;"
    );

    // Button row
    HBox buttonRow = new HBox(10, saveButton, cancelButton);
    buttonRow.setStyle("-fx-alignment:center-right;");
    buttonRow.setPrefHeight(60); // ensures they are fully visible

    // Panel spacing & padding
    root.setSpacing(20);
    root.setPadding(new javafx.geometry.Insets(20));
    root.setStyle(
            "-fx-background-color:white;" +
            "-fx-background-radius:14;" +
            "-fx-effect:dropshadow(gaussian, rgba(0,0,0,0.25),20,0,0,4);"
    );

    // Replace old button row
    root.getChildren().remove(root.getChildren().size() - 1);
    root.getChildren().add(buttonRow);
}


    public void setData(Incharge incharge, Runnable callback) {
        this.currentIncharge = incharge;
        this.onSaveCallback = callback;

        if (incharge == null) {
            titleLabel.setText("Add In-Charge");
        } else {
            titleLabel.setText("Edit In-Charge");

            nameField.setText(incharge.getInchargeName());
            positionField.setText(incharge.getPosition());
            contactField.setText(incharge.getContactInfo());

            String catName = categories.entrySet().stream()
                    .filter(e -> e.getValue() == incharge.getAssignedCategoryId())
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(null);

            categoryComboBox.setValue(catName);
        }
    }

    @FXML
    private void handleSave() {

        if (!validate()) return;

        if (currentIncharge == null) currentIncharge = new Incharge();

        currentIncharge.setInchargeName(nameField.getText());
        currentIncharge.setPosition(positionField.getText());
        currentIncharge.setContactInfo(contactField.getText());
        currentIncharge.setAssignedCategoryId(categories.get(categoryComboBox.getValue()));

        if (currentIncharge.getInchargeId() == 0)
            dao.addIncharge(currentIncharge);
        else
            dao.updateIncharge(currentIncharge);

        if (onSaveCallback != null) onSaveCallback.run();

        close();
    }

    private boolean validate() {
        if (nameField.getText().isEmpty() ||
            positionField.getText().isEmpty() ||
            contactField.getText().isEmpty() ||
            categoryComboBox.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please fill all fields.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    @FXML
    private void handleCancel() { close(); }

    private void close() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
