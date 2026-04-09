package setpassword;

import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class HalamanUtamaAdmin {
    
    private DashboardBase baseApp;

    public HalamanUtamaAdmin(DashboardBase baseApp) {
        this.baseApp = baseApp;
    }

    public VBox getTampilan() {
        VBox root = new VBox(25);

        // --- 1. BARIS CARD STATISTIK (DATA REAL DARI DATABASE) ---
        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER);
        
        cards.getChildren().addAll(
            // Data diambil dari FungsiDB
            baseApp.buatCardKustom("Total Stok Fisik", FungsiDB.hitungTotalStokFisik(), "#E74C3C", "📊"), 
            
            // Data diambil dari PesananDAO (Tabel toko_mitra)
            baseApp.buatCardKustom("Mitra Terdaftar", PesananDAO.hitungTotalMitra(), "#6AA051", "🤝"),  
            
            // Data diambil dari PesananDAO (Tabel pesanan)
            baseApp.buatCardKustom("Pesanan Terkini", PesananDAO.hitungTotalPesanan(), "#F39C12", "🛒")
        );

        // --- 2. BARIS GRAFIK ---
        VBox chartBox = new VBox(10);
        chartBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10,0,0,4);");
        
        Label lbl = new Label("📈 Laporan Volume Pesanan");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1F4A75;");
        
        BarChart<String, Number> bc = new BarChart<>(new CategoryAxis(), new NumberAxis());
        bc.setLegendVisible(false); 
        bc.setPrefHeight(300);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>(); 
        series.getData().add(new XYChart.Data<>("Total Pesanan", Integer.parseInt(PesananDAO.hitungTotalPesanan().replace(" Pesanan", ""))));
        
        bc.getData().add(series);
        
        chartBox.getChildren().addAll(lbl, bc);

        // --- 3. TAMBAHKAN TABEL PESANAN TERBARU DI BAWAH GRAFIK (OPSIONAL) ---
        // Jika Anda ingin tabel muncul di Dashboard utama juga:
        HalamanDataPesanan tabelSingkat = new HalamanDataPesanan();
        VBox tabelBox = tabelSingkat.getTampilan();
        tabelBox.setPrefHeight(300); // Batasi tingginya agar tidak terlalu panjang

        root.getChildren().addAll(cards, chartBox, tabelBox);
        return root;
    }
}