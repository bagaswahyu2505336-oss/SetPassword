package Role_Kurir;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.embed.swing.SwingFXUtils;
import javafx.application.Platform;
import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import Error_Exception.ErrorHandler;
import Database.PengirimanDAO;
import Error_Exception.GrosirException;
import Model.ModelData;


public class HalamanJadwalKurir {
    
    private int idKurir;

    public HalamanJadwalKurir(int idKurir) {
        this.idKurir = idKurir;
    }

    private int getStatusLevel(String status) {
        if (status == null) return -1;
        switch (status.toLowerCase()) {
            case "pending": return 0;
            case "dalam perjalanan": return 1;
            case "sampai": return 2;
            case "selesai": return 3;
            default: return -1;
        }
    }

    public VBox getTampilan() {
        VBox tableBox = new VBox(10);
        tableBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10,0,0,4);");
        Label lblJudul = new Label("🚚 Daftar Pengiriman Anda");
        lblJudul.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1F4A75;");
        
        TableView<ModelData.JadwalKurir> tabelJadwal = new TableView<>();
        tabelJadwal.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<ModelData.JadwalKurir, String> colId = new TableColumn<>("ID Kirim"); colId.setCellValueFactory(new PropertyValueFactory<>("idPengiriman"));
        TableColumn<ModelData.JadwalKurir, String> colToko = new TableColumn<>("Tujuan (Toko)"); colToko.setCellValueFactory(new PropertyValueFactory<>("namaToko"));
        TableColumn<ModelData.JadwalKurir, String> colNama = new TableColumn<>("Nama Pemesan"); colNama.setCellValueFactory(new PropertyValueFactory<>("namaPemesan"));
        TableColumn<ModelData.JadwalKurir, String> colWA = new TableColumn<>("No. WhatsApp"); colWA.setCellValueFactory(new PropertyValueFactory<>("noHp"));
        TableColumn<ModelData.JadwalKurir, String> colAlamat = new TableColumn<>("Alamat Lengkap"); colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        TableColumn<ModelData.JadwalKurir, String> colStatus = new TableColumn<>("Status"); colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        tabelJadwal.getColumns().addAll(colId, colToko, colNama, colWA, colAlamat, colStatus);

        try {
            tabelJadwal.setItems(FXCollections.observableArrayList(PengirimanDAO.getJadwalKurir(idKurir)));
        } catch (GrosirException e) { ErrorHandler.handleException("Memuat Jadwal", e); }

        HBox formUpdate = new HBox(15);
        formUpdate.setAlignment(Pos.CENTER_LEFT);
        TextField txtIdKirim = new TextField(); txtIdKirim.setEditable(false); 
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("dalam perjalanan", "sampai", "selesai");
        Button btnUpdate = new Button("Update Status");
        btnUpdate.setStyle("-fx-background-color: #1F4A75; -fx-text-fill: white; -fx-font-weight: bold;");

