package com.example.bartek.praca_inzynierska;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DaneOsoboweActivity extends AppCompatActivity {

    private ListView lvDaneOsobowe;
    private String uzytkownikUrl= "http://mesomagik.ugu.pl/praca_inzynierska/getUserById.php";
    private String id;
    private RequestQueue pobierzUzytkownikaQueue;
    private Integer id_zalogowanego;

    private Button bAdres;
    private Button bTelefon;
    private Button bImieNazwisko;
    private Button bHaslo;
    private Button bEmail;

    private TextView tvImieNazwisko;
    private TextView tvTelefon;
    private TextView tvAdres;
    private TextView tvEmail;
    private Button bWroc;
    private ProgressDialog progressDialog;

    private ControlerUzytkownik controlerUzytkownik;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent_w = new Intent(getApplicationContext(),UserPanelActivity.class);
            intent_w.putExtra("id_zalogowanego",id_zalogowanego.toString());
            finish();
            startActivity(intent_w);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dane_osobowe);
        Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        bAdres = (Button) findViewById(R.id.bAdres);
        bTelefon = (Button) findViewById(R.id.bTelefon);
        bImieNazwisko = (Button) findViewById(R.id.bImieNazwisko);
        bHaslo = (Button) findViewById(R.id.bHaslo);
        bEmail = (Button) findViewById(R.id.bEmail);
        bWroc = (Button) findViewById(R.id.bWroc);

        bAdres.setTypeface(fontFamily);
        bAdres.setText("\uF041");
        bTelefon.setTypeface(fontFamily);
        bTelefon.setText("\uF095");
        bImieNazwisko.setTypeface(fontFamily);
        bImieNazwisko.setText("\uF007");
        bHaslo.setTypeface(fontFamily);
        bHaslo.setText("\uF13E");
        bEmail.setTypeface(fontFamily);
        bEmail.setText("\uF0E0");


        tvImieNazwisko = (TextView) findViewById(R.id.tvImieNazwisko);
        tvTelefon = (TextView) findViewById(R.id.tvTelefon);
        tvAdres = (TextView) findViewById(R.id.tvAdres);
        tvEmail = (TextView) findViewById(R.id.tvEmail);

        Intent intent_id = getIntent();
        id_zalogowanego = Integer.valueOf(intent_id.getStringExtra("id_zalogowanego"));

        pobierzUzytkownikaQueue = Volley.newRequestQueue(getApplicationContext());

        final HashMap<String, String> uzytkownikParams = new HashMap<>();
            uzytkownikParams.put("id_zalogowanego",id_zalogowanego.toString());

        progressDialog = new ProgressDialog(DaneOsoboweActivity.this);
        progressDialog.setMessage("Pobieranie danych...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        CustomRequest uzytkownikRequest = new CustomRequest(uzytkownikUrl, uzytkownikParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    progressDialog.dismiss();
                    final ArrayList<ControlerUzytkownik> userList = new ArrayList<ControlerUzytkownik>();
                    JSONArray users = response.getJSONArray("user");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        boolean active = Boolean.parseBoolean(user.getString("aktywne")); //parsowanie z bazy czy aktywne
                        controlerUzytkownik = new ControlerUzytkownik(
                                user.getInt("id"),
                                user.getString("email"),
                                user.getString("imie"),
                                user.getString("nazwisko"),
                                user.getString("adres"),
                                user.getInt("telefon"),
                                user.getString("haslo"),
                                active,
                                user.getString("pesel"));

                        Log.e("imie", controlerUzytkownik.getTelefon().toString());
                        tvAdres.setText(controlerUzytkownik.getAdres());
                        tvTelefon.setText(controlerUzytkownik.getTelefon().toString());
                        tvImieNazwisko.setText(controlerUzytkownik.getImie() + ' ' + controlerUzytkownik.getNazwisko());
                        tvEmail.setText(controlerUzytkownik.getEmail());
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                    Log.e("imie", e.toString());
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("imie", error.toString());
                if (error instanceof NoConnectionError) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DaneOsoboweActivity.this);

                    builder.setTitle("brak połączenia z internetem");

                    builder.setPositiveButton("odśwież", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(getIntent());
                        }
                    });
                    builder.setNeutralButton("wyjdź", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(new Intent(getApplicationContext(),LogowanieActivity.class));
                        }
                    });
                    builder.show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return uzytkownikParams;
            }
        };
        pobierzUzytkownikaQueue.add(uzytkownikRequest);

        bImieNazwisko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),EdytujDaneImieNazwisko.class);
                intent.putExtra("controlerUzytkownik",controlerUzytkownik.getObj());
                finish();
                startActivity(intent);
            }
        });

        bAdres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),EdytujDaneAdres.class);
                intent.putExtra("controlerUzytkownik",controlerUzytkownik.getObj());
                finish();
                startActivity(intent);
            }
        });

        bTelefon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),EdytujDaneTelefon.class);
                intent.putExtra("controlerUzytkownik",controlerUzytkownik.getObj());
                finish();
                startActivity(intent);
            }
        });

        bHaslo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),EdytujDaneHaslo.class);
                intent.putExtra("controlerUzytkownik",controlerUzytkownik.getObj());
                finish();
                startActivity(intent);
            }
        });

        bEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),EdytujDaneEmail.class);
                intent.putExtra("controlerUzytkownik",controlerUzytkownik.getObj());
                finish();
                startActivity(intent);
            }
        });

        bWroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_w = new Intent(getApplicationContext(),UserPanelActivity.class);
                intent_w.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent_w);
            }
        });
    }

}
