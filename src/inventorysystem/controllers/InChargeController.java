package inventorysystem.controllers;

import inventorysystem.dao.CategoryDAO;
import inventorysystem.dao.InchargeDAO;
import inventorysystem.models.Incharge;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.HBox;

public class InChargeController {

    @FXML
    private TableView<Incharge> inchargeTable;

    @FXML
    private TableColumn<Incharge, Integer> colId;
    @FXML
    private TableColumn<Incharge, String> colName, colPosition, colContact, colAssignedCategory;

    @FXML
    private Button addButton, editButton, deleteButton;

    private final InchargeDAO dao = new InchargeDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private ObservableList<Incharge> inchargeList;

    private Map<Integer, String> categoryMap;  // id -> name

    @FXML
    public void initialize() {

        // Load categories
        categoryMap = new HashMap<>();
        categoryDAO.getAllCategories().forEach(cat
                -> categoryMap.put(cat.getCategoryId(), cat.getCategoryName()));

        // Bind column data
        colId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getInchargeId()).asObject());
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getInchargeName()));
        colPosition.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPosition()));
        colContact.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getContactInfo()));
        colAssignedCategory.setCellValueFactory(cd
                -> new SimpleStringProperty(categoryMap.getOrDefault(cd.getValue().getAssignedCategoryId(), "None")));

        loadIncharges();

        // Enable/disable buttons when selecting rows
        inchargeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            editButton.setDisable(!selected);
            deleteButton.setDisable(!selected);
        });
    }

    private void loadIncharges() {
        inchargeList = FXCollections.observableArrayList(dao.getAllIncharges());
        inchargeTable.setItems(inchargeList);
    }

    @FXML
    private void openAddPopup() {
        openFormPopup(null);
    }

    @FXML
    private void openEditPopup() {
        Incharge selected = inchargeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openFormPopup(selected);
        }
    }

    // ============================================================
    //  CUSTOM STYLED ADD / EDIT POPUP  (NO X BUTTON)
    // ============================================================
    private void openFormPopup(Incharge incharge) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventorysystem/views/incharge_form.fxml"));
            Parent form = loader.load();

            InChargeFormController controller = loader.getController();
            controller.setData(incharge, this::loadIncharges);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setResizable(false);

            Scene scene = new Scene(form);
            stage.setScene(scene);

            // 🔽 Move popup slightly lower
            stage.setX((javafx.stage.Screen.getPrimary().getVisualBounds().getWidth() - 400) / 2);
            stage.setY((javafx.stage.Screen.getPrimary().getVisualBounds().getHeight() - 300) / 2 - 30);

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    //  CUSTOM STYLED DELETE POPUP  (NO X BUTTON + MODERN DESIGN)
    // ============================================================
    @FXML
    private void handleDelete() {
        Incharge selected = inchargeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Stage popup = new Stage(StageStyle.UNDECORATED);
        popup.initModality(Modality.APPLICATION_MODAL);

        // Card container
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: white; "
                + "-fx-padding: 20; "
                + "-fx-background-radius: 12; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0, 0, 3);"
        );

        Label title = new Label("Delete In-Charge");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label message = new Label(
                "Are you sure you want to delete:\n"
                + selected.getInchargeName()
                + "\n\nThis action cannot be undone."
        );
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
        message.setAlignment(Pos.CENTER);
        message.setWrapText(true);

        Button cancel = new Button("Cancel");
        cancel.setStyle(
                "-fx-background-color: #bdc3c7; -fx-text-fill: white; "
                + "-fx-background-radius: 8; -fx-padding: 6 18;"
        );
        cancel.setOnAction(e -> popup.close());

        Button delete = new Button("Delete");
        delete.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white; "
                + "-fx-background-radius: 8; -fx-padding: 6 18;"
        );
        delete.setOnAction(e -> {
            dao.deleteIncharge(selected.getInchargeId());
            loadIncharges();
            popup.close();
        });

        VBox buttons = new VBox(new HBox(10, cancel, delete));
        buttons.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(title, message, buttons);

        Scene scene = new Scene(card, 330, 200);
        popup.setScene(scene);

        // Fade-in animation
        FadeTransition ft = new FadeTransition(Duration.millis(200), card);
        card.setOpacity(0);
        ft.setToValue(1);
        ft.play();

        popup.showAndWait();
    }
}
