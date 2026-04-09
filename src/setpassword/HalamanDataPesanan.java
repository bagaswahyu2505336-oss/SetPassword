package setpassword;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

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
            // Memanggil fungsi baru yang mengambil SEMUA pesanan
            List<String[]> semuaPesanan = FungsiDB.getAllPesananAdmin();
            
            if (semuaPesanan.isEmpty()) {
                boxPesanan.getChildren().add(new Label("Belum ada data pesanan dari Mitra."));
                return;
            }

            for (String[] p : semuaPesanan) {
                // p[0]=id, p[1]=toko, p[2]=alamat, p[3]=tanggal, p[4]=total, p[5]=status
                boxPesanan.getChildren().add(buatCardAdmin(p[0], p[1], p[3], p[4], p[5]));
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

        // --- SECTION STATUS & TOMBOL ---
        VBox aksi = new VBox(8);
        aksi.setAlignment(Pos.CENTER_RIGHT);

        Label lblStatus = new Label(status.toUpperCase());
        lblStatus.setStyle("-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4; -fx-font-size: 12px;");
        
        Button btnAcc = new Button("✅ ACC & Teruskan ke Kurir");
        btnAcc.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnAcc.setVisible(false); // Disembunyikan secara default
        btnAcc.setManaged(false);

        // Logika pewarnaan UI secara Real-Time berdasarkan status database
        if (status.equalsIgnoreCase("pending")) {
            lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #FFF3CD; -fx-text-fill: #856404;");
            lblStatus.setText("MENUNGGU ACC");
            btnAcc.setVisible(true);   // Munculkan tombol ACC hanya jika pending
            btnAcc.setManaged(true);
        } else if (status.equalsIgnoreCase("diproses")) {
            lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #D1ECF1; -fx-text-fill: #0C5460;");
            lblStatus.setText("SEDANG DIPAKING");
        } else if (status.equalsIgnoreCase("dikirim") || status.equalsIgnoreCase("sampai_tujuan")) {
            lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #CCE5FF; -fx-text-fill: #004085;");
            lblStatus.setText("DI TANGAN KURIR");
        } else if (status.equalsIgnoreCase("selesai")) {
            lblStatus.setStyle(lblStatus.getStyle() + "-fx-background-color: #D4EDDA; -fx-text-fill: #155724;");
            lblStatus.setText("TRANSAKSI SELESAI");
        }

        btnAcc.setOnAction(e -> {
            try {
                FungsiDB.accPesananAdmin(id); 
                new Alert(Alert.AlertType.INFORMATION, "Sukses! Pesanan disetujui & diteruskan ke Kurir.").showAndWait();
                refreshData(); // Me-refresh list seketika setelah ACC (Real-Time Update)
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        aksi.getChildren().addAll(lblStatus, btnAcc);
        card.getChildren().addAll(info, spacer, aksi);
        return card;
    }
}