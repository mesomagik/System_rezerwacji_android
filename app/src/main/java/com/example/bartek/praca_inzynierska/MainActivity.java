package com.example.bartek.praca_inzynierska;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button bUzytkownicy;
    Button bWybierzDate;
    Button bKorespondencja;
    Button bLogowanie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bUzytkownicy = (Button)findViewById(R.id.bUzytkownicy);
        bWybierzDate = (Button)findViewById(R.id.bWybierzDate);
        bKorespondencja = (Button) findViewById(R.id.bKorespondecja);
        bLogowanie = (Button) findViewById(R.id.bLogowanie);

        bUzytkownicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AllUsersActivity.class));
            }
        });
        bWybierzDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UstalWizyteActivity.class));
            }
        });

        bKorespondencja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,KorespondencjaWybranieWizytyActivity.class));
            }
        });

        bLogowanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LogowanieActivity.class));
            }
        });
    }
}
