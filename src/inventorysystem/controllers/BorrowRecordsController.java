package inventorysystem.controllers;

import inventorysystem.dao.AuditLogDAO;
import inventorysystem.dao.BorrowRecordDAO;
import inventorysystem.dao.ItemDAO;
import inventorysystem.dao.BorrowerDAO;

import inventorysystem.models.BorrowRecord;
import inventorysystem.models.Borrower;
import inventorysystem.models.Item;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class BorrowRecordsController {

    // ===================== UI Components =====================
    @FXML
    private TableView<BorrowRecord> borrowTable;

    @FXML
    private TableColumn<BorrowRecord, Integer> colRecordId; // <== changed to Integer
    @FXML
    private TableColumn<BorrowRecord, String> colItemName;
    @FXML
    private TableColumn<BorrowRecord, String> colBorrowerName;
    @FXML
    private TableColumn<BorrowRecord, String> colBorrowDate;
    @FXML
    private TableColumn<BorrowRecord, String> colReturnDate;
    @FXML
    private TableColumn<BorrowRecord, String> colStatus;
    @FXML
    private TableColumn<BorrowRecord, String> colRemarks;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> statusFilterCombo;
    @FXML
    private Button clearFilterBtn;

    @FXML
    private Button prevPageBtn;
    @FXML
    private Button nextPageBtn;
    @FXML
    private Label pageInfoLabel;
    @FXML
    private ComboBox<Integer> rowsPerPageCombo;

    @FXML
    private Button viewDetailsBtn;

    // ===================== DAO + Data Lists =====================
    private final BorrowRecordDAO borrowDAO = new BorrowRecordDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final BorrowerDAO borrowerDAO = new BorrowerDAO();

    private ObservableList<BorrowRecord> masterList = FXCollections.observableArrayList();
    private ObservableList<BorrowRecord> filteredList = FXCollections.observableArrayList();

    private int currentPage = 1;
    private int rowsPerPage = 10;

    private final DateTimeFormatter formatter
            = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a");

    private void logAction(String action, String details) {
        AuditLogDAO.log(ItemController.getLoggedUsername(), action, details);
    }

    // ===================== INITIALIZE =====================
    @FXML
    public void initialize() {
        setupColumns();
        setupFilters();
        setupPagination();

        // instead of setupButtons()
        viewDetailsBtn.setOnAction(e -> openDetailsForSelectedRow());

        loadRecords();
        applyFilters();
    }

    // ===================== SETUP TABLE COLUMNS =====================
    private void setupColumns() {

        colRecordId.setCellValueFactory(c
                -> new SimpleIntegerProperty(c.getValue().getRecordId()).asObject()
        );

        colItemName.setCellValueFactory(c -> {
            Item item = itemDAO.getItemById(c.getValue().getItemId());
            String name = item != null ? item.getItemName() : "Unknown";
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        colBorrowerName.setCellValueFactory(c -> {
            Borrower b = borrowerDAO.getBorrowerById(c.getValue().getBorrowerId());
            String name = b != null ? b.getBorrowerName() : "Unknown";
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        colBorrowDate.setCellValueFactory(c -> {
            if (c.getValue().getBorrowDate() == null) {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
            return new javafx.beans.property.SimpleStringProperty(
                    c.getValue().getBorrowDate().format(formatter)
            );
        });

        colReturnDate.setCellValueFactory(c -> {
            if (c.getValue().getReturnDate() == null) {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
            return new javafx.beans.property.SimpleStringProperty(
                    c.getValue().getReturnDate().format(formatter)
            );
        });

        colStatus.setCellValueFactory(c
                -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus())
        );

        colRemarks.setCellValueFactory(c
                -> new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getRemarks() != null ? c.getValue().getRemarks() : ""
                )
        );
    }

    // ===================== LOADING RECORDS =====================
    private void loadRecords() {
        masterList = FXCollections.observableArrayList(borrowDAO.getAllBorrowRecords());
    }

    // ===================== FILTERING =====================
    private void setupFilters() {

        statusFilterCombo.setItems(FXCollections.observableArrayList(
                "All", "Borrowed", "Returned"
        ));
        statusFilterCombo.setValue("All");

        searchField.textProperty().addListener((a, b, c) -> applyFilters());
        statusFilterCombo.valueProperty().addListener((a, b, c) -> applyFilters());
        clearFilterBtn.setOnAction(e -> clearFilters());
    }

    private void clearFilters() {
        searchField.clear();
        statusFilterCombo.setValue("All");
        applyFilters();
    }

    private void applyFilters() {

        String search = searchField.getText().toLowerCase().trim();
        String status = statusFilterCombo.getValue();

        filteredList = masterList.filtered(record -> {

            Item item = itemDAO.getItemById(record.getItemId());
            Borrower borrower = borrowerDAO.getBorrowerById(record.getBorrowerId());

            String itemName = item != null ? item.getItemName().toLowerCase() : "";
            String borrowerName = borrower != null ? borrower.getBorrowerName().toLowerCase() : "";

            boolean matchSearch
                    = itemName.contains(search)
                    || borrowerName.contains(search)
                    || record.getStatus().toLowerCase().contains(search)
                    || (record.getRemarks() != null && record.getRemarks().toLowerCase().contains(search));

            boolean matchStatus
                    = status.equals("All") || record.getStatus().equalsIgnoreCase(status);

            return matchSearch && matchStatus;
        });

        currentPage = 1;
        updateTablePage();
    }

    // ===================== PAGINATION =====================
    private void setupPagination() {
        rowsPerPageCombo.setItems(FXCollections.observableArrayList(5, 10, 20, 50));
        rowsPerPageCombo.setValue(10);

        rowsPerPageCombo.valueProperty().addListener((a, b, c) -> {
            rowsPerPage = c;
            currentPage = 1;
            updateTablePage();
        });

        prevPageBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTablePage();
            }
        });

        nextPageBtn.setOnAction(e -> {
            int maxPage = Math.max(1, (int) Math.ceil(filteredList.size() / (double) rowsPerPage));
            if (currentPage < maxPage) {
                currentPage++;
                updateTablePage();
            }
        });
    }

    private void updateTablePage() {
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, filteredList.size());

        if (start > end) {
            start = 0;
        }

        borrowTable.setItems(FXCollections.observableArrayList(filteredList.subList(start, end)));

        int maxPage = Math.max(1, (int) Math.ceil(filteredList.size() / (double) rowsPerPage));
        pageInfoLabel.setText("Page " + currentPage + " of " + maxPage);
    }

    // ===================== DETAILS POPUP =====================
    private void openDetailsForSelectedRow() {

        BorrowRecord selected = borrowTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a record to view details.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/inventorysystem/views/borrow_record_details.fxml"
            ));

            Parent root = loader.load();

            BorrowRecordDetailsController controller = loader.getController();
            controller.setRecord(selected); // Pure BorrowRecord

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Borrow Record Details");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open details window.");
        }
    }

    // ===================== UTIL =====================
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }
}
