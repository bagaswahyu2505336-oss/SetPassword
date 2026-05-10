package Role_Mitra;

import Login_Dan_Akses.LoginForm;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;
import Database.MitraDAO;
import Database.WilayahDAO;
import Error_Exception.ErrorHandler;

public class FormDaftarMitra {

    
    public void start(Stage stage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20, 40, 20, 40));
        root.setStyle("-fx-background-color: #F4F7F6;");

        Label lblJudul = new Label("📝 Pendaftaran Mitra Jawa Timur");
        lblJudul.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F4A75;");

        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER_LEFT);
        formContainer.setMinWidth(400);
        String styleInput = "-fx-background-radius: 5; -fx-border-color: #DDD; -fx-border-radius: 5;";

        TextField txtNama = new TextField(); 
        txtNama.setPromptText("Nama Lengkap Pemilik");
        txtNama.setStyle(styleInput);
        txtNama.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[a-zA-Z\\s]*")){
                txtNama.setText(newValue.replaceAll("[^a-zA-Z\\s]", ""));
            }
        });
        
        TextField txtHp = new TextField(); 
        txtHp.setPromptText("Nomor HP / WhatsApp");
        txtHp.setStyle(styleInput);
        txtHp.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtHp.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (txtHp.getText().length() > 13) {
                txtHp.setText(txtHp.getText().substring(0, 13));
            }
        });

        TextField txtToko = new TextField(); 
        txtToko.setPromptText("Nama Toko");
        txtToko.setStyle(styleInput);
        txtToko.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[a-zA-Z0-9\\s]*")){
                txtToko.setText(newValue.replaceAll("[^a-zA-Z0-9\\s]", ""));
            }
        });

        ComboBox<String> cbProvinsi = new ComboBox<>();
        cbProvinsi.setPromptText("Pilih Provinsi"); 
        cbProvinsi.setMaxWidth(Double.MAX_VALUE);
        cbProvinsi.setStyle(styleInput);

        ComboBox<String> cbKabupaten = new ComboBox<>();
        cbKabupaten.setPromptText("Pilih Kabupaten"); 
        cbKabupaten.setMaxWidth(Double.MAX_VALUE);
        cbKabupaten.setDisable(true);
        cbKabupaten.setStyle(styleInput);

        ComboBox<String> cbKecamatan = new ComboBox<>();
        cbKecamatan.setPromptText("Pilih Kecamatan"); 
        cbKecamatan.setMaxWidth(Double.MAX_VALUE);
        cbKecamatan.setDisable(true);
        cbKecamatan.setStyle(styleInput);

        try {
            List<String> listProv = WilayahDAO.getDaftarProvinsi();
            cbProvinsi.setItems(FXCollections.observableArrayList(listProv));
        } catch (Exception e) { 
            ErrorHandler.handleException("Gagal Memuat Provinsi", e);
            
            
        }

        cbProvinsi.setOnAction(e -> {
            String prov = cbProvinsi.getValue();
            if (prov != null && !prov.equals("Pilih Provinsi")) {
                try {
                    List<String> listKab = WilayahDAO.getDaftarKabupaten(prov);
                    cbKabupaten.setItems(FXCollections.observableArrayList(listKab));
                    cbKabupaten.setDisable(false);
                    cbKecamatan.getItems().clear();
                    cbKecamatan.setDisable(true);
                } catch (Exception ex) { 
                    ErrorHandler.handleException("Gagal Memuat Kabupaten", ex);
                }
            }
        });

        cbKabupaten.setOnAction(e -> {
            String kab = cbKabupaten.getValue();
            if (kab != null) {
                try {
                    List<String> listKec = WilayahDAO.getDaftarKecamatan(kab);
                    cbKecamatan.setItems(FXCollections.observableArrayList(listKec));
                    cbKecamatan.setDisable(false);
                    cbKecamatan.setPromptText("Pilih Kecamatan");
                } catch (Exception ex) { 
                    ErrorHandler.handleException("Gagal Memuat Kecamatan", ex); 
                }
            }
        });

        TextField txtDesa = new TextField(); 
        txtDesa.setPromptText("Nama Desa (Ketik Manual)");
        txtDesa.setStyle(styleInput);
        txtDesa.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[a-zA-Z\\s.,]*")){
                txtDesa.setText(newValue.replaceAll("[^a-zA-Z\\s.,]", ""));
            }
        });
        
        TextArea txtDetail = new TextArea(); 
        txtDetail.setPromptText("Detail Alamat (Dusun, RT/RW)");
        txtDetail.setPrefRowCount(2);
        txtDetail.setStyle(styleInput);
        txtDetail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z0-9\\s.,/]*")) {
                txtDetail.setText(newValue.replaceAll("[^a-zA-Z0-9.,/]", ""));
            }
        });

        TextField txtUser = new TextField(); 
        txtUser.setPromptText("Username Login");
        txtUser.setStyle(styleInput);
        txtUser.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("[a-zA-Z0-9]*")){
                txtUser.setText(newValue.replaceAll("[^a-zA-Z0-9]", ""));
            }
        });
        
        PasswordField txtPass = new PasswordField(); 
        txtPass.setPromptText("Password");
        txtPass.setStyle(styleInput);

        formContainer.getChildren().addAll(
            new Label("Identitas Pemilik:"), txtNama, txtHp,
            new Label("Informasi Toko:"), txtToko,
            new Label("Wilayah:"), cbProvinsi, cbKabupaten, cbKecamatan, txtDesa, txtDetail,
            new Label("Akun Login:"), txtUser, txtPass
        );

        Button btnKirim = new Button("Kirim Pengajuan");
        btnKirim.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        btnKirim.setMaxWidth(Double.MAX_VALUE);
        btnKirim.setPadding(new Insets(10));

        Button btnBatal = new Button("Kembali");
        btnBatal.setMaxWidth(Double.MAX_VALUE);
        btnBatal.setStyle("-fx-cursor: hand;");

        btnKirim.setOnAction(e -> {
            // Validasi: Cek apakah ada kolom yang kosong (menggunakan .trim() agar spasi saja tidak lolos)
            if(txtNama.getText().trim().isEmpty() || 
               txtHp.getText().trim().isEmpty() || 
               txtToko.getText().trim().isEmpty() || 
               cbProvinsi.getValue() == null || 
               cbKabupaten.getValue() == null || 
               cbKecamatan.getValue() == null || 
               txtDesa.getText().trim().isEmpty() || 
               txtDetail.getText().trim().isEmpty() || 
               txtUser.getText().trim().isEmpty() || 
               txtPass.getText().isEmpty()) {
                
                ErrorHandler.tampilkanPeringatan("Formulir Tidak Lengkap", "Semua data wajib diisi! Mohon lengkapi formulir Anda.");
                return;
            }

            try {
                String alamatLengkap = txtDetail.getText() + ", Desa " + txtDesa.getText() + ", Kec. " + cbKecamatan.getValue() + ", " + cbKabupaten.getValue();
                MitraDAO.ajukanPendaftaranMitra(txtNama.getText(), txtHp.getText(), "", txtToko.getText(), alamatLengkap, txtUser.getText(), txtPass.getText());
                
                ErrorHandler.tampilkanSukses("Pendaftaran Terkirim", "Pengajuan Anda telah dikirim. Menunggu verifikasi Admin.");
                stage.close(); 
                try {
                    new LoginForm().start(new Stage()); 
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception ex) { 
                ErrorHandler.handleException("Gagal Daftar Mitra", ex);
            }
        });

        btnBatal.setOnAction(e -> { 
            stage.close(); 
            try {
                new LoginForm().start(new Stage()); 
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        root.getChildren().addAll(lblJudul, formContainer, btnKirim, btnBatal);
        
        Scene scene = new Scene(root, 480, 750);
        stage.setTitle("Grosirku - Pendaftaran Mitra");
        stage.setScene(scene);
        stage.show();
    }
}