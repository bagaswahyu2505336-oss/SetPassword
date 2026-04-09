package setpassword;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginForm extends Application {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button daftarButton; // Tombol baru

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane(); 
        root.setStyle("-fx-background-color: white;");

        try {
            GambarLogin gambarLogin = new GambarLogin();
            root.getChildren().add(gambarLogin);
        } catch (Exception e) {
            System.out.println("Gambar background tidak ditemukan, background kosong.");
        }

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setLayoutX(260); 
        usernameField.setLayoutY(270); 
        usernameField.setPrefWidth(200);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setLayoutX(260);
        passwordField.setLayoutY(310);
        passwordField.setPrefWidth(200);

        loginButton = new Button("Login");
        loginButton.setLayoutX(260);
        loginButton.setLayoutY(360);
        loginButton.setPrefWidth(200);
        loginButton.setStyle("-fx-background-color: #1F4A75; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setOnAction(e -> login(primaryStage));

        // ===== TAMBAHAN TOMBOL DAFTAR =====
        Label lblDaftar = new Label("Ingin bekerja sama tapi belum punya akun?");
        lblDaftar.setLayoutX(245);
        lblDaftar.setLayoutY(405);
        lblDaftar.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

        daftarButton = new Button("Daftar Menjadi Mitra");
        daftarButton.setLayoutX(260);
        daftarButton.setLayoutY(425);
        daftarButton.setPrefWidth(200);
        daftarButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #28A745; -fx-border-color: #28A745; -fx-border-radius: 3;");
        
        daftarButton.setOnAction(e -> {
            new FormDaftarMitra().start(new Stage());
            primaryStage.close(); // Tutup form login
        });

        root.getChildren().addAll(usernameField, passwordField, loginButton, lblDaftar, daftarButton);

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Aplikasi Toko");
        primaryStage.show();
    }

    private void login(Stage stage) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Username dan Password tidak boleh kosong!").showAndWait();
            return;
        }

        Admin admin = new Admin(username, password);
        Mitra mitra = new Mitra(username, password);
        Kurir kurir = new Kurir(username, password);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (admin.login()) {
            alert.setContentText("Login berhasil sebagai Admin");
            alert.showAndWait();
            new DashboardAdmin().start(new Stage());
            stage.close();
            
        } else if (mitra.login()) {
            alert.setContentText("Login berhasil sebagai Mitra");
            alert.showAndWait();
            new DashboardMitra(mitra.getIdUser(), mitra.getIdToko()).start(new Stage());
            stage.close();
            
        } else if (kurir.login()) {
            alert.setContentText("Login berhasil sebagai Kurir");
            alert.showAndWait();
            new DashboardKurir(kurir.getIdKurir()).start(new Stage());
            stage.close();
            
        } else {
            // Cek apakah akunnya nyangkut di tabel calon_mitra (menunggu ACC)
            try {
                if(FungsiDB.cekStatusPending(username, password)) {
                    Alert pending = new Alert(Alert.AlertType.WARNING, "Akun Anda masih dalam status PENDING. Silakan tunggu Admin melakukan ACC.");
                    pending.showAndWait();
                    return;
                }
            } catch (Exception ex) { ex.printStackTrace(); }

            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Login gagal! Username atau Password salah.");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}