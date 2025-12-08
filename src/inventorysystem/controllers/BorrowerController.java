package inventorysystem.controllers;

import inventorysystem.dao.BorrowerDAO;
import inventorysystem.dao.BorrowRecordDAO;
import inventorysystem.dao.ItemDAO;
import inventorysystem.models.BorrowRecord;
import inventorysystem.models.Borrower;
import inventorysystem.models.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BorrowerController {

    // Table
    @FXML
    private TableView<Borrower> borrowerTable;
    @FXML
    private TableColumn<Borrower, Integer> colId;
    @FXML
    private TableColumn<Borrower, String> colName;
    @FXML
    private TableColumn<Borrower, String> colPosition;
    @FXML
    private TableColumn<Borrower, String> colType;
    @FXML
    private TableColumn<Borrower, Void> colActions;

    // Filters
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterTypeCombo;
    @FXML
    private ComboBox<String> filterPositionCombo;
    @FXML
    private Button clearFilterButton;

    // Buttons
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button cancelButton;

    private final BorrowerDAO borrowerDAO = new BorrowerDAO();
    private final BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO();
    private final ItemDAO itemDAO = new ItemDAO();

    private ObservableList<Borrower> borrowerList;
    private Borrower selectedBorrower;

    // -----------------------------------------
    // INITIALIZE
    // -----------------------------------------
    @FXML
    public void initialize() {

        colId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getBorrowerId()).asObject());
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBorrowerName()));
        colPosition.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPosition()));
        colType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBorrowerType()));

        setupActionColumn();
        loadBorrowers();
        populatePositionFilter();
        setupFilters();

        // FIX: Enable update/delete when table row selected
        borrowerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedBorrower = newSel;

            boolean hasSelection = newSel != null;

            updateButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
            cancelButton.setVisible(hasSelection);
        });

        handleCancel();
    }

    // -----------------------------------------
    // LOAD DATA
    // -----------------------------------------
    private void loadBorrowers() {
        borrowerList = FXCollections.observableArrayList(borrowerDAO.getAllBorrowers());
        borrowerTable.setItems(borrowerList);
        setupActionColumn(); // <<<<<< important
    }

    private void populatePositionFilter() {
        ObservableList<String> positions = FXCollections.observableArrayList("All");
        borrowerList.forEach(b -> {
            if (!positions.contains(b.getPosition())) {
                positions.add(b.getPosition());
            }
        });

        filterPositionCombo.setItems(positions);
        filterPositionCombo.setValue("All");
    }

    // -----------------------------------------
    // FILTERING
    // -----------------------------------------
    private void setupFilters() {

        filterTypeCombo.setItems(FXCollections.observableArrayList("All", "Student", "Teacher", "Staff"));
        filterTypeCombo.setValue("All");

        filterPositionCombo.setValue("All");

        searchField.textProperty().addListener((a, b, c) -> applyFilters());
        filterTypeCombo.valueProperty().addListener((a, b, c) -> applyFilters());
        filterPositionCombo.valueProperty().addListener((a, b, c) -> applyFilters());
    }

    private void applyFilters() {

        String search = searchField.getText().toLowerCase().trim();
        String type = filterTypeCombo.getValue();
        String position = filterPositionCombo.getValue();

        ObservableList<Borrower> filtered = borrowerList.filtered(b -> {

            boolean matchSearch
                    = b.getBorrowerName().toLowerCase().contains(search)
                    || b.getPosition().toLowerCase().contains(search);

            boolean matchType
                    = type.equals("All") || b.getBorrowerType().equalsIgnoreCase(type);

            boolean matchPosition
                    = position.equals("All") || b.getPosition().equalsIgnoreCase(position);

            return matchSearch && matchType && matchPosition;
        });

        borrowerTable.setItems(filtered);
        setupActionColumn(); // <<<<<<<<<<<<<<<<<< REAPPLY ACTION COLUMN
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        filterTypeCombo.setValue("All");
        filterPositionCombo.setValue("All");

        borrowerTable.setItems(borrowerList);
        setupActionColumn(); // <<<<<<<<<<<<<<<< REAPPLY ACTION COLUMN
    }

    // -----------------------------------------
    // CRUD BUTTONS
    // -----------------------------------------
    @FXML
    private void handleAdd() {
        openForm(null, false);
    }

    @FXML
    private void handleUpdate() {
        Borrower selected = borrowerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Select a borrower first.");
            return;
        }
        openForm(selected, true);
    }

    @FXML
    private void handleDelete() {
        if (selectedBorrower == null) {
            return;
        }

        if (borrowerDAO.deleteBorrower(selectedBorrower.getBorrowerId())) {
            borrowerList.remove(selectedBorrower);
            populatePositionFilter();
            handleCancel();
        }
    }

    @FXML
    private void handleCancel() {
        borrowerTable.getSelectionModel().clearSelection();
    }

    // -----------------------------------------
    // ACTION COLUMN (Borrow / Return)
    // -----------------------------------------
    private void setupActionColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {

            private final Button borrowBtn = new Button("Borrow");
            private final Button returnBtn = new Button("Return");
            private final HBox container = new HBox(10, borrowBtn, returnBtn);

            {
                borrowBtn.setOnAction(e -> openBorrowPanel(getTableView().getItems().get(getIndex())));

                returnBtn.setOnAction(e -> openReturnPanel(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Borrower borrower = getTableView().getItems().get(getIndex());
                boolean hasBorrowed = borrowRecordDAO.getBorrowRecordsByBorrower(borrower.getBorrowerId())
                        .stream()
                        .anyMatch(r -> "Borrowed".equals(r.getStatus()));

                returnBtn.setDisable(!hasBorrowed);
                setGraphic(container);
            }
        });
    }

    // -----------------------------------------
    // BORROW + RETURN PANELS
    // -----------------------------------------
    private void openBorrowPanel(Borrower borrower) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Borrow Item");

        TextField search = new TextField();
        search.setPromptText("Search item...");

        ListView<Item> listView = new ListView<>();
        ObservableList<Item> items = FXCollections.observableArrayList(itemDAO.getAllItems());
        listView.setItems(items);

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getItemName() + " (" + item.getStatus() + ")");
            }
        });

        // Search filter
        search.textProperty().addListener((obs, old, val) -> {
            listView.setItems(items.filtered(i
                    -> i.getItemName().toLowerCase().contains(val.toLowerCase())));
        });

        VBox content = new VBox(10, search, listView);
        dialog.getDialogPane().setContent(content);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {
                Item item = listView.getSelectionModel().getSelectedItem();
                if (item != null && item.getStatus().equals("Available")) {

                    itemDAO.updateItemStatus(item.getItemId(), "Borrowed");

                    BorrowRecord record = new BorrowRecord(
                            0, item.getItemId(), borrower.getBorrowerId(),
                            LocalDate.now(), null, "Borrowed"
                    );
                    borrowRecordDAO.addBorrowRecord(record);

                    showAlert("Success", "Item borrowed successfully!");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void openReturnPanel(Borrower borrower) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Return Item");

        List<BorrowRecord> active = borrowRecordDAO
                .getBorrowRecordsByBorrower(borrower.getBorrowerId())
                .stream().filter(r -> r.getStatus().equals("Borrowed")).toList();

        ListView<BorrowRecord> listView = new ListView<>(FXCollections.observableArrayList(active));

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BorrowRecord r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? null
                        : "Item ID: " + r.getItemId() + " | Borrowed: " + r.getBorrowDate());
            }
        });

        TextArea remarks = new TextArea();
        remarks.setPromptText("Remarks...");

        VBox content = new VBox(10, listView, new Label("Remarks:"), remarks);
        dialog.getDialogPane().setContent(content);

        ButtonType saveBtn = new ButtonType("Return", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {
                BorrowRecord rec = listView.getSelectionModel().getSelectedItem();
                if (rec != null) {

                    borrowRecordDAO.returnBorrowRecord(rec.getRecordId(), LocalDate.now(), remarks.getText());

                    String newStatus = remarks.getText().toLowerCase().contains("damaged")
                            ? "Damaged" : "Available";

                    itemDAO.updateItemStatus(rec.getItemId(), newStatus);

                    showAlert("Success", "Item returned successfully!");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // -----------------------------------------
    // UTIL
    // -----------------------------------------
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showBorrowerDetails(Borrower borrower) {
        List<BorrowRecord> records = borrowRecordDAO.getBorrowRecordsByBorrower(borrower.getBorrowerId());

        StringBuilder sb = new StringBuilder();
        sb.append("Borrower: ").append(borrower.getBorrowerName()).append("\n")
                .append("Position: ").append(borrower.getPosition()).append("\n")
                .append("Type: ").append(borrower.getBorrowerType()).append("\n\n");

        for (BorrowRecord r : records) {
            sb.append("Item ID: ").append(r.getItemId())
                    .append(" | Status: ").append(r.getStatus())
                    .append(" | Borrowed: ").append(r.getBorrowDate())
                    .append(" | Returned: ").append(r.getReturnDate() != null ? r.getReturnDate() : "-")
                    .append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Borrower Details");
        alert.setHeaderText("Borrower Information");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    private void showAddEditDialog(Borrower borrower, boolean isEdit) {

        Dialog<Borrower> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit In-Charge" : "Add In-Charge");

        // Transparent overlay
        dialog.getDialogPane().setStyle("-fx-background-color: transparent;");

        // Container
        VBox root = new VBox(12);
        root.setStyle(
                "-fx-background-radius: 12; "
                + "-fx-padding: 20; "
                + "-fx-background-color: white; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0, 0, 3);"
        );

        Label title = new Label(isEdit ? "Edit In-Charge" : "Add In-Charge");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField tfName = new TextField();
        tfName.setPromptText("Enter name");

        TextField tfPosition = new TextField();
        tfPosition.setPromptText("Enter position");

        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("Student", "Teacher", "Staff");
        cbType.setPromptText("Select type");

        // Fill fields when editing
        if (borrower != null) {
            tfName.setText(borrower.getBorrowerName());
            tfPosition.setText(borrower.getPosition());
            cbType.setValue(borrower.getBorrowerType());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(tfName, 1, 0);

        grid.add(new Label("Position:"), 0, 1);
        grid.add(tfPosition, 1, 1);

        grid.add(new Label("Type:"), 0, 2);
        grid.add(cbType, 1, 2);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button btnSave = new Button("Save");
        btnSave.setStyle("-fx-background-color:#007bff; -fx-text-fill:white;");

        Button btnCancel = new Button("Cancel");

        buttons.getChildren().addAll(btnSave, btnCancel);

        root.getChildren().addAll(title, grid, buttons);

        dialog.getDialogPane().setContent(root);

        // Fade-in animation
        FadeTransition ft = new FadeTransition(Duration.millis(200), root);
        root.setOpacity(0);
        ft.setToValue(1);
        ft.play();

        // Close popup
        btnCancel.setOnAction(e -> dialog.close());

        btnSave.setOnAction(e -> {

            if (tfName.getText().isEmpty()
                    || tfPosition.getText().isEmpty()
                    || cbType.getValue() == null) {

                showAlert("Validation Error", "All fields are required!");
                return;
            }

            if (isEdit) {
                borrower.setBorrowerName(tfName.getText());
                borrower.setPosition(tfPosition.getText());
                borrower.setBorrowerType(cbType.getValue());

                borrowerDAO.updateBorrower(borrower);
                borrowerTable.refresh();
            } else {
                Borrower b = new Borrower(0, tfName.getText(), tfPosition.getText(), cbType.getValue());
                borrowerDAO.insertBorrower(b);
                borrowerList.add(b);
            }

            dialog.close();
        });

        dialog.showAndWait();
    }

    private void openForm(Borrower borrower, boolean isEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventorysystem/views/BorrowerForm.fxml"));
            Parent root = loader.load();

            BorrowerFormController controller = loader.getController();
            controller.setData(borrower, isEdit, () -> {
                loadBorrowers();
                borrowerTable.refresh();
            });

            Stage stage = new Stage();
            stage.setTitle(isEdit ? "Edit Borrower" : "Add Borrower");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
