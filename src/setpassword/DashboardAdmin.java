package setpassword;

import javafx.scene.layout.*;

public class DashboardAdmin extends DashboardBase {

    @Override
    protected String getTitle() { return "Dashboard Administrator"; }

    @Override
    public void loadMenu() {
        tambahMenu("⊞ Dashboard", this::tampilkanDashboard);
        tambahMenu("📦 Kelola Barang", this::tampilkanKelolaBarang);
        tambahMenu("👥 Kelola User", this::tampilkanKelolaUser);
        tambahMenu("🛒 Data Pesanan", this::tampilkanDataPesanan);
        tambahMenu("Logout", () -> System.exit(0));
    }

    @Override
    public void buildContent() { 
        tampilkanDashboard(); // Halaman pertama yang terbuka
    }

    // ==========================================
    // MENU 1: DASHBOARD UTAMA
    // ==========================================
    private void tampilkanDashboard() {
        contentArea.getChildren().clear(); 
        
        // "this" dikirim agar HalamanUtamaAdmin bisa memakai fungsi buatCardKustom
        HalamanUtamaAdmin halaman = new HalamanUtamaAdmin(this); 
        VBox view = halaman.getTampilan();
        
        VBox.setVgrow(view, Priority.ALWAYS);
        contentArea.getChildren().add(view);
    }

    // ==========================================
    // MENU 2: KELOLA BARANG
    // ==========================================
    private void tampilkanKelolaBarang() {
        contentArea.getChildren().clear(); 
        
        HalamanKelolaBarang halaman = new HalamanKelolaBarang();
        VBox view = halaman.getTampilan();
        
        VBox.setVgrow(view, Priority.ALWAYS);
        contentArea.getChildren().add(view);
    }

    // ==========================================
    // MENU 3: KELOLA USER
    // ==========================================
    private void tampilkanKelolaUser() {
        contentArea.getChildren().clear(); 
        
        HalamanKelolaUser halaman = new HalamanKelolaUser();
        VBox view = halaman.getTampilan();
        
        VBox.setVgrow(view, Priority.ALWAYS);
        contentArea.getChildren().add(view);
    }

    // ==========================================
    // MENU 4: DATA PESANAN
    // ==========================================
    private void tampilkanDataPesanan() {
        contentArea.getChildren().clear(); 
        
        HalamanDataPesanan halaman = new HalamanDataPesanan();
        VBox view = halaman.getTampilan();
        
        VBox.setVgrow(view, Priority.ALWAYS);
        contentArea.getChildren().add(view);
    }
}