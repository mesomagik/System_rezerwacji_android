package com.example.bartek.praca_inzynierska;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class UserPanelActivity extends AppCompatActivity {

    private Button bDodajWizyte;
    private Button bWiadomosci;
    private Integer id_zalogowanego;
    private Button bWyloguj;
    private Button bEdycjaDanych;
    private Button bTwojeWizyty;

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
        setContentView(R.layout.activity_user_panel);

        Intent intent_id = getIntent();
        id_zalogowanego = Integer.valueOf(intent_id.getStringExtra("id_zalogowanego"));

        bDodajWizyte = (Button)findViewById(R.id.bDodajWizyte);
        bWiadomosci = (Button) findViewById(R.id.bWiadomosci);
        bWyloguj = (Button) findViewById(R.id.bWyloguj);
        bEdycjaDanych = (Button) findViewById(R.id.bEdycjaDanych);
        bTwojeWizyty = (Button) findViewById(R.id.bTwojeWizyty);

        Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        bDodajWizyte.setTypeface(fontFamily);
        bDodajWizyte.setText("Dodaj wizytę\n\n\uF196");
        bWiadomosci.setTypeface(fontFamily);
        bWiadomosci.setText("Wiadomości\n\n\uF003");
        bWyloguj.setTypeface(fontFamily);
        bWyloguj.setText("Wyloguj się \uF08B");
        bEdycjaDanych.setTypeface(fontFamily);
        bEdycjaDanych.setText("Edytuj dane\n\n\uF007");
        bTwojeWizyty.setTypeface(fontFamily);
        bTwojeWizyty.setText("Twoje wizyty\n\n\uF017");

        bDodajWizyte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UstalWizyteActivity.class);
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

        bWiadomosci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),KorespondencjaWybranieWizytyActivity.class);
                intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent);
            }
        });
        bEdycjaDanych.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DaneOsoboweActivity.class);
                intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent);
            }
        });

        bTwojeWizyty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TwojeWizytyAcitvity.class);
                intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent);
            }
        });
    }
}
