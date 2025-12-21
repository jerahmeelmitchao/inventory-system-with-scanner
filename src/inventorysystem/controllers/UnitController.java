package inventorysystem.controllers;

import inventorysystem.dao.UnitDAO;
import inventorysystem.models.Unit;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UnitController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField descField;

    @FXML
    private TableView<Unit> unitTable;
    @FXML
    private TableColumn<Unit, Integer> colId;
    @FXML
    private TableColumn<Unit, String> colName;
    @FXML
    private TableColumn<Unit, String> colDesc;

    private final UnitDAO dao = new UnitDAO();
    private ObservableList<Unit> unitList;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(c
                -> new SimpleIntegerProperty(c.getValue().getUnitId()).asObject());
        colName.setCellValueFactory(c
                -> new SimpleStringProperty(c.getValue().getUnitName()));
        colDesc.setCellValueFactory(c
                -> new SimpleStringProperty(c.getValue().getDescription()));

        loadUnits();

        // ✅ Populate fields on row select
        unitTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, selected) -> {
                    if (selected != null) {
                        nameField.setText(selected.getUnitName());
                        descField.setText(selected.getDescription());
                    }
                }
        );
    }

    private void loadUnits() {
        unitList = FXCollections.observableArrayList(dao.getAllUnits());
        unitTable.setItems(unitList);
    }

    @FXML
    private void handleAdd() {
        if (nameField.getText().isBlank()) {
            showError("Unit name is required.");
            return;
        }

        boolean success = dao.addUnit(
                new Unit(
                        nameField.getText().trim(),
                        descField.getText().trim()
                )
        );

        if (success) {
            showInfo("Unit added successfully.");
            loadUnits();
            handleClear();
        } else {
            showError("Failed to add unit.");
        }
    }

    @FXML
    private void handleUpdate() {
        Unit selected = unitTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a unit to update.");
            return;
        }

        selected.setUnitName(nameField.getText().trim());
        selected.setDescription(descField.getText().trim());

        if (dao.updateUnit(selected)) {
            showInfo("Unit updated successfully.");
            loadUnits();
        } else {
            showError("Failed to update unit.");
        }
    }

    @FXML
    private void handleDelete() {
        Unit selected = unitTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a unit to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete selected unit?",
                ButtonType.OK, ButtonType.CANCEL);

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                if (dao.deleteUnit(selected.getUnitId())) {
                    showInfo("Unit deleted successfully.");
                    loadUnits();
                    handleClear();
                } else {
                    showError("Failed to delete unit.");
                }
            }
        });
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        descField.clear();
        unitTable.getSelectionModel().clearSelection();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
