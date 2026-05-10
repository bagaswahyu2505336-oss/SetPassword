package Role_Admin;

import javafx.scene.layout.*;
import Antarmuka_Aset_Visual.DashboardBase;

public class DashboardAdmin extends DashboardBase {

    @Override
    protected String getTitle() { return "Dashboard Administrator"; }

    @Override
    public void loadMenu() {
        tambahMenu("⊞ Dashboard", this::tampilkanDashboard);
        tambahMenu("📦 Kelola Barang", this::tampilkanKelolaBarang);
        tambahMenu("👥 Kelola User", this::tampilkanKelolaUser);
        tambahMenu("🛒 Data Pesanan", this::tampilkanDataPesanan);
    }

    @Override
    public void buildContent() { 
        tampilkanDashboard(); 
    }

    private void tampilkanDashboard() {
        contentArea.getChildren().clear(); 
        
        HalamanUtamaAdmin halaman = new HalamanUtamaAdmin(this); 
        VBox view = halaman.getTampilan();
        
        VBox.setVgrow(view, Priority.ALWAYS);
        contentArea.getChildren().add(view);
    }

    private void tampilkanKelolaBarang() {
        contentArea.getChildren().clear(); 
        
        HalamanKelolaBarang halaman = new HalamanKelolaBarang();
        VBox view = halaman.getTampilan();
        
        VBox.setVgrow(view, Priority.ALWAYS);
        contentArea.getChildren().add(view);
    }

    private void tampilkanKelolaUser() {
        contentArea.getChildren().clear(); 
        
        HalamanKelolaUser halaman = new HalamanKelolaUser();
        VBox view = halaman.getTampilan();
        
        VBox.setVgrow(view, Priority.ALWAYS);
        contentArea.getChildren().add(view);
    }

    private void tampilkanDataPesanan() {
        contentArea.getChildren().clear(); 
        
        HalamanDataPesanan halaman = new HalamanDataPesanan();
        VBox view = halaman.getTampilan();
        
        VBox.setVgrow(view, Priority.ALWAYS);
        contentArea.getChildren().add(view);
    }
}