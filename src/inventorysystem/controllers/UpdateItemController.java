package inventorysystem.controllers;

import inventorysystem.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
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
    private TextField unitField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private DatePicker dateAcquiredPicker;
    @FXML
    private TextField locationField;
    @FXML
    private ComboBox<String> inChargeComboBox;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField addedByField;

    @FXML
    private Button cancelButton;

    private final Map<String, Integer> inChargeMap = new HashMap<>();
    private int editingId = -1;

    @FXML
    public void initialize() {
        loadCategories();
        loadInChargeList();

        statusComboBox.setItems(FXCollections.observableArrayList(
                "Available", "Damaged", "Borrowed", "Missing", "Disposed"
        ));
    }

    public void loadItem(int itemId) {
        this.editingId = itemId;

        String sql = """
            SELECT i.*, c.category_name, ic.incharge_name
            FROM items i
            LEFT JOIN categories c ON i.category_id = c.category_id
            LEFT JOIN incharge ic ON i.incharge_id = ic.incharge_id
            WHERE item_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                itemNameField.setText(rs.getString("item_name"));
                barcodeField.setText(rs.getString("barcode"));
                unitField.setText(rs.getString("unit"));
                categoryComboBox.setValue(rs.getString("category_name"));
                statusComboBox.setValue(rs.getString("status"));
                locationField.setText(rs.getString("storage_location"));
                inChargeComboBox.setValue(rs.getString("incharge_name"));
                descriptionField.setText(rs.getString("description"));
                addedByField.setText(rs.getString("added_by"));

                Date d = rs.getDate("date_acquired");
                if (d != null) {
                    dateAcquiredPicker.setValue(d.toLocalDate());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load item", e.getMessage());
        }
    }

    private void loadCategories() {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT category_name FROM categories"); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categoryComboBox.getItems().add(rs.getString("category_name"));
            }

        } catch (SQLException ignored) {
        }
    }

    private void loadInChargeList() {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT incharge_id, incharge_name FROM incharge"); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                inChargeComboBox.getItems().add(rs.getString("incharge_name"));
                inChargeMap.put(rs.getString("incharge_name"), rs.getInt("incharge_id"));
            }

        } catch (SQLException ignored) {
        }
    }

    @FXML
    private void handleUpdate() {
        if (editingId <= 0) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("""
                UPDATE items SET 
                    item_name=?, category_id=?, unit=?, date_acquired=?, 
                    status=?, storage_location=?, incharge_id=?, description=?
                WHERE item_id=?
            """)) {

            ps.setString(1, itemNameField.getText());
            ps.setInt(2, getCategoryId(categoryComboBox.getValue()));
            ps.setString(3, unitField.getText());
            ps.setDate(4, Date.valueOf(dateAcquiredPicker.getValue()));
            ps.setString(5, statusComboBox.getValue());
            ps.setString(6, locationField.getText());
            ps.setInt(7, inChargeMap.get(inChargeComboBox.getValue()));
            ps.setString(8, descriptionField.getText());
            ps.setInt(9, editingId);

            ps.executeUpdate();

            showInfo("Success", "Item updated successfully!");
            closeWindow();

        } catch (Exception e) {
            showAlert("Error", "Failed to update item", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage st = (Stage) cancelButton.getScene().getWindow();
        st.close();
    }

    private int getCategoryId(String name) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT category_id FROM categories WHERE category_name=?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private void showAlert(String title, String head, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setTitle(title);
        a.setHeaderText(head);
        a.show();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setTitle(title);
        a.show();
    }
}
