package Antarmuka_Aset_Visual;

import Login_Dan_Akses.FungsiLogout;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node; 
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.FileInputStream;

public abstract class DashboardBase extends Application implements AksiDashboard {

    protected HBox headerMenuContainer; 
    protected VBox contentArea;         

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F4F7F6;");

        HBox header = new HBox();
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setSpacing(15); 
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #1F4A75;"); 

        Label title = new Label(getTitle());
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20px;");
        HBox.setMargin(title, new Insets(0, 15, 0, 0));

        headerMenuContainer = new HBox();
        headerMenuContainer.setSpacing(8); 
        headerMenuContainer.setAlignment(Pos.CENTER_LEFT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);


        
        Button btnLogout = new Button("🚪 Logout");
        btnLogout.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 5;");
        btnLogout.setOnMouseEntered(e -> btnLogout.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 5;"));
        btnLogout.setOnMouseExited(e -> btnLogout.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 5;"));
        
        
        btnLogout.setOnAction(e -> {
            FungsiLogout.prosesLogout(btnLogout.getScene().getWindow());
        });
        
        HBox.setMargin(btnLogout, new Insets(0, 15, 0, 0)); 


        Node logoNode;
        try {
            FileInputStream input = new FileInputStream("C:/SEMESTER 2/PBO/TUGAS/FOOLDER BAHAN/LogoNew.jpg");
            ImageView logoView = new ImageView(new Image(input));
            logoView.setFitWidth(130);
            logoView.setPreserveRatio(true);
            logoNode = logoView;
        } catch (Exception e) {
            Label fallback = new Label("GROSIRKU");
            fallback.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20px;");
            logoNode = fallback;
        }
        

        header.getChildren().addAll(title, headerMenuContainer, spacer, btnLogout, logoNode);
        root.setTop(header);

        contentArea = new VBox();
        contentArea.setPadding(new Insets(25));
        root.setCenter(contentArea);

        loadMenu();    
        buildContent(); 

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle(getTitle());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); 
        primaryStage.show();
    }


    protected void tambahMenu(String text, Runnable aksiKlik) {
        Button btn = new Button(text);
        btn.setPadding(new Insets(8, 12, 8, 12)); 
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;"));
        
        if (aksiKlik != null) {
            btn.setOnAction(e -> aksiKlik.run());
        }
        
        headerMenuContainer.getChildren().add(btn);
    }

    protected void tambahMenu(String text) {
        tambahMenu(text, null);
    }


    public Pane buatCardKustom(String judul, String nilai, String warnaBawah, String emoji) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(20));
        card.setPrefSize(280, 110);
        card.setAlignment(Pos.CENTER_LEFT);
        
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10,0,0,4); " +
                      "-fx-border-color: transparent transparent " + warnaBawah + " transparent; " +
                      "-fx-border-width: 0 0 5 0; -fx-border-radius: 10;");

        VBox teks = new VBox(5);
        Label lblJdl = new Label(judul);
        lblJdl.setStyle("-fx-text-fill: #777777; -fx-font-size: 14px;");
        Label lblNilai = new Label(nilai);
        lblNilai.setStyle("-fx-font-weight: bold; -fx-font-size: 24px; -fx-text-fill: #333333;");
        teks.getChildren().addAll(lblJdl, lblNilai);

        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);
        Label ikon = new Label(emoji);
        ikon.setStyle("-fx-font-size: 30px;");

        card.getChildren().addAll(teks, s, ikon);
        return card;
    }

    public abstract void loadMenu();
    public abstract void buildContent();
    protected abstract String getTitle();
}