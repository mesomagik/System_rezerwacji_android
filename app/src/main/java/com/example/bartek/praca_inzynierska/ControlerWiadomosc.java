package com.example.bartek.praca_inzynierska;

/**
 * Created by Bartek on 2016-04-14.
 */
public class ControlerWiadomosc {
    private Integer id_wiadomosc;
    private Integer wiadomosc_id_wizyta;
    private Integer wiadomosc_id_user;
    private String tekst;

    ControlerWiadomosc(Integer id_wiadomosc, Integer wiadomosc_id_wizyta, Integer wiadomosc_id_user, String tekst){
        this.setId_wiadomosc(id_wiadomosc);
        this.setWizyta_id_wizyta(wiadomosc_id_wizyta);
        this.setUser_id_user(wiadomosc_id_user);
        this.setTekst(tekst);
    }

    public Integer getWizyta_id_wizyta() {
        return wiadomosc_id_wizyta;
    }

    public void setWizyta_id_wizyta(Integer wizyta_id_wizyta) {
        this.wiadomosc_id_wizyta = wizyta_id_wizyta;
    }

    public Integer getId_wiadomosc() {
        return id_wiadomosc;
    }

    public void setId_wiadomosc(Integer id_wiadomosc) {
        this.id_wiadomosc = id_wiadomosc;
    }

    public Integer getUser_id_user() {
        return wiadomosc_id_user;
    }

    public void setUser_id_user(Integer user_id_user) {
        this.wiadomosc_id_user = user_id_user;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }
}


