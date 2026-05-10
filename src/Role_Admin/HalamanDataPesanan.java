package Role_Admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional; 
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import Database.PesananDAO;
import Database.PengirimanDAO;
import Model.Pesanan;

public class HalamanDataPesanan {

    private VBox boxPesanan = new VBox(15);

    public VBox getTampilan() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F7F6;");

        Label lblJudul = new Label("🛠️ Kelola & Riwayat Pesanan (Admin)");
        lblJudul.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F4A75;");

        ScrollPane scroll = new ScrollPane(boxPesanan);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Button btnRefresh = new Button("🔄 Refresh Data Real-Time");
        btnRefresh.setStyle("-fx-background-color: #1F4A75; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnRefresh.setOnAction(e -> refreshData());

        root.getChildren().addAll(lblJudul, btnRefresh, scroll);
        refreshData();
        return root;
    }
    
    private void refreshData() {
    boxPesanan.getChildren().clear();
    try {
        List<Pesanan> semuaPesanan = PesananDAO.getAllPesananAdmin();
        
        if (semuaPesanan.isEmpty()) {
            boxPesanan.getChildren().add(new Label("Belum ada data pesanan dari Mitra."));
            return;
        }
        for (Pesanan p : semuaPesanan) {
            boxPesanan.getChildren().add(buatCardAdmin(
                String.valueOf(p.getId()), 
                p.getNamaToko(), 
                p.getTanggal(), 
                String.valueOf((long) p.getTotal()), 
                p.getStatus()
            ));
        }
    } catch (Exception e) { 
        e.printStackTrace(); 
        boxPesanan.getChildren().add(new Label("Terjadi kesalahan saat memuat data."));
    }
}

    private HBox buatCardAdmin(String id, String toko, String tanggal, String total, String status) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        VBox info = new VBox(5);
        Label lblToko = new Label("🏪 " + toko + " (Pesanan #" + id + ")");
        lblToko.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label lblDetail = new Label("📅 " + tanggal + " | 💰 Rp " + total);
        info.getChildren().addAll(lblToko, lblDetail);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox aksi = new VBox(8);
        aksi.setAlignment(Pos.CENTER_RIGHT);

        Label lblStatus = new Label(status.toUpperCase());
        lblStatus.setStyle("-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4; -fx-font-size: 12px;");
        
        Button btnAcc = new Button("✅ ACC & Teruskan ke Kurir");
        btnAcc.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnAcc.setVisible(false); 
        btnAcc.setManaged(false);

