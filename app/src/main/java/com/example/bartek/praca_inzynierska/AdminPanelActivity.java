package com.example.bartek.praca_inzynierska;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class AdminPanelActivity extends AppCompatActivity {

    private Button bWizyty;
    private Button bWiadomosci;
    private Button bPacjenci;
    private Button bDodajDzien;
    private Integer id_zalogowanego;
    private Button bWyloguj;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            startActivity(new Intent(getApplicationContext(),LogowanieActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_panel);

        Intent intent_id = getIntent();
        id_zalogowanego = Integer.valueOf(intent_id.getStringExtra("id_zalogowanego"));

        bWiadomosci = (Button) findViewById(R.id.bWiadomosci);
        bWizyty = (Button) findViewById(R.id.bWizyty);
        bPacjenci = (Button) findViewById(R.id.bPacjenci);
        bWyloguj = (Button) findViewById(R.id.bWyloguj);
        bDodajDzien = (Button) findViewById(R.id.bDodajDzien);

        Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        bWizyty.setTypeface(fontFamily);
        bWizyty.setText("Wizyty\n\n\uF017");
        bPacjenci.setTypeface(fontFamily);
        bPacjenci.setText("Nowi pacjenci\n\n\uF0C0");
        bDodajDzien.setTypeface(fontFamily);
        bDodajDzien.setText("Dodaj dzień\n\n\uF196");
        bWiadomosci.setTypeface(fontFamily);
        bWiadomosci.setText("Wiadomości\n\n\uF003");
        bWyloguj.setTypeface(fontFamily);
        bWyloguj.setText("Wyloguj się \uF08B");

        bWizyty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminWizytyActivity.class);
                intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent);
            }
        });

        bWiadomosci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminWybranieKorespondencji.class);
                intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent);
            }
        });

        bPacjenci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminNowiPacjenciActivity.class);
                intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent);
            }
        });

        bDodajDzien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminDodajDzienActivity.class);
                intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent);
            }
        });

        bWyloguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(),LogowanieActivity.class));
            }
        });

    }
}
