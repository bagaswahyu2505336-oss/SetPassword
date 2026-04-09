package setpassword;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class HalamanTugasKurir {

    private VBox boxTugasBaru = new VBox(15);
    private VBox boxSedangDikirim = new VBox(15);

    public VBox getTampilan() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F7F6;");

        Label lblJudul = new Label("🚚 Dashboard Tugas Kurir");
        lblJudul.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F4A75;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabBaru = new Tab("📦 Siap Dikirim");
        ScrollPane scrollBaru = new ScrollPane(boxTugasBaru);
        scrollBaru.setFitToWidth(true); tabBaru.setContent(scrollBaru);

        Tab tabJalan = new Tab("🛵 Sedang Diantar");
        ScrollPane scrollJalan = new ScrollPane(boxSedangDikirim);
        scrollJalan.setFitToWidth(true); tabJalan.setContent(scrollJalan);

        tabPane.getTabs().addAll(tabBaru, tabJalan);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Button btnRefresh = new Button("🔄 Refresh Tugas");
        btnRefresh.setOnAction(e -> refreshData());

        root.getChildren().addAll(lblJudul, btnRefresh, tabPane);
        refreshData();
        return root;
    }

    private void refreshData() {
        boxTugasBaru.getChildren().clear();
        boxSedangDikirim.getChildren().clear();
        try {
            List<String[]> tugasBaru = FungsiDB.getDaftarPesananByStatus("diproses");
            for (String[] t : tugasBaru) {
                boxTugasBaru.getChildren().add(buatCardKurir(t[0], t[1], t[2], "Mulai Kirim", "dikirim"));
            }

            List<String[]> tugasJalan = FungsiDB.getDaftarPesananByStatus("dikirim");
            for (String[] t : tugasJalan) {
                boxSedangDikirim.getChildren().add(buatCardKurir(t[0], t[1], t[2], "Tandai Sampai", "sampai_tujuan"));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private HBox buatCardKurir(String id, String toko, String alamat, String teksTombol, String statusTujuan) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #ccc;");

        VBox info = new VBox(5);
        Label lblToko = new Label("🏪 " + toko + " (#" + id + ")");
        lblToko.setStyle("-fx-font-weight: bold;");
        Label lblAlamat = new Label("📍 " + alamat);
        lblAlamat.setWrapText(true); lblAlamat.setMaxWidth(350);
        info.getChildren().addAll(lblToko, lblAlamat);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAksi = new Button(teksTombol);
        btnAksi.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");
        
        btnAksi.setOnAction(e -> {
            try {
                if (statusTujuan.equals("dikirim")) {
                    FungsiDB.kurirKirimBarang(id);
                } else {
                    FungsiDB.kurirSampaiTujuan(id);
                }
                refreshData();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        card.getChildren().addAll(info, spacer, btnAksi);
        return card;
    }
}