package setpassword;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class HalamanPesananAdmin {

    private VBox boxPending = new VBox(15);

    public VBox getTampilan() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F7F6;");

        Label lblJudul = new Label("🛠️ Kelola Pesanan Masuk (Admin)");
        lblJudul.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F4A75;");

        ScrollPane scroll = new ScrollPane(boxPending);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-padding: 10;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Button btnRefresh = new Button("🔄 Refresh Data");
        btnRefresh.setOnAction(e -> refreshData());

        root.getChildren().addAll(lblJudul, btnRefresh, scroll);
        refreshData();
        return root;
    }

    private void refreshData() {
        boxPending.getChildren().clear();
        try {
            // Admin hanya melihat yang berstatus 'pending'
            List<String[]> pesananPending = FungsiDB.getDaftarPesananByStatus("pending");
            
            if (pesananPending.isEmpty()) {
                boxPending.getChildren().add(new Label("Belum ada pesanan baru dari Mitra."));
                return;
            }

            for (String[] p : pesananPending) {
                boxPending.getChildren().add(buatCardAdmin(p[0], p[1], p[3], p[4]));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private HBox buatCardAdmin(String id, String toko, String tanggal, String total) {
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

        Button btnAcc = new Button("✅ ACC & Teruskan ke Kurir");
        btnAcc.setStyle("-fx-background-color: #28A745; -fx-text-fill: white; -fx-font-weight: bold;");
        
        btnAcc.setOnAction(e -> {
            try {
                FungsiDB.accPesananAdmin(id); // Mengubah jadi 'diproses'
                new Alert(Alert.AlertType.INFORMATION, "Pesanan diteruskan ke Kurir!").showAndWait();
                refreshData();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        card.getChildren().addAll(info, spacer, btnAcc);
        return card;
    }
}
