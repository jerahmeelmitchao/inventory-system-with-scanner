package inventorysystem.controllers;

import inventorysystem.dao.AuditLogDAO;
import inventorysystem.dao.ItemDAO;
import inventorysystem.dao.BorrowerDAO;
import inventorysystem.dao.BorrowRecordDAO;
import inventorysystem.models.BorrowRecord;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

public class DashboardMainController {

    // KPI labels
    @FXML
    private Label lblTotalItems;
    @FXML
    private Label lblAvailableItems;
    @FXML
    private Label lblBorrowedItems;
    @FXML
    private Label lblDamagedItems;

    @FXML
    private Label lblTotalBorrowers;
    @FXML
    private Label lblTotalRecords;
    @FXML
    private Label lblActiveBorrows;
    @FXML
    private Label lblTodaySummary;

    // Card rows for animation
    @FXML
    private HBox row1;
    @FXML
    private HBox row2;

    // Quick actions
    @FXML
    private Button btnAddItem;
    @FXML
    private Button btnAddBorrower;
    @FXML
    private Button btnAddCategory;

    // Charts
    @FXML
    private PieChart statusPieChart;
    @FXML
    private BarChart<String, Number> todayBarChart;
    @FXML
    private CategoryAxis todayCategoryAxis;
    @FXML
    private NumberAxis todayNumberAxis;

    private final ItemDAO itemDAO = new ItemDAO();
    private final BorrowerDAO borrowerDAO = new BorrowerDAO();
    private final BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO();

    private void logAction(String action, String details) {
        AuditLogDAO.log(ItemController.getLoggedUsername(), action, details);
    }

    @FXML
    private StackPane notifBell;
    @FXML
    private Label notifCount;

    @FXML
    private void initialize() {
        loadStatsAndCharts();
        animateCards();
        setupQuickActions();

        Platform.runLater(() -> {
            checkOverdueBorrowedItems();
            notifBell.setOnMouseClicked(e -> showOverduePopup());
            checkOverdueBorrowedItems();
        });

    }

    private void checkOverdueBorrowedItems() {
        BorrowRecordDAO dao = new BorrowRecordDAO();

        dao.getAndMarkOverdue(5);

        int overdueCount = dao.getOverdueCount(5);
        if (overdueCount > 0) {
            notifCount.setText(String.valueOf(overdueCount));
            notifCount.setVisible(true);
        } else {
            notifCount.setVisible(false);
        }
    }

