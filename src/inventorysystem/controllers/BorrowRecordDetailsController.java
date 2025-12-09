package inventorysystem.controllers;

import inventorysystem.models.BorrowRecord;
import inventorysystem.dao.ItemDAO;
import inventorysystem.dao.BorrowerDAO;
import inventorysystem.models.Item;
import inventorysystem.models.Borrower;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class BorrowRecordDetailsController {

    @FXML private Label lblRecordId;
    @FXML private Label lblItemName;
    @FXML private Label lblBorrowerName;
    @FXML private Label lblBorrowDate;
    @FXML private Label lblReturnDate;
    @FXML private Label lblStatus;
    @FXML private TextArea txtRemarks;
    @FXML private Button btnClose;

    private final ItemDAO itemDAO = new ItemDAO();
    private final BorrowerDAO borrowerDAO = new BorrowerDAO();

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a");

    /**
     * Called by BorrowRecordsController after loading the FXML.
     */
    public void setRecord(BorrowRecord record) {

        lblRecordId.setText(String.valueOf(record.getRecordId()));

        // Load item name
        Item item = itemDAO.getItemById(record.getItemId());
        lblItemName.setText(item != null ? item.getItemName() : "Unknown");

        // Load borrower name
        Borrower borrower = borrowerDAO.getBorrowerById(record.getBorrowerId());
        lblBorrowerName.setText(borrower != null ? borrower.getBorrowerName() : "Unknown");

        // Format dates
        if (record.getBorrowDate() != null) {
            lblBorrowDate.setText(record.getBorrowDate().format(formatter));
        } else {
            lblBorrowDate.setText("-");
        }

        if (record.getReturnDate() != null) {
            lblReturnDate.setText(record.getReturnDate().format(formatter));
        } else {
            lblReturnDate.setText("-");
        }

        lblStatus.setText(record.getStatus() != null ? record.getStatus() : "-");
        txtRemarks.setText(record.getRemarks() != null ? record.getRemarks() : "");
    }

    @FXML
    private void initialize() {
        btnClose.setOnAction(e -> {
            Stage stage = (Stage) btnClose.getScene().getWindow();
            stage.close();
        });
    }
}
