/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kuis2pbo;

/**
 *
 * @author ASUS
 */
public class TiketAcara extends EntitasEvent {
    private static final long serialVersionUID = 1L;
    private int kuota;

    public TiketAcara(String kode, String namaAcara, int kuota) {
        super(kode, namaAcara);
        this.kuota = kuota;
    }

    public int getKuota() {
        return kuota;
    }

    public void kurangiKuota(int jumlah) throws TransaksiException {
        if (jumlah <= 0) {
            throw new TransaksiException("Jumlah pemesanan harus lebih dari 0.");
        }
        if (this.kuota < jumlah) {
            throw new TransaksiException("Kuota tiket '" + getNama() + "' tidak mencukupi (Sisa: " + this.kuota + ", Diminta: " + jumlah + ")");
        }
        this.kuota -= jumlah;
    }

    @Override
    public String getInfoDetail() {
        return "Tiket [" + getId() + "] " + getNama() + " (Sisa Kuota: " + kuota + ")";
    }
}