        tabelJadwal.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) { 
                txtIdKirim.setText(newSel.getIdPengiriman()); cbStatus.setValue(newSel.getStatus()); 
                if (newSel.getStatus().equalsIgnoreCase("selesai")) {
                    cbStatus.setDisable(true); btnUpdate.setDisable(true);
                } else {
                    cbStatus.setDisable(false); btnUpdate.setDisable(false);
                }
            }
        });

        btnUpdate.setOnAction(e -> {
            if (txtIdKirim.getText().isEmpty() || cbStatus.getValue() == null) return;

            ModelData.JadwalKurir terpilih = tabelJadwal.getSelectionModel().getSelectedItem();
            if (terpilih != null) {
                int levelSekarang = getStatusLevel(terpilih.getStatus());
                int levelBaru = getStatusLevel(cbStatus.getValue());
                if (levelBaru==levelSekarang)return;
                if (levelBaru != levelSekarang +1){
                    if (levelBaru < levelSekarang) {
                        new Alert(Alert.AlertType.WARNING, "Peringatan!\nAnda tidak bisa mengembalikan status ke tahap sebelumnya.").showAndWait();
                    }
                    else{
                        new Alert(Alert.AlertType.WARNING, "Peringatan!\nUbah status menjadi sampai l./luntuk menyelesaikan pengiriman.").showAndWait();
                    }
                    return;
                }
            }

            if (cbStatus.getValue().equalsIgnoreCase("selesai")) {
                // BUKA KAMERA ASLI
                tampilkanKameraAsli(txtIdKirim.getText(), cbStatus.getValue(), tabelJadwal);
            } else {
                eksekusiUpdate(txtIdKirim.getText(), cbStatus.getValue(), null, tabelJadwal);
            }
        });

        formUpdate.getChildren().addAll(new Label("ID:"), txtIdKirim, cbStatus, btnUpdate);
        VBox.setVgrow(tabelJadwal, Priority.ALWAYS);
        tableBox.getChildren().addAll(lblJudul, tabelJadwal, formUpdate);
        return tableBox;
    }

    private void tampilkanKameraAsli(String idPengiriman, String status, TableView<ModelData.JadwalKurir> tabelJadwal) {
        Webcam webcam = Webcam.getDefault();
        if (webcam == null) {
            new Alert(Alert.AlertType.ERROR, "Kamera tidak terdeteksi!").showAndWait();
            return;
        }

        try {
            if (!webcam.isOpen()) {
                webcam.setViewSize(webcam.getViewSizes()[0]);
                webcam.open();
            }
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Gagal menyalakan kamera: " + ex.getMessage()).showAndWait();
            return;
        }

        javafx.stage.Stage stageKamera = new javafx.stage.Stage();
        stageKamera.setTitle("Kamera - Ambil Bukti");

        VBox boxKamera = new VBox(15);
        boxKamera.setAlignment(Pos.CENTER);
        boxKamera.setPadding(new javafx.geometry.Insets(20));
        boxKamera.setStyle("-fx-background-color: #2C3E50;");

        Label lblInfo = new Label("📸 Arahkan wajah ke kamera.\n[Kamera Aktif: " + webcam.getName() + "]");
        lblInfo.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-alignment: center;");
        lblInfo.setAlignment(Pos.CENTER);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(400);
        imgView.setFitHeight(300);
        imgView.setPreserveRatio(true);

        StackPane frame = new StackPane(imgView);
        frame.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-width: 2;");
        frame.setPrefSize(400, 300);

        Button btnJepret = new Button("📸 Jepret & Selesaikan");
        btnJepret.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");

        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                if (webcam.isOpen()) {
                    java.awt.image.BufferedImage bimg = webcam.getImage();
                    if (bimg != null) {
                        java.awt.image.BufferedImage gambarBersih = new java.awt.image.BufferedImage(
                            bimg.getWidth(), bimg.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
                        
                        java.awt.Graphics2D g = gambarBersih.createGraphics();
                        g.drawImage(bimg, 0, 0, null);
                        g.dispose();

                        Image image = SwingFXUtils.toFXImage(gambarBersih, null);
                        imgView.setImage(image);
                    }
                }
            }
        };
        timer.start();

        btnJepret.setOnAction(e -> {
            timer.stop(); 
            btnJepret.setText("⏳ Menyimpan...");
            
            try {
                java.awt.image.BufferedImage fotoHasil = webcam.getImage();
                if (fotoHasil != null) {
                    String namaFile = "Bukti_Kirim_" + idPengiriman + "_" + System.currentTimeMillis() + ".jpg";
                    java.io.File fileTujuan = new java.io.File(namaFile);
                    javax.imageio.ImageIO.write(fotoHasil, "JPG", fileTujuan);
                    
                    if (webcam.isOpen()) {
                        webcam.close();
                    }
                    stageKamera.close();
                    
                    eksekusiUpdate(idPengiriman, status, namaFile, tabelJadwal);
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Gagal menyimpan foto: " + ex.getMessage()).showAndWait();
                btnJepret.setText("📸 Jepret & Selesaikan");
            }
        });

        stageKamera.setOnCloseRequest(e -> {
            timer.stop();
            if (webcam.isOpen()) {
                webcam.close();
            }
        });

        boxKamera.getChildren().addAll(lblInfo, frame, btnJepret);
        javafx.scene.Scene scene = new javafx.scene.Scene(boxKamera, 450, 500);
        stageKamera.setScene(scene);
        stageKamera.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stageKamera.show();
    }
    
    private void eksekusiUpdate(String idPengiriman, String status, String pathFoto, TableView<ModelData.JadwalKurir> tabelJadwal) {
        try {
            if (PengirimanDAO.updateStatusPengiriman(idPengiriman, status, pathFoto)) {
                ErrorHandler.tampilkanSukses("Berhasil", "Status berhasil diperbarui!");
                tabelJadwal.setItems(FXCollections.observableArrayList(PengirimanDAO.getJadwalKurir(idKurir)));
            }
        } catch (GrosirException ex) { ErrorHandler.handleException("Update Status", ex); }
    }
}