package inventorysystem.controllers;

import inventorysystem.dao.ItemDAO;
import inventorysystem.models.Item;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ItemDetailsController {

    @FXML
    private Label lblItemId;
    @FXML
    private Label lblName;
    @FXML
    private Label lblBarcode;
    @FXML
    private Label lblCategory;
    @FXML
    private Label lblUnit;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblAddedBy;
    @FXML
    private Label lblInCharge;
    @FXML
    private Label lblLocation;
    @FXML
    private Label lblDateAcquired;
    @FXML
    private Label lblLastScan;
    @FXML
    private TextArea txtDescription;

    private final ItemDAO itemDAO = new ItemDAO();

    // Human readable formats
    private final DateTimeFormatter dateFormatter
            = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    private final DateTimeFormatter dateTimeFormatter
            = DateTimeFormatter.ofPattern("MMMM dd, yyyy - hh:mm a");

    public void loadItem(int itemId) {

        Item item = itemDAO.getItemById(itemId);

        if (item == null) {
            lblName.setText("Item not found.");
            return;
        }

        // Basic fields
        lblItemId.setText(String.valueOf(item.getItemId()));
        lblName.setText(nullSafe(item.getItemName()));
        lblBarcode.setText(nullSafe(item.getBarcode()));
        lblCategory.setText(nullSafe(item.getCategoryName()));
        lblUnit.setText(nullSafe(item.getUnit()));
        lblStatus.setText(nullSafe(item.getStatus()));
        lblAddedBy.setText(nullSafe(item.getAddedBy()));
        lblInCharge.setText(nullSafe(item.getInChargeName()));
        lblLocation.setText(nullSafe(item.getStorageLocation()));

        // Description
        txtDescription.setText(nullSafe(item.getDescription()));

        // Date Acquired
        LocalDate acquired = item.getDateAcquired();
        lblDateAcquired.setText(
                acquired != null ? acquired.format(dateFormatter) : "—"
        );

        // Last scanned (LocalDateTime)
        LocalDateTime lastScan = item.getLastScanned();
        lblLastScan.setText(
                lastScan != null ? lastScan.format(dateTimeFormatter) : "—"
        );
    }

    private String nullSafe(String s) {
        return (s == null || s.isEmpty()) ? "—" : s;
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) lblName.getScene().getWindow();
        stage.close();
    }
}
