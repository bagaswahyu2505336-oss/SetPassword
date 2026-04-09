package setpassword;

import javafx.scene.layout.*;

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
        tambahMenu("Logout", () -> System.exit(0));
    }

    @Override
    public void buildContent() { tampilkanJadwal(); }

    private void tampilkanJadwal() {
        contentArea.getChildren().clear();
        contentArea.setSpacing(20);

        HBox cards = new HBox(20);
        cards.getChildren().addAll(buatCardKustom("Tugas Hari Ini", "Update di Bawah", "#F39C12", "📍"));

        // Panggil OOP Class khusus Jadwal Kurir
        HalamanJadwalKurir halaman = new HalamanJadwalKurir(myIdKurir);
        VBox tampilanMenu = halaman.getTampilan();
        VBox.setVgrow(tampilanMenu, Priority.ALWAYS);

        contentArea.getChildren().addAll(cards, tampilanMenu);
    }
}