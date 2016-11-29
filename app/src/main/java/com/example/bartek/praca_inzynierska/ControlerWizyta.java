package com.example.bartek.praca_inzynierska;

import java.io.Serializable;

/**
 * Created by Bartek on 2016-04-14.
 */
public class ControlerWizyta implements Serializable{
    private Integer id_wizyta;
    private Integer wizyta_id_uzytkownik;
    private Integer wizyta_id_data;
    private Boolean odbyta;
    private Integer wizyta_id_godzina;
    private Boolean pierwsza;

    ControlerWizyta(Integer id_wizyta, Integer wizyta_id_uzytkownik, Integer wizyta_id_data, Boolean odbyta, Integer wizyta_id_godzina,Boolean pierwsza){
        this.setId_wizyta(id_wizyta);
        this.setWizyta_id_uzytkownik(wizyta_id_uzytkownik);
        this.setWizyta_id_data(wizyta_id_data);
        this.setOdbyta(odbyta);
        this.setWizyta_id_godzina(wizyta_id_godzina);
        this.setPierwsza(pierwsza);
    }

    public ControlerWizyta getObj(){
        return this;
    }

    public Boolean getPierwsza() {
        return pierwsza;
    }

    public void setPierwsza(Boolean pierwsza) {
        this.pierwsza = pierwsza;
    }

    public Integer getId_wizyta() {
        return id_wizyta;
    }

    public void setId_wizyta(Integer id_wizyta) {
        this.id_wizyta = id_wizyta;
    }

    public Integer getWizyta_id_uzytkownik() {
        return wizyta_id_uzytkownik;
    }

    public void setWizyta_id_uzytkownik(Integer wizyta_id_uzytkownik) {
        this.wizyta_id_uzytkownik = wizyta_id_uzytkownik;
    }

    public Integer getWizyta_id_data() {
        return wizyta_id_data;
    }

    public void setWizyta_id_data(Integer wizyta_id_data) {
        this.wizyta_id_data = wizyta_id_data;
    }

    public Boolean getOdbyta() {
        return odbyta;
    }

    public void setOdbyta(Boolean odbyta) {
        this.odbyta = odbyta;
    }

    public Integer getWizyta_id_godzina() {
        return wizyta_id_godzina;
    }

    public void setWizyta_id_godzina(Integer wizyta_id_godzina) {
        this.wizyta_id_godzina = wizyta_id_godzina;
    }
}
