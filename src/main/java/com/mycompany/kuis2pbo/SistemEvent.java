/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kuis2pbo;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public class SistemEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String namaEO;
    private Map<String, Peserta> daftarPeserta;
    private Map<String, TiketAcara> daftarTiket;
    private List<Transaksi> riwayatTransaksi;

    public SistemEvent(String namaEO) {
        this.namaEO = namaEO;
        this.daftarPeserta = new HashMap<>();
        this.daftarTiket = new HashMap<>();
        this.riwayatTransaksi = new ArrayList<>();
    }

    public void tambahPeserta(String id, String nama) {
        daftarPeserta.put(id, new Peserta(id, nama));
    }

    public void tambahTiketAcara(String kode, String namaAcara, int kuota) {
        daftarTiket.put(kode, new TiketAcara(kode, namaAcara, kuota));
    }

    // DIGIT 2 (Varian 6): Parsing format "ID>ACT-01.KD>OBJ-A.JML>3.|..."
    public void jalankanTransaksiDariFile(String namaFile) throws IOException {
        File file = new File(namaFile);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + namaFile + " tidak ditemukan di direktori project.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String baris;
            while ((baris = br.readLine()) != null) {
                if (baris.trim().isEmpty()) continue;

                // Pecah antar transaksi berdasarkan karakter pipe '|'
                String[] records = baris.split("\\|");
                for (String record : records) {
                    record = record.trim();
                    if (record.isEmpty()) continue;

                    try {
                        prosesSatuRecord(record);
                    } catch (TransaksiException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.out.println("[TRANSAKSI GAGAL] (" + record + ") -> " + e.getMessage());
                    }
                }
            }
        }
    }

    private void prosesSatuRecord(String record) throws TransaksiException {
        // Format: ID>ACT-01.KD>OBJ-A.JML>3.
        String[] bagian = record.split("\\.");
        if (bagian.length < 3) {
            throw new TransaksiException("Format record tidak lengkap.");
        }

        String idPeserta = bagian[0].trim().split(">")[1].trim();
        String kodeTiket = bagian[1].trim().split(">")[1].trim();
        int jumlah = Integer.parseInt(bagian[2].trim().split(">")[1].trim());

        Peserta peserta = daftarPeserta.get(idPeserta);
        if (peserta == null) {
            throw new TransaksiException("ID Peserta '" + idPeserta + "' tidak terdaftar.");
        }

        TiketAcara tiket = daftarTiket.get(kodeTiket);
        if (tiket == null) {
            throw new TransaksiException("Kode Tiket '" + kodeTiket + "' tidak ditemukan.");
        }

        tiket.kurangiKuota(jumlah);
        Transaksi trx = new Transaksi(peserta, tiket, jumlah);
        riwayatTransaksi.add(trx);

        System.out.println("[SUKSES] " + peserta.getNama() + " memesan " + jumlah + "x " + tiket.getNama());
    }

    // DIGIT 3 (Varian 9): Serialization ke 'state.bak' & Laporan Format Kolom ke 'laporan.txt'
    public void simpanLaporanAkhir() {
        // 1. Object Serialization ke state.bak
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("state.bak"))) {
            oos.writeObject(this);
            System.out.println("\n[SERIALIZATION] State objek berhasil disimpan ke 'state.bak'.");
        } catch (IOException e) {
            System.out.println("[ERROR SERIALIZATION] Gagal menyimpan state.bak: " + e.getMessage());
        }

        // 2. Penulisan File Teks Format Kolom (String.format) ke laporan.txt
        try (PrintWriter pw = new PrintWriter(new FileWriter("laporan.txt"))) {
            pw.println("=====================================================================================");
            pw.println("LAPORAN TRANSAKSI RESMI - " + namaEO.toUpperCase());
            pw.println("=====================================================================================");
            pw.println(String.format("%-4s | %-10s | %-15s | %-10s | %-22s | %-6s",
                    "NO", "ID PESERTA", "NAMA PESERTA", "KODE TIKET", "NAMA ACARA", "QTY"));
            pw.println("-------------------------------------------------------------------------------------");

            int no = 1;
            for (Transaksi t : riwayatTransaksi) {
                pw.println(String.format("%-4d | %-10s | %-15s | %-10s | %-22s | %-6d",
                        no++,
                        t.getPeserta().getId(),
                        t.getPeserta().getNama(),
                        t.getTiket().getId(),
                        t.getTiket().getNama(),
                        t.getJumlah()));
            }

            pw.println("=====================================================================================");
            pw.println("SISA KUOTA TIKET ACARA:");
            pw.println("-------------------------------------------------------------------------------------");
            pw.println(String.format("%-10s | %-25s | %-12s", "KODE", "NAMA ACARA", "SISA KUOTA"));
            pw.println("-------------------------------------------------------------------------------------");
            for (TiketAcara tiket : daftarTiket.values()) {
                pw.println(String.format("%-10s | %-25s | %-12d",
                        tiket.getId(),
                        tiket.getNama(),
                        tiket.getKuota()));
            }
            pw.println("=====================================================================================");

            System.out.println("[FILE I/O] Laporan format kolom berhasil ditulis ke 'laporan.txt'.");
        } catch (IOException e) {
            System.out.println("[ERROR FILE I/O] Gagal menulis laporan.txt: " + e.getMessage());
        }
    }
}
