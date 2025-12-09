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
    @FXML
    private TextField locationField;
    @FXML
    private ComboBox<String> inChargeComboBox;

    @FXML
    private TextArea descriptionField;   // <-- NEW

    @FXML
    private TextField addedByField; // auto username
    @FXML
    private Button saveButton;
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

        dateAcquiredPicker.setValue(LocalDate.now());

        // auto-fill username only
        String username = ItemController.getLoggedUsername();
        addedByField.setText(username);
        addedByField.setEditable(false);
        addedByField.setStyle("-fx-opacity: 0.8;");
    }

    private void loadCategories() {
        String sql = "SELECT category_name FROM categories ORDER BY category_name ASC";

        try (Connection conn = DatabaseConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
            categoryComboBox.setItems(categories);

        } catch (SQLException e) {
            showError("Error", "Failed to load categories.", e.getMessage());
        }
    }

    private void loadInChargeList() {
        String sql = "SELECT incharge_id, incharge_name FROM incharge ORDER BY incharge_name ASC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            inCharges.clear();
            inChargeMap.clear();

            while (rs.next()) {
                String name = rs.getString("incharge_name");
                int id = rs.getInt("incharge_id");
                inCharges.add(name);
                inChargeMap.put(name, id);
            }

            inChargeComboBox.setItems(inCharges);

        } catch (SQLException e) {
            showError("Error", "Failed to load in-charge.", e.getMessage());
        }
    }

    private Integer getInChargeId(String name) {
        return name == null ? null : inChargeMap.get(name);
    }

    private void setupDropdownOptions() {
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Available", "Damaged", "Borrowed", "Missing", "Disposed"
        ));
    }

    // BARCODE GENERATOR
    private String generateBarcode() {
        String code = Long.toHexString(RANDOM.nextLong()).toUpperCase();
        while (code.length() < 12) {
            code = "0" + code;
        }
        return code.substring(0, 12);
    }

    private boolean barcodeExists(String code) {
        String sql = "SELECT 1 FROM items WHERE barcode = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return true; // fail safe
        }
    }

    private String generateUniqueBarcode() {
        String code;
        do {
            code = generateBarcode();
        } while (barcodeExists(code));
        return code;
    }

    // SAVE
    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }

        String name = itemNameField.getText().trim();
        String category = categoryComboBox.getValue();
        String unit = unitField.getText().trim();
        LocalDate dateAcquired = dateAcquiredPicker.getValue();
        String status = statusComboBox.getValue();
        String location = locationField.getText().trim();
        String inCharge = inChargeComboBox.getValue();
        String description = descriptionField.getText().trim();
        String addedBy = addedByField.getText().trim();

        Integer categoryId = getCategoryId(category);
        Integer inChargeId = getInChargeId(inCharge);

        if (categoryId == null || inChargeId == null) {
            showError("Error", "Category or in-charge not valid.", null);
            return;
        }

        saveNewItem(name, categoryId, unit, dateAcquired, status, location, inChargeId, addedBy, description);
    }

    private Integer getCategoryId(String name) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(
                "SELECT category_id FROM categories WHERE category_name=?")) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : null;

        } catch (SQLException e) {
            return null;
        }
    }

    private void saveNewItem(String name, int categoryId, String unit, LocalDate dateAcquired,
            String status, String location, int inChargeId,
            String addedBy, String description) {

        String barcode = generateUniqueBarcode();

        String sql = """
            INSERT INTO items (
                item_name, barcode, category_id, unit, description,
                date_acquired, status, storage_location, incharge_id, added_by
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, barcode);
            ps.setInt(3, categoryId);
            ps.setString(4, unit);
            ps.setString(5, description);
            ps.setDate(6, Date.valueOf(dateAcquired));
            ps.setString(7, status);
            ps.setString(8, location);
            ps.setInt(9, inChargeId);
            ps.setString(10, addedBy);

            ps.executeUpdate();

            showInfo("Success", "Item added successfully!\nGenerated Barcode: " + barcode);
            clearForm();

        } catch (SQLException e) {
            showError("Error", "Failed to add item.", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage st = (Stage) cancelButton.getScene().getWindow();
        st.close();
    }

    private boolean validateInputs() {
        return !(itemNameField.getText().isEmpty()
                || categoryComboBox.getValue() == null
                || unitField.getText().isEmpty()
                || dateAcquiredPicker.getValue() == null
                || statusComboBox.getValue() == null
                || locationField.getText().isEmpty()
                || inChargeComboBox.getValue() == null);
    }

    private void clearForm() {
        itemNameField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        unitField.clear();
        statusComboBox.getSelectionModel().clearSelection();
        dateAcquiredPicker.setValue(LocalDate.now());
        locationField.clear();
        inChargeComboBox.getSelectionModel().clearSelection();
        descriptionField.clear();  // <-- clear description too
    }

    private void showError(String title, String header, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setTitle(title);
        a.setHeaderText(header);
        a.show();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setTitle(title);
        a.show();
    }
}
