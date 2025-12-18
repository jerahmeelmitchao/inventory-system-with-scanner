package inventorysystem.controllers;

import inventorysystem.dao.LocationDAO;
import inventorysystem.models.Location;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class LocationController {

    @FXML
    private TextField nameField, descField;
    @FXML
    private TableView<Location> locationTable;
    @FXML
    private TableColumn<Location, Integer> colId;
    @FXML
    private TableColumn<Location, String> colName, colDesc;

    private final LocationDAO dao = new LocationDAO();
    private ObservableList<Location> locations;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getLocationId()).asObject());
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocationName()));
        colDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        loadLocations();

        // ✅ Populate fields when a row is selected
        locationTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        nameField.setText(newSel.getLocationName());
                        descField.setText(newSel.getDescription());
                    }
                }
        );
    }

    private void loadLocations() {
        locations = FXCollections.observableArrayList(dao.getAllLocations());
        locationTable.setItems(locations);
    }

    // ===================== ADD =====================
    @FXML
    private void handleAdd() {
        if (nameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Location name is required.");
            return;
        }

        try {
            dao.addLocation(new Location(0, nameField.getText(), descField.getText()));
            loadLocations();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Location added successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add location.");
        }
    }

    // ===================== UPDATE =====================
    @FXML
    private void handleUpdate() {
        Location selected = locationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a location to update.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Update");
        confirm.setHeaderText("Update Location");
        confirm.setContentText("Are you sure you want to update this location?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                selected.setLocationName(nameField.getText());
                selected.setDescription(descField.getText());
                dao.updateLocation(selected);
                loadLocations();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Location updated successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update location.");
            }
        }
    }

    // ===================== DELETE =====================
    @FXML
    private void handleDelete() {
        Location selected = locationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a location to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Location");
        confirm.setContentText("This action cannot be undone. Continue?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                dao.deleteLocation(selected.getLocationId());
                loadLocations();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Location deleted successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete location.");
            }
        }
    }

    // ===================== HELPERS =====================
    private void clearFields() {
        nameField.clear();
        descField.clear();
        locationTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
