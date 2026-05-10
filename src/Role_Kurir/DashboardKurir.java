package Role_Kurir;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import Antarmuka_Aset_Visual.DashboardBase;
import Database.PengirimanDAO;

public class DashboardKurir extends DashboardBase {
    private int myIdKurir; 

    public DashboardKurir(int idKurir) {
        this.myIdKurir = idKurir;
    }

    @Override
    protected String getTitle() { return "Dashboard Kurir Pengiriman"; }

    @Override
    public void loadMenu() {
        tambahMenu("🚚 Jadwal Kirim", this::tampilkanJadwal);
    }

    @Override
    public void buildContent() { tampilkanJadwal(); }

    private void tampilkanJadwal() {
        contentArea.getChildren().clear();
        contentArea.setSpacing(20);


        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.CENTER_LEFT);

        
        
        String jumlahSelesai = PengirimanDAO.hitungPengirimanSelesai(myIdKurir);

        HBox cards = new HBox(20);
        cards.getChildren().addAll(
            buatCardKustom("Total Pengiriman", jumlahSelesai, "#F39C12", "🚚")
        );
        
        
        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-cursor: hand;");
        btnRefresh.setOnAction(e -> tampilkanJadwal());

        topRow.getChildren().addAll(cards, btnRefresh);

        
        HalamanJadwalKurir halaman = new HalamanJadwalKurir(myIdKurir);
        VBox tampilanMenu = halaman.getTampilan();
        VBox.setVgrow(tampilanMenu, Priority.ALWAYS);

        contentArea.getChildren().addAll(topRow, tampilanMenu);
    }
}