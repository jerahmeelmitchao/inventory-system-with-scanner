package inventorysystem.controllers;

import inventorysystem.dao.ItemDAO;
import inventorysystem.models.Item;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ItemDetailsController {

    @FXML
    private Label lblName;
    @FXML
    private Label lblCategory;
    @FXML
    private Label lblUnit;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblLocation;
    @FXML
    private Label lblLastScan;
    @FXML
    private TextArea txtDescription;

    private final ItemDAO itemDAO = new ItemDAO();

    public void loadItem(int itemId) {
        Item item = itemDAO.getItemById(itemId);

        if (item == null) {
            lblName.setText("Item not found.");
            return;
        }

        lblName.setText(item.getItemName());
        lblCategory.setText(item.getCategoryName());
        lblUnit.setText(item.getUnit());
        lblStatus.setText(item.getStatus());
        lblLocation.setText(item.getStorageLocation());

        lblLastScan.setText(
                item.getLastScanned() != null ? item.getLastScanned().toString() : "—"
        );

        txtDescription.setText(item.getDescription());
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) lblName.getScene().getWindow();
        stage.close();
    }
}
