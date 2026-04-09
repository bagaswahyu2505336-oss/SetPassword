package setpassword;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class HalamanJadwalKurir {
    
    private int idKurir;

    public HalamanJadwalKurir(int idKurir) {
        this.idKurir = idKurir;
    }

    public VBox getTampilan() {
        VBox tableBox = new VBox(10);
        tableBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10,0,0,4);");
        Label lblJudul = new Label("🚚 Daftar Pengiriman Anda");
        lblJudul.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1F4A75;");
        
        TableView<ModelData.JadwalKurir> tabelJadwal = new TableView<>();
        tabelJadwal.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<ModelData.JadwalKurir, String> colId = new TableColumn<>("ID Kirim"); colId.setCellValueFactory(new PropertyValueFactory<>("idPengiriman"));
        TableColumn<ModelData.JadwalKurir, String> colToko = new TableColumn<>("Tujuan (Toko)"); colToko.setCellValueFactory(new PropertyValueFactory<>("namaToko"));
        TableColumn<ModelData.JadwalKurir, String> colAlamat = new TableColumn<>("Alamat Lengkap"); colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        TableColumn<ModelData.JadwalKurir, String> colStatus = new TableColumn<>("Status"); colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tabelJadwal.getColumns().addAll(colId, colToko, colAlamat, colStatus);

        try {
            tabelJadwal.setItems(FXCollections.observableArrayList(FungsiDB.getJadwalKurir(idKurir)));
        } catch (GrosirException e) { ErrorHandler.handleException("Memuat Jadwal", e); }

        HBox formUpdate = new HBox(15);
        formUpdate.setAlignment(Pos.CENTER_LEFT);
        TextField txtIdKirim = new TextField(); txtIdKirim.setEditable(false); 
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("dalam perjalanan", "sampai", "selesai");
        Button btnUpdate = new Button("Update Status");
        btnUpdate.setStyle("-fx-background-color: #1F4A75; -fx-text-fill: white; -fx-font-weight: bold;");

        tabelJadwal.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) { txtIdKirim.setText(newSel.getIdPengiriman()); cbStatus.setValue(newSel.getStatus()); }
        });

        btnUpdate.setOnAction(e -> {
            if (txtIdKirim.getText().isEmpty() || cbStatus.getValue() == null) return;
            try {
                if (FungsiDB.updateStatusPengiriman(txtIdKirim.getText(), cbStatus.getValue())) {
                    ErrorHandler.tampilkanSukses("Berhasil", "Status diperbarui!");
                    tabelJadwal.setItems(FXCollections.observableArrayList(FungsiDB.getJadwalKurir(idKurir)));
                }
            } catch (GrosirException ex) { ErrorHandler.handleException("Update Status", ex); }
        });

        formUpdate.getChildren().addAll(new Label("ID:"), txtIdKirim, cbStatus, btnUpdate);
        VBox.setVgrow(tabelJadwal, Priority.ALWAYS);
        tableBox.getChildren().addAll(lblJudul, tabelJadwal, formUpdate);
        
        return tableBox;
    }
}