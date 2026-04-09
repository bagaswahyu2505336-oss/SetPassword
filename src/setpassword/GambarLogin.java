package setpassword;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class GambarLogin extends Pane {

    public GambarLogin() {
        setPrefSize(700, 500);

        // ================= LOGO =================
        Image logoImg = new Image("file:C:/SEMESTER 2/PBO/TUGAS/FOOLDER BAHAN/Logo1White.png");
        ImageView logoView = new ImageView(logoImg);
        logoView.setFitWidth(600);
        logoView.setFitHeight(300);
        logoView.setLayoutX(50); // posisi horizontal
        logoView.setLayoutY(20); // posisi vertikal
        getChildren().add(logoView);

        // ================= ICON USERNAME =================
        Image userImg = new Image("file:C:/SEMESTER 2/PBO/TUGAS/FOOLDER BAHAN/Username.jpg");
        ImageView userView = new ImageView(userImg);
        userView.setFitWidth(25);
        userView.setFitHeight(25);
        userView.setLayoutX(220);
        userView.setLayoutY(270);
        getChildren().add(userView);

        // ================= ICON PASSWORD =================
        Image passImg = new Image("file:C:/SEMESTER 2/PBO/TUGAS/FOOLDER BAHAN/Password.jpg");
        ImageView passView = new ImageView(passImg);
        passView.setFitWidth(25);
        passView.setFitHeight(25);
        passView.setLayoutX(220);
        passView.setLayoutY(310);
        getChildren().add(passView);
    }
}