    private void showOverduePopup() {

        BorrowRecordDAO dao = new BorrowRecordDAO();
        var overdueList = dao.getOverdueRecords(5);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Overdue Items");

        // Compact nice panel
        dialog.getDialogPane().setPrefWidth(380);
        dialog.getDialogPane().setStyle(
                "-fx-background-color: #ffffff;"
                + "-fx-padding: 15;"
                + "-fx-border-color: #d0d0d0;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;"
        );

        Label title = new Label("Items Overdue for More Than 5 Days");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        ListView<BorrowRecord> listView = new ListView<>();
        listView.setPrefHeight(200);

        // Load list
        listView.setItems(FXCollections.observableArrayList(overdueList));

        ItemDAO itemDAO = new ItemDAO();

        // Styled cell (same style as return panel)
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BorrowRecord r, boolean empty) {
                super.updateItem(r, empty);

                if (empty || r == null) {
                    setText(null);
                    return;
                }

                var item = itemDAO.getItemById(r.getItemId());
                String itemName = item != null ? item.getItemName() : "Unknown Item";
                String borrowDate = r.getBorrowDate().toString().replace("T", " ");

                setText(
                        itemName
                        + "\nBorrower: " + r.getBorrowerName()
                        + "\nBorrowed: " + borrowDate
                        + "\nDays Overdue: " + r.getDaysOverdue()
                );

                setStyle("-fx-font-size: 13px; -fx-padding: 6; -fx-text-fill: #2c3e50;");
            }
        });

        Label noItemsLabel = new Label("No overdue items.");
        noItemsLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
        noItemsLabel.setVisible(overdueList.isEmpty());

        VBox content = new VBox(10, title, overdueList.isEmpty() ? noItemsLabel : listView);
        content.setStyle("-fx-padding: 5;");
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    private void loadStatsAndCharts() {
        int totalItems = itemDAO.getTotalItems();
        int availableItems = itemDAO.getAvailableItemsCount();
        int borrowedItems = itemDAO.getBorrowedItemsCount();
        int damagedItems = itemDAO.getDamagedItemsCount();

        int totalBorrowers = borrowerDAO.getTotalBorrowers();
        int totalRecords = borrowRecordDAO.getTotalBorrowRecords();
        int activeBorrows = borrowRecordDAO.getActiveBorrowRecordsCount();
        int borrowedToday = borrowRecordDAO.getBorrowedTodayCount();
        int returnedToday = borrowRecordDAO.getReturnedTodayCount();

        // Set numbers (you can add tween animation later if you want)
        lblTotalItems.setText(String.valueOf(totalItems));
        lblAvailableItems.setText(String.valueOf(availableItems));
        lblBorrowedItems.setText(String.valueOf(borrowedItems));
        lblDamagedItems.setText(String.valueOf(damagedItems));

        lblTotalBorrowers.setText(String.valueOf(totalBorrowers));
        lblTotalRecords.setText(String.valueOf(totalRecords));
        lblActiveBorrows.setText(String.valueOf(activeBorrows));

        lblTodaySummary.setText(borrowedToday + " / " + returnedToday);

        // ---- Pie chart: status distribution ----
        statusPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Available", availableItems),
                new PieChart.Data("Borrowed", borrowedItems),
                new PieChart.Data("Damaged", damagedItems)
        ));

        // ---- Bar chart: today's activity ----
        todayCategoryAxis.setCategories(FXCollections.observableArrayList("Borrowed", "Returned"));
        todayBarChart.getData().clear();

        var series = new javafx.scene.chart.XYChart.Series<String, Number>();
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Borrowed", borrowedToday));
        series.getData().add(new javafx.scene.chart.XYChart.Data<>("Returned", returnedToday));

        todayBarChart.getData().add(series);
    }

    private void animateCards() {
        animateRow(row1, 0);
        animateRow(row2, 120);
    }

    private void animateRow(HBox row, int delayMs) {
        if (row == null) {
            return;
        }

        for (int i = 0; i < row.getChildren().size(); i++) {
            if (!(row.getChildren().get(i) instanceof VBox card)) {
                continue;
            }

            card.setOpacity(0);
            card.setTranslateY(20);

            FadeTransition fade = new FadeTransition(Duration.millis(300), card);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.millis(delayMs + i * 90));

            TranslateTransition slide = new TranslateTransition(Duration.millis(300), card);
            slide.setFromY(20);
            slide.setToY(0);
            slide.setDelay(Duration.millis(delayMs + i * 90));

            fade.play();
            slide.play();
        }
    }

    private void setupQuickActions() {
        // Navigate by firing the existing sidebar buttons in DashboardController

        btnAddItem.setOnAction(e -> fireSidebarButton("#itemsBtn"));
        btnAddBorrower.setOnAction(e -> fireSidebarButton("#borrowersBtn"));
        btnAddCategory.setOnAction(e -> fireSidebarButton("#categoriesBtn"));
    }

    /**
     * Helper: find a sidebar button in the main scene by fx:id and fire it.
     * This reuses DashboardController.loadView(...) so navigation stays
     * centralized.
     */
    private void fireSidebarButton(String buttonSelector) {
        if (btnAddItem.getScene() == null) {
            return; // safety check: scene not attached yet
        }

        // Look up the button on the root scene (the BorderPane with sidebar)
        javafx.scene.control.Button target
                = (javafx.scene.control.Button) btnAddItem.getScene().lookup(buttonSelector);

        if (target != null) {
            target.fire();  // triggers the same action as clicking the sidebar
        } else {
            System.err.println("Sidebar button not found for selector: " + buttonSelector);
        }
    }

}
