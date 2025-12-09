package inventorysystem.controllers;

import inventorysystem.dao.BorrowRecordDAO;
import inventorysystem.dao.BorrowerDAO;
import inventorysystem.dao.ItemDAO;
import inventorysystem.models.Borrower;
import inventorysystem.models.Item;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import javafx.scene.control.Alert;

public class BorrowItemPopupController {

    @FXML
    private ComboBox<Borrower> borrowerDropdown;


    // ✅ This is the missing field
    private static Item itemToBorrow;

    // --------------------------------------------------------------
    // 🔥 Method called from ScanResultController → opens popup
    // --------------------------------------------------------------
    public static void open(Item item) {
        itemToBorrow = item;  // <-- FIX: save the item being borrowed

        try {
            FXMLLoader loader = new FXMLLoader(
                    BorrowItemPopupController.class.getResource("/inventorysystem/views/borrow_item_popup.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Borrow Item");

            // Keep popup in front
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(true);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------
    // Load all borrowers when popup opens
    // --------------------------------------------------------------
    @FXML
    public void initialize() {
        loadBorrowers();
    }

    private void loadBorrowers() {
        BorrowerDAO dao = new BorrowerDAO();
        List<Borrower> borrowers = dao.getAllBorrowers();
        borrowerDropdown.setItems(FXCollections.observableArrayList(borrowers));
    }

    // --------------------------------------------------------------
    // Search borrower
    // --------------------------------------------------------------
   

    // --------------------------------------------------------------
    // 🔥 Borrow the item
    // --------------------------------------------------------------
    @FXML
    private void borrowItem() {
        Borrower selected = borrowerDropdown.getValue();
        if (selected == null) {
            System.out.println("⚠ No borrower selected");
            return;
        }

        // Insert borrow record
        BorrowRecordDAO borrowDao = new BorrowRecordDAO();
        borrowDao.insertBorrow(itemToBorrow.getItemId(), selected.getBorrowerId());

        // Update item status to "Borrowed"
        ItemDAO itemDao = new ItemDAO();
        itemDao.updateItemStatus(itemToBorrow.getItemId(), "Borrowed");

        // 🔥 Make alert show in FRONT of the popup
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(borrowerDropdown.getScene().getWindow()); // <-- FIX
        alert.setHeaderText(null);
        alert.setTitle("Success");
        alert.setContentText("Item borrowed successfully!");
        alert.showAndWait();

        System.out.println("✔ Item borrowed successfully!");

        // Close popup AFTER notification
        borrowerDropdown.getScene().getWindow().hide();
    }

    // --------------------------------------------------------------
    // Open Add Borrower form
    // --------------------------------------------------------------
    @FXML
    private void addBorrower() {

        // Close Borrow Item popup
        ((Stage) borrowerDropdown.getScene().getWindow()).close();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/inventorysystem/views/BorrowerForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Borrower");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
