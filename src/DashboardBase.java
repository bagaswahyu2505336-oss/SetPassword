package setpassword;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public abstract class DashboardBase extends Application {

    VBox sidebar;
    private HBox header;
    private StackPane content;
    private boolean sidebarVisible = false;

    @Override
    public void start(Stage primaryStage) {
        // Root pane
        BorderPane root = new BorderPane();

        // ===== HEADER =====
        header = createHeader();
        root.setTop(header);

        // ===== SIDEBAR =====
        sidebar = createSidebar();
        StackPane sidebarWrapper = new StackPane(sidebar);
        sidebarWrapper.setPrefWidth(220);
        root.setLeft(sidebarWrapper);
        sidebar.setTranslateX(-220); // awal tersembunyi

        // ===== CONTENT =====
        content = new StackPane();
        content.setStyle("-fx-background-color: #f5f5f5;");
        root.setCenter(content);

        // Load menu
        loadMenu();

        Scene scene = new Scene(root, 1366, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dashboard JavaFX");
        primaryStage.show();
    }

    // ================= HEADER =================
    private HBox createHeader() {
        HBox headerBox = new HBox();
        headerBox.setPadding(new Insets(15));
        headerBox.setSpacing(10);
        headerBox.setStyle("-fx-background-color: white;");
        headerBox.setPrefHeight(70);

        Button menuButton = new Button("☰");
        menuButton.setFont(Font.font(18));
        menuButton.setOnAction(e -> toggleSidebar());

        Label title = new Label(getTitle());
        title.setFont(Font.font("Arial", 22));

        headerBox.getChildren().addAll(menuButton, title);
        return headerBox;
    }

    // ================= SIDEBAR =================
    private VBox createSidebar() {
        VBox box = new VBox();
        box.setPadding(new Insets(30, 10, 10, 10));
        box.setSpacing(20);
        box.setPrefWidth(220);
        box.setStyle("-fx-background-color: #143c96;");

        Label logo = new Label("SISTEM GROSIR");
        logo.setFont(Font.font("Arial", 18));
        logo.setTextFill(Color.WHITE);

        box.getChildren().add(logo);
        return box;
    }

    // ================= ANIMASI SIDEBAR =================
    private void toggleSidebar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), sidebar);
        if (!sidebarVisible) {
            transition.setToX(0);
            sidebarVisible = true;
        } else {
            transition.setToX(-220);
            sidebarVisible = false;
        }
        transition.play();
    }

    // ================= ABSTRACT MENU =================
    public abstract void loadMenu();

    public StackPane getContent() {
        return content;
    }

    private String getTitle() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}