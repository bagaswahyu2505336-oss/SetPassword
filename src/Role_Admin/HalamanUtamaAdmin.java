package Role_Admin;

import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.util.Map;
import Antarmuka_Aset_Visual.DashboardBase;
import Database.MitraDAO;
import Database.PesananDAO;
import Database.BarangDAO;

public class HalamanUtamaAdmin {
    
    private DashboardBase baseApp;

    public HalamanUtamaAdmin(DashboardBase baseApp) {
        this.baseApp = baseApp;
    }

    public VBox getTampilan() {
        VBox root = new VBox(25);
        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER);
        
        cards.getChildren().addAll(
            baseApp.buatCardKustom("Total Stok Fisik", BarangDAO.hitungTotalStokFisik(), "#E74C3C", "📊"), 
            baseApp.buatCardKustom("Mitra Terdaftar", MitraDAO.hitungTotalMitra(), "#6AA051", "🤝"),  
            baseApp.buatCardKustom("Pesanan Terkini", PesananDAO.hitungTotalPesanan(), "#F39C12", "🛒")
        );

        VBox chartBox = new VBox(10);
        chartBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10,0,0,4);");
        
        Label lbl = new Label("📈 Laporan Volume Pesanan (7 Hari Terakhir)");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1F4A75;");
        
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tanggal");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Jumlah Pesanan");

        BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
        bc.setLegendVisible(false); 
        bc.setPrefHeight(350);
        bc.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>(); 
        Map<String, Integer> dataVolume = PesananDAO.getVolumePesananPerHari();
        
        if (dataVolume.isEmpty()) {
            series.getData().add(new XYChart.Data<>("Belum Ada Data", 0));
        } else {
            dataVolume.forEach((tanggal, total) -> {
                series.getData().add(new XYChart.Data<>(tanggal, total));
            });
        }
        
        bc.getData().add(series);
        chartBox.getChildren().addAll(lbl, bc);

        HalamanDataPesanan tabelSingkat = new HalamanDataPesanan();
        VBox tabelBox = tabelSingkat.getTampilan();
        VBox.setVgrow(tabelBox, Priority.ALWAYS); 

        root.getChildren().addAll(cards, chartBox, tabelBox);
        return root;
    }
}