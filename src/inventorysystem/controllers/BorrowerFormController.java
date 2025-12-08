package inventorysystem.controllers;

import inventorysystem.dao.BorrowerDAO;
import inventorysystem.models.Borrower;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class BorrowerFormController {

    @FXML
    private Label titleLabel;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfPosition;
    @FXML
    private ComboBox<String> cbType;

    private BorrowerDAO borrowerDAO = new BorrowerDAO();
    private Borrower borrower;
    private boolean isEdit = false;

    private Runnable onSaveCallback;

    @FXML
    public void initialize() {
        cbType.getItems().addAll("Student", "Teacher", "Staff");
    }

    public void setData(Borrower borrower, boolean isEdit, Runnable onSaveCallback) {
        this.borrower = borrower;
        this.isEdit = isEdit;
        this.onSaveCallback = onSaveCallback;

        titleLabel.setText(isEdit ? "Edit Borrower" : "Add Borrower");

        if (isEdit && borrower != null) {
            tfName.setText(borrower.getBorrowerName());
            tfPosition.setText(borrower.getPosition());
            cbType.setValue(borrower.getBorrowerType());
        }
    }

    @FXML
    private void handleSave() {
        if (tfName.getText().isEmpty() || tfPosition.getText().isEmpty() || cbType.getValue() == null) {
            showAlert("Validation Error", "All fields are required.");
            return;
        }

        if (isEdit) {
            borrower.setBorrowerName(tfName.getText());
            borrower.setPosition(tfPosition.getText());
            borrower.setBorrowerType(cbType.getValue());
            borrowerDAO.updateBorrower(borrower);
        } else {
            Borrower newBorrower = new Borrower(0,
                    tfName.getText(),
                    tfPosition.getText(),
                    cbType.getValue());
            borrowerDAO.insertBorrower(newBorrower);
        }

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }

        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tfName.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
