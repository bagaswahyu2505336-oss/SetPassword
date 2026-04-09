package setpassword;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class HalamanRiwayatPesanan {

    private VBox boxRiwayat = new VBox(15);
    private int idTokoMitra;

    public HalamanRiwayatPesanan(int idToko) {
        this.idTokoMitra = idToko;
    }

    public VBox getTampilan() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F7F6;");

        Label lblJudul = new Label("📦 Tracking Pesanan Mitra");
        lblJudul.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        ScrollPane scroll = new ScrollPane(boxRiwayat);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setOnAction(e -> refreshData());

        root.getChildren().addAll(lblJudul, btnRefresh, scroll);
        refreshData();
        return root;
    }

    private void refreshData() {
        boxRiwayat.getChildren().clear();
        try {
            List<String[]> riwayat = FungsiDB.getRiwayatPesananMitra(idTokoMitra);
            if (riwayat.isEmpty()) {
                boxRiwayat.getChildren().add(new Label("Belum ada riwayat pesanan."));
                return;
            }
            for (String[] rwy : riwayat) {
                boxRiwayat.getChildren().add(buatCardPesanan(rwy[0], rwy[1], rwy[2], rwy[3]));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private HBox buatCardPesanan(String id, String tanggal, String total, String status) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #E0E0E0;");

        VBox info = new VBox(5);
        Label lblID = new Label("Order #" + id);
        lblID.setStyle("-fx-font-weight: bold;");
        Label lblDetail = new Label("📅 " + tanggal + " | 💰 Rp " + total);
        info.getChildren().addAll(lblID, lblDetail);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblStatus = new Label(status.toUpperCase());
        lblStatus.setStyle("-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 4;");
        
        Button btnTerima = new Button("Konfirmasi Terima");
        btnTerima.setStyle("-fx-background-color: #28A745; -fx-text-fill: white;");
        btnTerima.setVisible(false); btnTerima.setManaged(false);

        // Logic Status
        if (status.equals("sampai_tujuan")) {
            lblStatus.setStyle("-fx-background-color: #F8D7DA;");
            btnTerima.setVisible(true); btnTerima.setManaged(true);
        } else if (status.equals("selesai")) {
            lblStatus.setStyle("-fx-background-color: #D4EDDA;");
        } else {
            lblStatus.setStyle("-fx-background-color: #FFF3CD;");
        }

        btnTerima.setOnAction(e -> {
            try {
                FungsiDB.konfirmasiMitraSelesai(id);
                refreshData();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        VBox aksi = new VBox(5, lblStatus, btnTerima);
        aksi.setAlignment(Pos.CENTER_RIGHT);
        card.getChildren().addAll(info, spacer, aksi);
        return card;
    }
}