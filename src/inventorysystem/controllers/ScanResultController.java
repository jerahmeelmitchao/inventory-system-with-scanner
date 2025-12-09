package inventorysystem.controllers;

import inventorysystem.dao.AuditLogDAO;
import inventorysystem.models.Item;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ScanResultController {

    @FXML
    private Label itemName;
    @FXML
    private Label description;
    @FXML
    private Label status;

    private Item currentItem;
    @FXML
    private Button borrowItemBtn;

    private void logAction(String action, String details) {
        AuditLogDAO.log(ItemController.getLoggedUsername(), action, details);
    }

    public void setItem(Item item) {
        this.currentItem = item;

        itemName.setText(item.getItemName());
        description.setText(item.getDescription());
        status.setText(item.getStatus());

        // 🔥 Disable Borrow Item button if already borrowed
        if ("Borrowed".equalsIgnoreCase(item.getStatus())) {
            borrowItemBtn.setDisable(true);
            borrowItemBtn.setText("Already Borrowed");
        } else {
            borrowItemBtn.setDisable(false);
        }
    }

    @FXML
    private void handleBorrowItem() {
        // Close Scan Popup
        ((Stage) itemName.getScene().getWindow()).close();
        BorrowItemPopupController.open(currentItem);
    }

    @FXML
    private void handleBorrowRecords() {
        ((Stage) itemName.getScene().getWindow()).close();
        BorrowRecordsPopupController.open(currentItem.getItemId());
    }

}
