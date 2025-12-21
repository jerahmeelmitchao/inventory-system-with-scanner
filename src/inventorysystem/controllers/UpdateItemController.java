package inventorysystem.controllers;

import inventorysystem.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UpdateItemController {

    @FXML
    private TextField itemNameField;
    @FXML
    private TextField barcodeField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ComboBox<String> unitComboBox;

    private final Map<String, Integer> unitMap = new HashMap<>();
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private DatePicker dateAcquiredPicker;

    // ✅ CHANGED
    @FXML
    private ComboBox<String> locationComboBox;
    private final Map<String, Integer> locationMap = new HashMap<>();

    @FXML
    private ComboBox<String> inChargeComboBox;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField addedByField;
    @FXML
    private Button cancelButton;

    private final Map<String, Integer> inChargeMap = new HashMap<>();
    private int editingId;

    @FXML
    public void initialize() {
        loadCategories();
        loadInChargeList();
        loadLocations(); // ✅
        loadUnits();

        statusComboBox.setItems(FXCollections.observableArrayList(
                "Available", "Damaged", "Borrowed", "Missing", "Disposed"
        ));
    }

    private void loadLocations() {
        String sql = "SELECT location_id, location_name FROM locations ORDER BY location_name";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {

            locationComboBox.getItems().clear();
            locationMap.clear();

            while (r.next()) {
                String name = r.getString("location_name");
                int id = r.getInt("location_id");

                locationComboBox.getItems().add(name);
                locationMap.put(name, id);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load locations", e.getMessage());
        }
    }

    public void loadItem(int itemId) {
        editingId = itemId;

        String sql = """
            SELECT 
                i.*,
                c.category_name,
                ic.incharge_name,
                l.location_name,
                u.unit_name
            FROM items i
            LEFT JOIN categories c ON i.category_id = c.category_id
            LEFT JOIN incharge ic ON i.incharge_id = ic.incharge_id
            LEFT JOIN locations l ON i.location_id = l.location_id
            LEFT JOIN units u ON i.unit_id = u.unit_id
            WHERE i.item_id = ?
        """;
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, itemId);
            ResultSet r = p.executeQuery();

            if (r.next()) {
                itemNameField.setText(r.getString("item_name"));
                barcodeField.setText(r.getString("barcode"));
                unitComboBox.setValue(r.getString("unit_name"));
                categoryComboBox.setValue(r.getString("category_name"));
                statusComboBox.setValue(r.getString("status"));

                // ✅ CORRECT: set location NAME
                locationComboBox.setValue(r.getString("location_name"));

                inChargeComboBox.setValue(r.getString("incharge_name"));
                descriptionField.setText(r.getString("description"));
                addedByField.setText(r.getString("added_by"));

                Date d = r.getDate("date_acquired");
                if (d != null) {
                    dateAcquiredPicker.setValue(d.toLocalDate());
                }
            }

        } catch (SQLException e) {
            showAlert("Error", "Failed to load item", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement("""
            UPDATE items SET
                item_name=?,
                category_id=?,
                unit_id=?,
                date_acquired=?,
                status=?,
                location_id=?,
                incharge_id=?,
                description=?
            WHERE item_id=?
        """)) {

            p.setString(1, itemNameField.getText());
            p.setInt(2, getCategoryId(categoryComboBox.getValue()));
            p.setInt(3, unitMap.get(unitComboBox.getValue()));
            p.setDate(4, Date.valueOf(dateAcquiredPicker.getValue()));
            p.setString(5, statusComboBox.getValue());

            // ✅ CORRECT: map name → ID
            p.setInt(6, locationMap.get(locationComboBox.getValue()));

            p.setInt(7, inChargeMap.get(inChargeComboBox.getValue()));
            p.setString(8, descriptionField.getText());
            p.setInt(9, editingId);

            p.executeUpdate();

            showInfo("Success", "Item updated successfully!");
            closeWindow();

        } catch (SQLException e) {
            showAlert("Error", "Update failed", e.getMessage());
        }
    }

    private void loadCategories() {
        try (Connection c = DatabaseConnection.getConnection(); ResultSet r = c.createStatement()
                .executeQuery("SELECT category_name FROM categories")) {
            while (r.next()) {
                categoryComboBox.getItems().add(r.getString(1));
            }
        } catch (SQLException ignored) {
        }
    }

    private void loadInChargeList() {
        try (Connection c = DatabaseConnection.getConnection(); ResultSet r = c.createStatement()
                .executeQuery("SELECT incharge_id, incharge_name FROM incharge")) {
            while (r.next()) {
                inChargeComboBox.getItems().add(r.getString("incharge_name"));
                inChargeMap.put(r.getString("incharge_name"), r.getInt("incharge_id"));
            }
        } catch (SQLException ignored) {
        }
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

    private void closeWindow() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    private void showAlert(String t, String h, String m) {
        new Alert(Alert.AlertType.ERROR, m).show();
    }

    private void showInfo(String t, String m) {
        new Alert(Alert.AlertType.INFORMATION, m).show();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void loadUnits() {
        String sql = "SELECT unit_id, unit_name FROM units ORDER BY unit_name";

        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {

            unitComboBox.getItems().clear();
            unitMap.clear();

            while (r.next()) {
                String name = r.getString("unit_name");
                int id = r.getInt("unit_id");

                unitComboBox.getItems().add(name);
                unitMap.put(name, id);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load units", e.getMessage());
        }
    }
}
