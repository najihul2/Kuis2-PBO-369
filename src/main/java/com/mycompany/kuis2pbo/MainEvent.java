/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.kuis2pbo;

/**
 *
 * @author ASUS
 */
public class MainEvent {
    public static void main(String[] args) {
        SistemEvent event = new SistemEvent("Event Organizer Puncak");
        event.tambahPeserta("ACT-01", "Qori");
        event.tambahPeserta("ACT-02", "Rafi");
        event.tambahTiketAcara("OBJ-A", "Konser VIP Premium", 10);
        event.tambahTiketAcara("OBJ-B", "Workshop Ekonomis", 50);
        event.tambahTiketAcara("OBJ-C", "Seminar Reguler", 2);

        System.out.println("--- MEMULAI PEMBACAAN FILE TRANSAKSI ---");
        try {
            event.jalankanTransaksiDariFile("input_transaksi.txt");
        } catch (Exception e) {
            System.out.println("[SISTEM FATAL ERROR] File gagal dibaca/diproses: " + e.getMessage());
        }
        event.simpanLaporanAkhir();
    }
}
