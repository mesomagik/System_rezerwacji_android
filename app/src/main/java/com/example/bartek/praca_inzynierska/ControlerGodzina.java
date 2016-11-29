package com.example.bartek.praca_inzynierska;

import java.io.Serializable;
import java.sql.Time;

/**
 * Created by Bartek on 2016-04-14.
 */
public class ControlerGodzina implements Serializable {

    private Integer id_godzina;
    private String godzina;

    ControlerGodzina(Integer id_godzina, String godzina) {
    this.setId_godzina(id_godzina);
        this.setGodzina(godzina);
    }

    public String toString()
    {
        return( godzina );
    }

    public ControlerGodzina getObj(){
        return this;
    }
    public Integer getId_godzina() {
        return id_godzina;
    }

    public void setId_godzina(Integer id_godzina) {
        this.id_godzina = id_godzina;
    }

    public String getGodzina() {
        return godzina;
    }

    public void setGodzina(String godzina) {
        this.godzina = godzina;
    }
}
