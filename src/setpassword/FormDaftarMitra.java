package setpassword;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FormDaftarMitra {

    public void start(Stage stage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #F4F7F6;");

        Label lblJudul = new Label("📝 Pendaftaran Mitra Jawa Timur");
        lblJudul.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1F4A75;");

        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER_LEFT);
        formContainer.setMaxWidth(400);

        TextField txtNama = new TextField(); txtNama.setPromptText("Nama Lengkap Pemilik");
        TextField txtHp = new TextField(); txtHp.setPromptText("Nomor HP / WhatsApp");
        TextField txtToko = new TextField(); txtToko.setPromptText("Nama Toko");

        ComboBox<String> cbProvinsi = new ComboBox<>(FXCollections.observableArrayList("Jawa Timur"));
        cbProvinsi.setValue("Jawa Timur"); cbProvinsi.setDisable(true); cbProvinsi.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cbKabupaten = new ComboBox<>();
        cbKabupaten.setPromptText("Pilih Kabupaten"); cbKabupaten.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cbKecamatan = new ComboBox<>();
        cbKecamatan.setPromptText("Pilih Kecamatan"); cbKecamatan.setMaxWidth(Double.MAX_VALUE); cbKecamatan.setDisable(true);

        TextField txtDesa = new TextField(); txtDesa.setPromptText("Nama Desa (Ketik Manual)");
        TextArea txtDetail = new TextArea(); txtDetail.setPromptText("Detail Alamat (Dusun, RT/RW)");
        txtDetail.setPrefRowCount(2);

        try {
            cbKabupaten.setItems(FXCollections.observableArrayList(FungsiDB.getDaftarKabupaten()));
        } catch (Exception e) { e.printStackTrace(); }

        cbKabupaten.setOnAction(e -> {
            String kab = cbKabupaten.getValue();
            if (kab != null) {
                try {
                    cbKecamatan.setItems(FXCollections.observableArrayList(FungsiDB.getDaftarKecamatan(kab)));
                    cbKecamatan.setDisable(false);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        TextField txtUser = new TextField(); txtUser.setPromptText("Username Login");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Password");

        formContainer.getChildren().addAll(
            new Label("Identitas Pemilik:"), txtNama, txtHp,
            new Label("Informasi Toko:"), txtToko,
            new Label("Wilayah:"), cbProvinsi, cbKabupaten, cbKecamatan, txtDesa, txtDetail,
            new Label("Akun Login:"), txtUser, txtPass
        );

        Button btnKirim = new Button("Kirim Pengajuan");
        btnKirim.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold;");
        btnKirim.setMaxWidth(Double.MAX_VALUE);

        Button btnBatal = new Button("Kembali");
        btnBatal.setMaxWidth(Double.MAX_VALUE);

        btnKirim.setOnAction(e -> {
            if(txtNama.getText().isEmpty() || cbKabupaten.getValue() == null || txtUser.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Lengkapi data pendaftaran!").showAndWait();
                return;
            }
            try {
                String alamat = txtDetail.getText() + ", Desa " + txtDesa.getText() + ", Kec. " + cbKecamatan.getValue() + ", " + cbKabupaten.getValue();
                FungsiDB.ajukanPendaftaranMitra(txtNama.getText(), txtHp.getText(), "", txtToko.getText(), alamat, txtUser.getText(), txtPass.getText());
                
                new Alert(Alert.AlertType.INFORMATION, "Sukses! Menunggu persetujuan Admin.").showAndWait();
                stage.close(); new LoginForm().start(new Stage());
            } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Gagal: " + ex.getMessage()).showAndWait(); }
        });

        btnBatal.setOnAction(e -> { stage.close(); new LoginForm().start(new Stage()); });

        root.getChildren().addAll(lblJudul, formContainer, btnKirim, btnBatal);
        stage.setScene(new Scene(root, 500, 800));
        stage.show();
    }
}