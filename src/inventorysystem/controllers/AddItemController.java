package inventorysystem.controllers;

import inventorysystem.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AddItemController {

    @FXML
    private TextField itemNameField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextField unitField;
    @FXML
    private DatePicker dateAcquiredPicker;
    @FXML
    private ComboBox<String> statusComboBox;

    // ✅ CHANGED
    @FXML
    private ComboBox<String> locationComboBox;

    @FXML
    private ComboBox<String> inChargeComboBox;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField addedByField;
    @FXML
    private Button cancelButton;

    private final ObservableList<String> categories = FXCollections.observableArrayList();
    private final ObservableList<String> inCharges = FXCollections.observableArrayList();
    private final Map<String, Integer> inChargeMap = new HashMap<>();

    private static final SecureRandom RANDOM = new SecureRandom();

    @FXML
    public void initialize() {
        loadCategories();
        loadInChargeList();
        setupDropdownOptions();
        loadLocations(); // ✅ ADD

        dateAcquiredPicker.setValue(LocalDate.now());

        String username = ItemController.getLoggedUsername();
        addedByField.setText(username);
        addedByField.setEditable(false);
    }

    private void loadLocations() {
        String sql = "SELECT location_name FROM locations ORDER BY location_name";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                locationComboBox.getItems().add(rs.getString("location_name"));
            }
        } catch (SQLException e) {
            showError("Error", "Failed to load locations", e.getMessage());
        }
    }

    private void setupDropdownOptions() {
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Available", "Damaged", "Borrowed", "Missing", "Disposed"
        ));
    }

    private void loadCategories() {
        String sql = "SELECT category_name FROM categories ORDER BY category_name";
        try (Connection c = DatabaseConnection.getConnection(); Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {

            while (r.next()) {
                categories.add(r.getString(1));
            }
            categoryComboBox.setItems(categories);
        } catch (SQLException e) {
            showError("Error", "Failed to load categories.", e.getMessage());
        }
    }

    private void loadInChargeList() {
        String sql = "SELECT incharge_id, incharge_name FROM incharge ORDER BY incharge_name";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {

            while (r.next()) {
                inCharges.add(r.getString("incharge_name"));
                inChargeMap.put(r.getString("incharge_name"), r.getInt("incharge_id"));
            }
            inChargeComboBox.setItems(inCharges);
        } catch (SQLException e) {
            showError("Error", "Failed to load in-charge.", e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }

        saveNewItem(
                itemNameField.getText().trim(),
                getCategoryId(categoryComboBox.getValue()),
                unitField.getText().trim(),
                dateAcquiredPicker.getValue(),
                statusComboBox.getValue(),
                locationComboBox.getValue(), // ✅
                inChargeMap.get(inChargeComboBox.getValue()),
                addedByField.getText(),
                descriptionField.getText()
        );
    }

    private void saveNewItem(String name, int categoryId, String unit,
            LocalDate dateAcquired, String status,
            String location, int inChargeId,
            String addedBy, String description) {

        String sql = """
            INSERT INTO items (
                item_name, barcode, category_id, unit, description,
                date_acquired, status, storage_location, incharge_id, added_by
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, name);
            p.setString(2, generateUniqueBarcode());
            p.setInt(3, categoryId);
            p.setString(4, unit);
            p.setString(5, description);
            p.setDate(6, Date.valueOf(dateAcquired));
            p.setString(7, status);
            p.setString(8, location); // ✅
            p.setInt(9, inChargeId);
            p.setString(10, addedBy);

            p.executeUpdate();
            showInfo("Success", "Item added successfully!");
            ((Stage) itemNameField.getScene().getWindow()).close();

        } catch (SQLException e) {
            showError("Error", "Failed to add item.", e.getMessage());
        }
    }

    private boolean validateInputs() {
        return !(itemNameField.getText().isEmpty()
                || categoryComboBox.getValue() == null
                || unitField.getText().isEmpty()
                || statusComboBox.getValue() == null
                || locationComboBox.getValue() == null
                || inChargeComboBox.getValue() == null);
    }

    private int getCategoryId(String name) {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement(
                "SELECT category_id FROM categories WHERE category_name=?")) {
            p.setString(1, name);
            ResultSet r = p.executeQuery();
            return r.next() ? r.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    private String generateUniqueBarcode() {
        return Long.toHexString(RANDOM.nextLong()).toUpperCase().substring(0, 12);
    }

    private void showError(String t, String h, String m) {
        new Alert(Alert.AlertType.ERROR, m).show();
    }

    private void showInfo(String t, String m) {
        new Alert(Alert.AlertType.INFORMATION, m).show();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
