package inventorysystem.controllers;

import inventorysystem.dao.AuditLogDAO;
import inventorysystem.dao.CategoryDAO;
import inventorysystem.models.Category;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

public class CategoryController {

    @FXML
    private TextField categoryNameField;

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button cancelButton;

    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, Integer> colCategoryId;
    @FXML
    private TableColumn<Category, String> colCategoryName;
    @FXML
    private Button btnPrev, btnNext;
    @FXML
    private Label lblPageInfo;
    @FXML
    private ComboBox<Integer> rowsPerPageCombo;

    private int currentPage = 1;
    private int rowsPerPage = 10;

    private ObservableList<Category> masterList = FXCollections.observableArrayList();
    private ObservableList<Category> pageList = FXCollections.observableArrayList();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private ObservableList<Category> categoryList;

    private void logAction(String action, String details) {
        AuditLogDAO.log(ItemController.getLoggedUsername(), action, details);
    }

    @FXML
    public void initialize() {
        // Bind table columns
        colCategoryId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getCategoryId()).asObject());
        colCategoryName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCategoryName()));

        loadCategories();

        // Initially disable update/delete buttons
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        cancelButton.setVisible(false);

        // Populate fields when selecting a row
        categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateFields(newSel);
                addButton.setDisable(true);      // Disable Add while editing
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                cancelButton.setVisible(true);
            } else {
                clearFields();
            }
        });

        // Optional: prevent numeric input in category name
        categoryNameField.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (e.getCharacter().matches("\\d")) {
                e.consume();
            }
        });
        setupPagination();
    }

    private void loadCategories() {
        categoryList = FXCollections.observableArrayList(categoryDAO.getAllCategories());
        categoryTable.setItems(categoryList);
        masterList.setAll(categoryList);
        updatePage();
    }

    private void populateFields(Category category) {
        categoryNameField.setText(category.getCategoryName());
    }

    @FXML
    private void handleAdd() {
        String name = categoryNameField.getText().trim();

        if (name.isEmpty()) {
            showAlert("Validation Error", "Please fill the category name.");
            return;
        }

        Category category = new Category(0, sanitizeInput(name));
        categoryDAO.addCategory(category);

        showConfirmation("Category added successfully!");
        loadCategories();
        clearFields();
    }

    @FXML
    private void handleUpdate() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a category to update.");
            return;
        }

        if (categoryNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please fill the category name.");
            return;
        }

        selected.setCategoryName(sanitizeInput(categoryNameField.getText().trim()));
        categoryDAO.updateCategory(selected);

        showConfirmation("Category updated successfully!");
        loadCategories();
        clearFields();
        cancelButton.setVisible(false);
    }

    @FXML
    private void handleDelete() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a category to delete.");
            return;
        }

        categoryDAO.deleteCategory(selected.getCategoryId());
        showConfirmation("Category deleted successfully!");
        loadCategories();
        clearFields();
        cancelButton.setVisible(false);
    }

    @FXML
    private void handleCancel() {
        clearFields();
        cancelButton.setVisible(false);
    }

    private void clearFields() {
        categoryNameField.clear();
        categoryTable.getSelectionModel().clearSelection();

        addButton.setDisable(false);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private String sanitizeInput(String input) {
        return input.replaceAll("[<>\"'%;()&+]", "");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupPagination() {
        rowsPerPageCombo.setItems(FXCollections.observableArrayList(5, 10, 20, 30, 50));
        rowsPerPageCombo.setValue(10);

        rowsPerPageCombo.valueProperty().addListener((obs, old, newVal) -> {
            rowsPerPage = newVal;
            currentPage = 1;
            updatePage();
        });

        btnPrev.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                updatePage();
            }
        });

        btnNext.setOnAction(e -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                updatePage();
            }
        });
    }

    private void updatePage() {
        int total = masterList.size();
        int totalPages = getTotalPages();
        if (totalPages == 0) {
            totalPages = 1;
        }

        int from = (currentPage - 1) * rowsPerPage;
        int to = Math.min(from + rowsPerPage, total);

        if (from > to) {
            currentPage = 1;
            from = 0;
            to = Math.min(rowsPerPage, total);
        }

        pageList.setAll(masterList.subList(from, to));
        categoryTable.setItems(pageList);

        lblPageInfo.setText("Page " + currentPage + " of " + totalPages);

        btnPrev.setDisable(currentPage == 1);
        btnNext.setDisable(currentPage == totalPages);
    }

    private int getTotalPages() {
        return (int) Math.ceil((double) masterList.size() / rowsPerPage);
    }

}
