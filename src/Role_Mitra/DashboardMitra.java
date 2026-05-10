package Role_Mitra;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import Antarmuka_Aset_Visual.DashboardBase;
import Database.MitraDAO;
import Database.PesananDAO;
import Antarmuka_Aset_Visual.SliderIklan;

public class DashboardMitra extends DashboardBase {
    private int myIdUser; 
    private int myIdToko;

    public DashboardMitra(int idUser, int idToko) {
        this.myIdUser = idUser;
        this.myIdToko = idToko;
    }

    @Override
    protected String getTitle() { return "Dashboard Mitra"; }

    @Override
    public void loadMenu() {
        tambahMenu("🏠 Beranda", this::tampilkanBeranda);
        tambahMenu("🛒 Order Barang", this::tampilkanKatalog);
        tambahMenu("📦 Riwayat Pesanan", this::tampilkanRiwayat);
    }

    @Override
    public void buildContent() { tampilkanBeranda(); }
    
    private void tampilkanBeranda() {
        contentArea.getChildren().clear();
        contentArea.setSpacing(25);
        contentArea.setPadding(new Insets(20));


        String statusToko = MitraDAO.getStatusToko(myIdToko);
        int pesananSelesai = PesananDAO.getPesananSelesaiCount(myIdToko);
        double tagihan = PesananDAO.getTotalTagihan(myIdToko);
        List<String> listAktivitas = PesananDAO.getAktivitasTerakhir(myIdToko);

        
        System.out.println("=== DIAGNOSTIK REFRESH MITRA ===");
        System.out.println("ID Toko Saya saat ini : " + myIdToko);
        System.out.println("Data Pesanan didapat  : " + pesananSelesai);
        System.out.println("Data Aktivitas didapat: " + listAktivitas.size());
        System.out.println("================================");

        SliderIklan slider = new SliderIklan();
        StackPane papanIklan = slider.getTampilan();


        HBox boxRefresh = new HBox();
        boxRefresh.setAlignment(Pos.CENTER_RIGHT); 
        
        Button btnRefresh = new Button("🔄 Refresh Data Terbaru");
        btnRefresh.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 5;");
        btnRefresh.setOnMouseEntered(e -> btnRefresh.setStyle("-fx-background-color: #E67E22; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 5;"));
        btnRefresh.setOnMouseExited(e -> btnRefresh.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 5;"));
        

        btnRefresh.setOnAction(e -> tampilkanBeranda());
        boxRefresh.getChildren().add(btnRefresh);


        HBox rowCards = new HBox(20);
        rowCards.setAlignment(Pos.CENTER);
        rowCards.getChildren().addAll(
            buatCardKustom("Toko Terhubung", statusToko, "#28B463", "🏪"),
            buatCardKustom("Transaksi", "Sistem Aman", "#1F4A75", "💳"),
            buatCardKustom("Pesanan Selesai", pesananSelesai + " Pesanan", "#F39C12", "📦"),
            buatCardKustom("Tagihan", "Rp " + (int)tagihan, "#E74C3C", "💰")
        );


        HBox bottomArea = new HBox(25);
        VBox.setVgrow(bottomArea, Priority.ALWAYS);

        VBox boxAktivitas = buatSectionBeranda("Aktivitas Terakhir", "#1F4A75");
        HBox.setHgrow(boxAktivitas, Priority.ALWAYS);
        Label lblInfo = new Label("Pantau aktivitas belanja dan status pengiriman Anda.");
        lblInfo.setStyle("-fx-text-fill: #7F8C8D;");
        VBox vbList = new VBox(10);
        vbList.setPadding(new Insets(10, 0, 0, 0));
        
        if (listAktivitas.isEmpty()) {
            Label lKosong = new Label("• Belum ada aktivitas tracking.");
            lKosong.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-text-fill: #95A5A6;");
            vbList.getChildren().add(lKosong);
        } else {
            for (String act : listAktivitas) {
                Label l = new Label("• " + act);
                l.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
                vbList.getChildren().add(l);
            }
        }
        
        boxAktivitas.getChildren().addAll(lblInfo, new Separator(), vbList);

        VBox boxBantuan = buatSectionBeranda("Pusat Bantuan", "#28B463");
        boxBantuan.setPrefWidth(350);
        Label lblBantuan = new Label("Butuh bantuan mengenai produk atau kendala aplikasi?");
        lblBantuan.setWrapText(true);
        Button btnWA = new Button("Hubungi CS (WhatsApp)");
        btnWA.setStyle("-fx-background-color: #25D366; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;");
        btnWA.setMaxWidth(Double.MAX_VALUE);
        
        btnWA.setOnAction(e -> {
            try {
                String noWA = "6285792668573"; 
                String pesan = "Halo%20Admin,%20saya%20butuh%20bantuan%20terkait%20aplikasi.";
                String urlWebWA = "https://web.whatsapp.com/send?phone=" + noWA + "&text=" + pesan;
                
                java.awt.Desktop.getDesktop().browse(new java.net.URI(urlWebWA));
                
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal membuka browser. Pastikan komputer Anda terhubung ke internet dan memiliki browser default.");
                alert.showAndWait();
            }
        });
        
        VBox contactInfo = new VBox(5);
        contactInfo.getChildren().addAll(
            new Label("📞 Hotline: 0857-9266-8573"), 
            new Label("📧 Email: support@grosirku.com")
        );
        contactInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        boxBantuan.getChildren().addAll(lblBantuan, btnWA, new Separator(), contactInfo);
        bottomArea.getChildren().addAll(boxAktivitas, boxBantuan);


        contentArea.getChildren().addAll(papanIklan, boxRefresh, rowCards, bottomArea);
    }


    private VBox buatSectionBeranda(String judul, String warna) {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: white; -fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label lblJudul = new Label(judul);
        lblJudul.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + warna + ";");
        section.getChildren().add(lblJudul);
        return section;
    }

    private void tampilkanKatalog() {
        contentArea.getChildren().clear();
        HalamanOrderMitra halaman = new HalamanOrderMitra(myIdUser, myIdToko);
        HBox tampilanMenu = halaman.getTampilan(); 
        VBox.setVgrow(tampilanMenu, Priority.ALWAYS);
        contentArea.getChildren().add(tampilanMenu);
    }

    private void tampilkanRiwayat() {
        contentArea.getChildren().clear();
        HalamanRiwayatPesanan halRiwayat = new HalamanRiwayatPesanan(myIdToko);
        VBox tampilanRiwayat = halRiwayat.getTampilan();
        VBox.setVgrow(tampilanRiwayat, Priority.ALWAYS);
        contentArea.getChildren().add(tampilanRiwayat);
    }
}