        if (status.equalsIgnoreCase("pending")) {
            lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #FFF3CD; -fx-text-fill: #856404;");
            lblStatus.setText("MENUNGGU ACC");
            btnAcc.setVisible(true);   
            btnAcc.setManaged(true);
        } else if (status.equalsIgnoreCase("diproses")) {
            lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #E2E3E5; -fx-text-fill: #383D41;");
            lblStatus.setText("SEDANG DIPAKING");
        } else if (status.equalsIgnoreCase("dikirim")) {
            lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #CCE5FF; -fx-text-fill: #004085;");
            lblStatus.setText("DI TANGAN KURIR (OTW)");
        } else if (status.equalsIgnoreCase("sampai_tujuan")) {
            lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #D1ECF1; -fx-text-fill: #0C5460;");
            lblStatus.setText("KURIR TIBA DI LOKASI");
        } else if (status.equalsIgnoreCase("selesai")) {
            lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #D4EDDA; -fx-text-fill: #155724;");
            lblStatus.setText("TRANSAKSI SELESAI");
            
            // ==========================================================
            // LOGIKA BARU: Tombol Cek Bukti yang memanggil jendela Gambar
            // ==========================================================
            Button btnCekBukti = new Button("📸 Cek Bukti Selfie");
            btnCekBukti.setStyle("-fx-background-color: #17A2B8; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            
            btnCekBukti.setOnAction(e -> {
                String namaFoto = PengirimanDAO.getBuktiFoto(id);
                if (namaFoto != null && !namaFoto.isEmpty()) {
                    // PANGGIL FUNGSI UNTUK MEMBUKA JENDELA FOTO ASLI
                    tampilkanJendelaFoto(id, namaFoto);
                } else {
                    new Alert(Alert.AlertType.WARNING, "Kurir belum mengirimkan foto bukti.").showAndWait();
                }
            });
            
            aksi.getChildren().addAll(lblStatus, btnCekBukti);
            card.getChildren().addAll(info, spacer, aksi);
            return card;
        }

        btnAcc.setOnAction(e -> {
            try {
                List<String[]> listKurir = PengirimanDAO.getDaftarKurir();
                
                if (listKurir.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, "Tidak ada data kurir! Silakan daftarkan kurir terlebih dahulu di Kelola User.").showAndWait();
                    return; 
                }

                List<String> pilihanKurir = new ArrayList<>();
                for (String[] k : listKurir) {
                    pilihanKurir.add(k[0] + " - " + k[1]); 
                }

                ChoiceDialog<String> dialog = new ChoiceDialog<>(pilihanKurir.get(0), pilihanKurir);
                dialog.setTitle("Pilih Kurir Pengirim");
                dialog.setHeaderText("Teruskan Pesanan #" + id + " ke Kurir");
                dialog.setContentText("Pilih Kurir yang akan mengantar:");

                Optional<String> result = dialog.showAndWait();
                
                if (result.isPresent()) {
                    String selectedKurir = result.get();
                    String idKurirTerpilih = selectedKurir.split(" - ")[0];
                    String namaKurirTerpilih = selectedKurir.split(" - ")[1];

                    PesananDAO.accPesananAdmin(id, idKurirTerpilih); 
                    
                    new Alert(Alert.AlertType.INFORMATION, "Sukses!\nPesanan disetujui & diteruskan ke Kurir: " + namaKurirTerpilih).showAndWait();
                    refreshData(); 
                }
            } catch (Exception ex) { 
                ex.printStackTrace(); 
                new Alert(Alert.AlertType.ERROR, "Terjadi kesalahan: " + ex.getMessage()).showAndWait();
            }
        });

        aksi.getChildren().addAll(lblStatus, btnAcc);
        card.getChildren().addAll(info, spacer, aksi);
        return card;
    }

    // ==========================================================
    // METHOD BARU: Menampilkan Foto Asli dari Harddisk
    // ==========================================================
    private void tampilkanJendelaFoto(String idPesanan, String namaFoto) {
        Stage stageFoto = new Stage();
        stageFoto.setTitle("Bukti Pengiriman - Pesanan #" + idPesanan);

        VBox boxFoto = new VBox(15);
        boxFoto.setAlignment(Pos.CENTER);
        boxFoto.setPadding(new Insets(20));
        boxFoto.setStyle("-fx-background-color: white;");

        Label lblInfo = new Label("Bukti Foto Selfie Kurir\nFile: " + namaFoto);
        lblInfo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1F4A75;");
        lblInfo.setAlignment(Pos.CENTER);
        lblInfo.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(400);
        imgView.setFitHeight(300);
        imgView.setPreserveRatio(true);

        try {
            // MENCARI FILE FOTO ASLI DI DALAM FOLDER PROJECT
            File fileFoto = new File(namaFoto);
            if (fileFoto.exists()) {
                Image image = new Image(fileFoto.toURI().toString());
                imgView.setImage(image);
            } else {
                lblInfo.setText("File Foto tidak ditemukan di Harddisk!\n(Nama File: " + namaFoto + ")");
                lblInfo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar: " + e.getMessage());
        }

        StackPane frame = new StackPane(imgView);
        frame.setStyle("-fx-border-color: #BDC3C7; -fx-border-width: 3; -fx-padding: 5;");

        Button btnTutup = new Button("Tutup Jendela");
        btnTutup.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnTutup.setOnAction(e -> stageFoto.close());

        boxFoto.getChildren().addAll(lblInfo, frame, btnTutup);

        Scene scene = new Scene(boxFoto, 450, 480);
        stageFoto.setScene(scene);
        stageFoto.show();
    }
}