package com.example.bartek.praca_inzynierska;

import java.io.Serializable;
import java.util.Date;

public class ControlerDzien implements Serializable {
    private Integer id_dzien;
    private String data;
    private Integer dzien_id_godzina_pocz;
    private Integer dzien_id_godzina_kon;

    ControlerDzien(Integer id_dzien, String data, Integer dzien_id_godzina_pocz, Integer dzien_id_godzina_kon){
        this.setId_dzien(id_dzien);
        this.setData(data);
        this.setDzien_id_godzina_pocz(dzien_id_godzina_pocz);
        this.setDzien_id_godzina_kon(dzien_id_godzina_pocz);
    }

    public ControlerDzien getObj(){
        return this;
    }

    public Integer getId_dzien() {
        return id_dzien;
    }

    public void setId_dzien(Integer id_dzien) {
        this.id_dzien = id_dzien;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getdzien_id_godzina_pocz() {
        return dzien_id_godzina_pocz;
    }

    public void setDzien_id_godzina_pocz(Integer dzien_id_godzina_pocz) {
        this.dzien_id_godzina_pocz = dzien_id_godzina_pocz;
    }

    public Integer getDzien_id_godzina_kon() {
        return dzien_id_godzina_kon;
    }

    public void setDzien_id_godzina_kon(Integer dzien_id_godzina_kon) {
        this.dzien_id_godzina_kon = dzien_id_godzina_kon;
    }
}
