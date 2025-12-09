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

    @FXML
    private Button btnPrev, btnNext;
    @FXML
    private Label lblPageInfo;
    @FXML
    private ComboBox<Integer> rowsPerPageCombo;

    // 🔥 Pagination variables (missing in your code)
    private int currentPage = 1;
    private int rowsPerPage = 10;

    private final ObservableList<ScannedItem> scannedList = FXCollections.observableArrayList();
    private final ObservableList<ScannedItem> filteredData = FXCollections.observableArrayList();
    private final ObservableList<ScannedItem> currentPageData = FXCollections.observableArrayList();

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

        tableScannedItems.setItems(currentPageData);

        loadAllScannedItems();

        // 🔥 setup pagination after loading
        setupPagination();
    }

    // ============================
    // FILTER
    // ============================
    @FXML
    private void onFilterClicked() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null) {
            new Alert(Alert.AlertType.WARNING, "Please select both start and end dates.").showAndWait();
            return;
        }

        loadScannedItems(start, end);
        setupPagination();
    }

    @FXML
    private void onClearFilter() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);

        loadAllScannedItems();
        setupPagination();
    }

    // ============================
    // DB LOADERS
    // ============================
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

            filteredData.setAll(scannedList);

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

            filteredData.setAll(scannedList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================
    // PAGINATION LOGIC
    // ============================
    private void setupPagination() {
        rowsPerPageCombo.setItems(FXCollections.observableArrayList(5, 10, 20, 30, 50));
        rowsPerPageCombo.setValue(rowsPerPage);

        rowsPerPageCombo.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal == null) {
                return;  // 🔥 prevent crash
            }
            rowsPerPage = newVal;
            currentPage = 1;
            updatePage();
        });

        btnPrev.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                updatePage();
            }
        });

        btnNext.setOnAction(e -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                updatePage();
            }
        });

        updatePage();
    }

    private void updatePage() {
        int total = filteredData.size();
        int totalPages = getTotalPages();

        if (totalPages == 0) {
            totalPages = 1;
        }

        int from = (currentPage - 1) * rowsPerPage;
        int to = Math.min(from + rowsPerPage, total);

        if (from > to) {
            currentPage = 1;
            from = 0;
            to = Math.min(rowsPerPage, total);
        }

        currentPageData.setAll(filteredData.subList(from, to));

        lblPageInfo.setText("Page " + currentPage + " of " + totalPages);

        btnPrev.setDisable(currentPage == 1);
        btnNext.setDisable(currentPage == totalPages);
    }

    private int getTotalPages() {
        return (int) Math.ceil((double) filteredData.size() / rowsPerPage);
    }

    // ============================
    // VIEW DETAILS
    // ============================
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
            // 🔥 FIX: Prevent the window from stretching vertically
            stage.setMaxHeight(850);
            stage.setMinHeight(850);
            stage.setMaxWidth(650);
            stage.setMinWidth(650);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open details window.").showAndWait();
        }
    }

    // ============================
    // MODEL
    // ============================
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
