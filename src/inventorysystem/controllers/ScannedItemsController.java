package inventorysystem.controllers;

import inventorysystem.dao.AuditLogDAO;
import inventorysystem.utils.DatabaseConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ScannedItemsController {

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableView<ScannedItem> tableScannedItems;
    @FXML
    private TableColumn<ScannedItem, String> colItemName;
    @FXML
    private TableColumn<ScannedItem, String> colScanDate;

    private final ObservableList<ScannedItem> scannedList = FXCollections.observableArrayList();
    private final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    private void logAction(String action, String details) {
        AuditLogDAO.log(ItemController.getLoggedUsername(), action, details);
    }

    @FXML
    public void initialize() {

        colItemName.setCellValueFactory(data -> data.getValue().itemNameProperty());

        // Format scan date to human readable
        colScanDate.setCellValueFactory(data -> {
            String raw = data.getValue().getScanDate();
            String formatted = raw;
            try {
                if (raw != null && raw.length() >= 10) {
                    LocalDate date = LocalDate.parse(raw.substring(0, 10));
                    formatted = date.format(displayFormatter);
                }
            } catch (Exception ignored) {
            }
            return new SimpleStringProperty(formatted);
        });

        tableScannedItems.setItems(scannedList);

        // 🔥 Load all scanned items when page opens
        loadAllScannedItems();
    }

    @FXML
    private void onFilterClicked() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null) {
            new Alert(Alert.AlertType.WARNING, "Please select both start and end dates.").showAndWait();
            return;
        }

        loadScannedItems(start, end);
    }

    private void loadScannedItems(LocalDate start, LocalDate end) {
        scannedList.clear();

        String sql = """
            SELECT s.item_id, i.item_name, s.scan_date
            FROM scan_log s
            JOIN items i ON s.item_id = i.item_id
            WHERE DATE(s.scan_date) BETWEEN ? AND ?
            ORDER BY s.scan_date DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                scannedList.add(new ScannedItem(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getString("scan_date")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAllScannedItems() {
        scannedList.clear();

        String sql = """
            SELECT s.item_id, i.item_name, s.scan_date
            FROM scan_log s
            JOIN items i ON s.item_id = i.item_id
            ORDER BY s.scan_date DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                scannedList.add(new ScannedItem(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getString("scan_date")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onViewDetails() {
        ScannedItem selected = tableScannedItems.getSelectionModel().getSelectedItem();

        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an item.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventorysystem/views/item_details.fxml"));
            Parent root = loader.load();

            ItemDetailsController controller = loader.getController();
            controller.loadItem(selected.getItemId());

            Stage stage = new Stage();
            stage.setTitle("Item Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open details window.").showAndWait();
        }
    }

    // ===============================
    // UI Table Row Model
    // ===============================
    public static class ScannedItem {

        private final int itemId;
        private final SimpleStringProperty itemName;
        private final SimpleStringProperty scanDate;

        public ScannedItem(int itemId, String itemName, String scanDate) {
            this.itemId = itemId;
            this.itemName = new SimpleStringProperty(itemName);
            this.scanDate = new SimpleStringProperty(scanDate);
        }

        public int getItemId() {
            return itemId;
        }

        public String getItemName() {
            return itemName.get();
        }

        public String getScanDate() {
            return scanDate.get();
        }

        public SimpleStringProperty itemNameProperty() {
            return itemName;
        }

        public SimpleStringProperty scanDateProperty() {
            return scanDate;
        }
    }
}
