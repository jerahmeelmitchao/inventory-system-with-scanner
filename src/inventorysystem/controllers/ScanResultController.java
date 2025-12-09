package inventorysystem.controllers;

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

    public void setItem(Item item) {
        this.currentItem = item;

        itemName.setText(item.getItemName());
        description.setText(item.getDescription());
        status.setText(item.getStatus());

        String stat = item.getStatus() == null ? "" : item.getStatus().trim();

        // 🔥 Disable button for items that cannot be borrowed
        if (stat.equalsIgnoreCase("Borrowed")) {

            borrowItemBtn.setDisable(true);
            borrowItemBtn.setText("Already Borrowed");

        } else if (stat.equalsIgnoreCase("Missing")
                || stat.equalsIgnoreCase("Damaged")
                || stat.equalsIgnoreCase("Disposed")) {

            borrowItemBtn.setDisable(true);
            borrowItemBtn.setText("Unavailable");

        } else {
            // ✔ Available for borrowing
            borrowItemBtn.setDisable(false);
            borrowItemBtn.setText("Borrow Item");
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
