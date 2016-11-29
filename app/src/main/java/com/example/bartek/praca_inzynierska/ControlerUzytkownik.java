package com.example.bartek.praca_inzynierska;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Bartek on 2016-03-22.
 */
public class ControlerUzytkownik implements Serializable {
    private Integer id;
    private String email;
    private String imie;
    private String nazwisko;
    private String adres;
    private Integer telefon;
    private String pesel;
    private String haslo;
    private Boolean aktywne;



    public ControlerUzytkownik(Integer id,
                               String email,
                               String imie,
                               String nazwisko,
                               String adres,
                               Integer telefon,
                               String haslo,
                               Boolean aktywne,
                               String pesel
    ){

        this.setEmail(email);
        this.setImie(imie);
        this.setId(id);
        this.setNazwisko(nazwisko);
        this.setAdres(adres);
        this.setHaslo(haslo);
        this.setAktywne(aktywne);
        this.setTelefon(telefon);
        this.setPesel(pesel);

    }


    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }
    public ControlerUzytkownik getObj(){
        return this;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public void setTelefon(Integer telefon) {
        this.telefon = telefon;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public Integer getTelefon() {
        return telefon;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public Boolean getAktywne() {
        return aktywne;
    }

    public void setAktywne(Boolean aktywne) {
        this.aktywne = aktywne;
    }



}
