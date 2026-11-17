/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.kuis2pbo;

import java.io.Serializable;

/**
 *
 * @author ASUS
 */
public abstract class EntitasEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String nama;

    public EntitasEvent(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    // Polymorphism method
    public abstract String getInfoDetail();
}
