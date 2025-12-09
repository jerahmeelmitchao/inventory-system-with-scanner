package inventorysystem.controllers;

import inventorysystem.dao.AuditLogDAO;
import inventorysystem.dao.BorrowRecordDAO;
import inventorysystem.dao.BorrowerDAO;
import inventorysystem.dao.ItemDAO;
import inventorysystem.models.Borrower;
import inventorysystem.models.Item;
import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class BorrowItemPopupController {

    @FXML
    private ComboBox<Borrower> borrowerDropdown;

    private static Item itemToBorrow;
    private Stage popupStage;

    // ------------------------------
    // 🔥 Open popup
    // ------------------------------
    public static void open(Item item) {
        itemToBorrow = item;

        try {
            FXMLLoader loader = new FXMLLoader(
                    BorrowItemPopupController.class.getResource("/inventorysystem/views/borrow_item_popup.fxml"));
            Parent root = loader.load();

            BorrowItemPopupController ctrl = loader.getController();

            Stage stage = new Stage();
            ctrl.popupStage = stage;

            stage.setScene(new Scene(root));
            stage.setTitle("Borrow Item");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(true);

            // Fade-in animation
            FadeTransition ft = new FadeTransition(Duration.millis(180), root);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logAction(String action, String details) {
        AuditLogDAO.log(ItemController.getLoggedUsername(), action, details);
    }

    // ------------------------------
    // Load borrowers
    // ------------------------------
    @FXML
    public void initialize() {
        BorrowerDAO dao = new BorrowerDAO();
        List<Borrower> borrowers = dao.getAllBorrowers();
        borrowerDropdown.setItems(FXCollections.observableArrayList(borrowers));
    }

    // ------------------------------
    // Borrow item button
    // ------------------------------
    @FXML
    private void borrowItem() {
        Borrower selected = borrowerDropdown.getValue();
        if (selected == null) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Select a borrower first.");
            a.initOwner(popupStage);
            a.showAndWait();
            return;
        }

        BorrowRecordDAO borrowDao = new BorrowRecordDAO();
        borrowDao.insertBorrow(itemToBorrow.getItemId(), selected.getBorrowerId());

        ItemDAO itemDao = new ItemDAO();
        itemDao.updateItemStatus(itemToBorrow.getItemId(), "Borrowed");

        Alert ok = new Alert(Alert.AlertType.INFORMATION, "Item borrowed successfully!");
        ok.initOwner(popupStage);
        ok.showAndWait();

        popupStage.close();
    }

    // ------------------------------
    // Add Borrower button
    // ------------------------------
    @FXML
    private void addBorrower() {
        popupStage.close();

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

    // ------------------------------
    // Cancel button
    // ------------------------------
    @FXML
    private void cancelPopup() {
        popupStage.close();
    }
}
