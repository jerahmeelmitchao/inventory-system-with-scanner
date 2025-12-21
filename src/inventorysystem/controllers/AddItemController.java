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
    private ComboBox<String> unitComboBox;
    private final Map<String, Integer> unitMap = new HashMap<>();

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
        loadUnits();
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
                unitMap.get(unitComboBox.getValue()),
                dateAcquiredPicker.getValue(),
                statusComboBox.getValue(),
                locationComboBox.getValue(), // ✅
                inChargeMap.get(inChargeComboBox.getValue()),
                addedByField.getText(),
                descriptionField.getText()
        );
    }

    private void saveNewItem(
            String name,
            int categoryId,
            int unitId,
            LocalDate dateAcquired,
            String status,
            String locationName,
            int inChargeId,
            String addedBy,
            String description
    ) {

        String sql = """
        INSERT INTO items (
            item_name, barcode, category_id, unit_id,
            date_acquired, status, location_id,
            incharge_id, added_by, description
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, name);
            p.setString(2, generateUniqueBarcode());
            p.setInt(3, categoryId);
            p.setInt(4, unitId);
            p.setDate(5, Date.valueOf(dateAcquired));
            p.setString(6, status);
            p.setInt(7, getLocationId(locationName));
            p.setInt(8, inChargeId);
            p.setString(9, addedBy);
            p.setString(10, description);

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
                || unitComboBox.getValue() == null
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

    private void loadUnits() {
        String sql = "SELECT unit_id, unit_name FROM units ORDER BY unit_name";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("unit_name");
                int id = rs.getInt("unit_id");

                unitComboBox.getItems().add(name);
                unitMap.put(name, id);
            }

        } catch (SQLException e) {
            showError("Error", "Failed to load units", e.getMessage());
        }
    }

    private int getLocationId(String locationName) {
        String sql = "SELECT location_id FROM locations WHERE location_name=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {

            p.setString(1, locationName);
            ResultSet r = p.executeQuery();
            return r.next() ? r.getInt(1) : 0;

        } catch (SQLException e) {
            return 0;
        }
    }

}
