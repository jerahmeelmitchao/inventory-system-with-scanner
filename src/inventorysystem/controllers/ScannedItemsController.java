package inventorysystem.controllers;

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

    @FXML
    public void initialize() {

        colItemName.setCellValueFactory(data -> data.getValue().itemNameProperty());

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
            SELECT i.item_name, s.scan_date
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

            // FIX: Use Parent, not AnchorPane
            Parent root = loader.load();

            ItemDetailsController controller = loader.getController();
            controller.loadItem(selected.getItemName());

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

    // Data model for the table
    public static class ScannedItem {

        private final SimpleStringProperty itemName;
        private final SimpleStringProperty scanDate;

        public ScannedItem(String itemName, String scanDate) {
            this.itemName = new SimpleStringProperty(itemName);
            this.scanDate = new SimpleStringProperty(scanDate);
        }

        public String getItemName() {
            return itemName.get();
        }

        public SimpleStringProperty itemNameProperty() {
            return itemName;
        }

        public String getScanDate() {
            return scanDate.get();
        }

        public SimpleStringProperty scanDateProperty() {
            return scanDate;
        }
    }
}
