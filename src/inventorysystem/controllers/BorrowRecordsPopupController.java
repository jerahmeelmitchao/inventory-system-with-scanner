package inventorysystem.controllers;

import inventorysystem.dao.BorrowRecordDAO;
import inventorysystem.models.BorrowRecord;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BorrowRecordsPopupController {

    @FXML
    private TableView<BorrowRecord> recordsTable;
    @FXML
    private TableColumn<BorrowRecord, String> colBorrower;
    @FXML
    private TableColumn<BorrowRecord, String> colBorrowDate;
    @FXML
    private TableColumn<BorrowRecord, String> colReturnDate;
    @FXML
    private TableColumn<BorrowRecord, String> colStatus;

    private static int itemId;

    public static void open(int id) {
        itemId = id;

        try {
            FXMLLoader loader = new FXMLLoader(
                    BorrowRecordsPopupController.class.getResource("/inventorysystem/views/borrow_records_popup.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Borrow Records");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(true);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        BorrowRecordDAO dao = new BorrowRecordDAO();

        recordsTable.setItems(FXCollections.observableArrayList(
                dao.getBorrowRecordsByItemId(itemId)
        ));

        colBorrower.setCellValueFactory(data -> data.getValue().borrowerNameProperty());
        colBorrowDate.setCellValueFactory(data -> data.getValue().borrowDateProperty());
        colReturnDate.setCellValueFactory(data -> data.getValue().returnDateProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
    }

    @FXML
    private void closePopup() {
        Stage stage = (Stage) recordsTable.getScene().getWindow();
        stage.close();
    }

}
