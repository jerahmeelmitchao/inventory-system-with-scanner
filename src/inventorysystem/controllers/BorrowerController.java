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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDateTime;
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
//        filterPositionCombo.setValue("All");
    }

    // -----------------------------------------
    // FILTERING
    // -----------------------------------------
    private void setupFilters() {

        filterTypeCombo.setItems(FXCollections.observableArrayList("All", "Student", "Teacher", "Staff"));
//        filterTypeCombo.setValue("All");

//        filterPositionCombo.setValue("All");
        searchField.textProperty().addListener((a, b, c) -> applyFilters());
        filterTypeCombo.valueProperty().addListener((a, b, c) -> applyFilters());
        filterPositionCombo.valueProperty().addListener((a, b, c) -> applyFilters());
    }

    private void applyFilters() {

        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
        String type = filterTypeCombo.getValue();
        String position = filterPositionCombo.getValue();

        ObservableList<Borrower> filtered = borrowerList.filtered(b -> {

            // SAFE STRINGS (never null)
            String name = b.getBorrowerName() == null ? "" : b.getBorrowerName().toLowerCase();
            String pos = b.getPosition() == null ? "" : b.getPosition().toLowerCase();
            String bType = b.getBorrowerType() == null ? "" : b.getBorrowerType().toLowerCase();

            // SEARCH
            boolean matchSearch = name.contains(search) || pos.contains(search);

            // TYPE FILTER
            boolean matchType = type == null
                    || type.equals("All")
                    || bType.equals(type.toLowerCase());

            // POSITION FILTER
            boolean matchPosition = position == null
                    || position.equals("All")
                    || pos.equals(position.toLowerCase());

            return matchSearch && matchType && matchPosition;
        });

        borrowerTable.setItems(filtered);
        setupActionColumn();
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
        showAlert("Error", "Select a borrower first.");
        return;
    }

    // Custom popup (NO X BUTTON)
    Stage popup = new Stage();
    popup.initStyle(javafx.stage.StageStyle.UNDECORATED);
    popup.initModality(Modality.APPLICATION_MODAL);
    popup.setAlwaysOnTop(true);

    // === ROOT CARD ===
    VBox root = new VBox(15);
    root.setAlignment(Pos.CENTER);
    root.setStyle(
            "-fx-background-color: white; "
            + "-fx-padding: 20; "
            + "-fx-background-radius: 12; "
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0, 0, 3);"
    );

    // === TITLE ===
    Label title = new Label("Delete Borrower");
    title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

    // === MESSAGE ===
    Label msg = new Label(
            "Are you sure you want to delete:\n"
            + selectedBorrower.getBorrowerName()
            + "\n(" + selectedBorrower.getPosition() + ")"
            + "\n\nThis action cannot be undone."
    );
    msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
    msg.setAlignment(Pos.CENTER);
    msg.setWrapText(true);

    // === BUTTONS ===
    Button btnCancel = new Button("Cancel");
    btnCancel.setStyle(
            "-fx-background-color: #bdc3c7; "
            + "-fx-text-fill: white; "
            + "-fx-background-radius: 8; "
            + "-fx-padding: 6 18;"
    );
    btnCancel.setOnAction(e -> popup.close());

    Button btnDelete = new Button("Delete");
    btnDelete.setStyle(
            "-fx-background-color: #e74c3c; "
            + "-fx-text-fill: white; "
            + "-fx-background-radius: 8; "
            + "-fx-padding: 6 18;"
    );
    btnDelete.setOnAction(e -> {
        if (borrowerDAO.deleteBorrower(selectedBorrower.getBorrowerId())) {
            borrowerList.remove(selectedBorrower);
            populatePositionFilter();
            handleCancel();
            showAlert("Success", "Borrower deleted successfully.");
        }
        popup.close();
    });

    HBox buttons = new HBox(10, btnCancel, btnDelete);
    buttons.setAlignment(Pos.CENTER_RIGHT);

    // === ADD TO ROOT ===
    root.getChildren().addAll(title, msg, buttons);

    // === SCENE ===
    Scene scene = new Scene(root, 350, 220);
    popup.setScene(scene);

    // === FADE IN (matches your update popup animation) ===
    FadeTransition ft = new FadeTransition(Duration.millis(180), root);
    root.setOpacity(0);
    ft.setToValue(1);
    ft.play();

    popup.showAndWait();
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
        dialog.initStyle(javafx.stage.StageStyle.UNDECORATED); // ❌ remove X

        dialog.setTitle("Borrow Item");

        // ---- STYLING ----
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #ffffff; "
                + "-fx-padding: 20; "
                + "-fx-border-color: #d0d0d0; "
                + "-fx-border-radius: 10; "
                + "-fx-background-radius: 10;"
        );

        Label title = new Label("Select an Item to Borrow");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField search = new TextField();
        search.setPromptText("Search item...");
        search.setStyle(
                "-fx-background-radius: 6; -fx-padding: 8; "
                + "-fx-border-radius: 6; -fx-border-color: #bdc3c7;"
        );

        ListView<Item> listView = new ListView<>();
        ObservableList<Item> items = FXCollections.observableArrayList(itemDAO.getAllItems());
        listView.setItems(items);

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                // Color-coded status
                String color = switch (item.getStatus()) {
                    case "Available" ->
                        "#27ae60";    // green
                    case "Borrowed" ->
                        "#e67e22";     // orange
                    case "Missing" ->
                        "#c0392b";      // red
                    default ->
                        "#7f8c8d";             // gray
                };

                setText(item.getItemName() + "  •  " + item.getStatus());
                setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            }
        });

        // Search filter
        search.textProperty().addListener((obs, old, val)
                -> listView.setItems(items.filtered(i
                        -> i.getItemName().toLowerCase().contains(val.toLowerCase())))
        );

        Label warningLabel = new Label("");
        warningLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        VBox content = new VBox(12, title, search, listView, warningLabel);
        content.setStyle("-fx-padding: 10;");
        dialog.getDialogPane().setContent(content);

        // ---- BUTTONS ----
        ButtonType saveBtn = new ButtonType("Borrow", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Button saveButtonNode = (Button) dialog.getDialogPane().lookupButton(saveBtn);

        // Default disabled
        saveButtonNode.setDisable(true);
        saveButtonNode.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 5;");

        // Enable/Disable based on item status
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel == null) {
                saveButtonNode.setDisable(true);
                warningLabel.setText("");
                return;
            }

            String status = newSel.getStatus();

            if (status.equalsIgnoreCase("Borrowed")
                    || status.equalsIgnoreCase("Missing")) {

                saveButtonNode.setDisable(true);
                saveButtonNode.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 5;");
                warningLabel.setText("⚠ This item is not available for borrowing.");
            } else {
                saveButtonNode.setDisable(false);
                saveButtonNode.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
                warningLabel.setText("");
            }
        });

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {
                Item item = listView.getSelectionModel().getSelectedItem();
                if (item != null) {

                    itemDAO.updateItemStatus(item.getItemId(), "Borrowed");

                    BorrowRecord record = new BorrowRecord(
                            0,
                            item.getItemId(),
                            borrower.getBorrowerId(),
                            LocalDateTime.now(),
                            null,
                            "Borrowed",
                            ""
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
        dialog.initStyle(javafx.stage.StageStyle.UNDECORATED);

        dialog.setTitle("Return Item");

        // Compact Panel Styling
        dialog.getDialogPane().setPrefWidth(380);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #ffffff; "
                + "-fx-padding: 15; "
                + "-fx-border-color: #d0d0d0; "
                + "-fx-border-radius: 10; "
                + "-fx-background-radius: 10;"
        );

        Label title = new Label("Return Borrowed Item");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Get active borrowed items
        List<BorrowRecord> active = borrowRecordDAO
                .getBorrowRecordsByBorrower(borrower.getBorrowerId())
                .stream()
                .filter(r -> r.getStatus().equals("Borrowed"))
                .toList();

        // Compact ListView
        ListView<BorrowRecord> listView = new ListView<>(FXCollections.observableArrayList(active));
        listView.setPrefHeight(150); // smaller height

        // Styled compact list cells
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BorrowRecord r, boolean empty) {
                super.updateItem(r, empty);

                if (empty || r == null) {
                    setText(null);
                    return;
                }

                Item item = itemDAO.getItemById(r.getItemId());
                String itemName = item != null ? item.getItemName() : "Unknown Item";

                setText(itemName + "\nBorrowed: " + r.getBorrowDate().toString().replace("T", " "));
                setStyle("-fx-font-size: 13px; -fx-padding: 4; -fx-text-fill: #2c3e50;");
            }
        });

        Label remarksLabel = new Label("Remarks:");
        remarksLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

        TextArea remarks = new TextArea();
        remarks.setPromptText("Remarks...");
        remarks.setPrefHeight(60); // smaller
        remarks.setStyle(
                "-fx-background-radius: 6; -fx-padding: 6; "
                + "-fx-border-radius: 6; -fx-border-color: #bdc3c7; -fx-font-size: 12px;"
        );

        Label warningLabel = new Label("");
        warningLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px;");

        if (active.isEmpty()) {
            warningLabel.setText("⚠ No borrowed items to return.");
        }

        VBox content = new VBox(10, title, listView, remarksLabel, remarks, warningLabel);
        content.setStyle("-fx-padding: 5;");
        dialog.getDialogPane().setContent(content);

        ButtonType saveBtn = new ButtonType("Return", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        Button saveButtonNode = (Button) dialog.getDialogPane().lookupButton(saveBtn);

        // Disable until selection
        saveButtonNode.setDisable(active.isEmpty());
        saveButtonNode.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            saveButtonNode.setDisable(newSel == null);
        });

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {

                BorrowRecord rec = listView.getSelectionModel().getSelectedItem();
                if (rec != null) {

                    borrowRecordDAO.returnBorrowRecord(rec.getRecordId(), LocalDateTime.now(), remarks.getText());

                    String newStatus = remarks.getText().toLowerCase().contains("damaged")
                            ? "Damaged"
                            : "Available";

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
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED); // ❌ removes X button
            stage.setTitle(isEdit ? "Edit Borrower" : "Add Borrower");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
