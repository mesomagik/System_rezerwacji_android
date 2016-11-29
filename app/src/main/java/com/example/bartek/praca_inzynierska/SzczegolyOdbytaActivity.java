package com.example.bartek.praca_inzynierska;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class SzczegolyOdbytaActivity extends AppCompatActivity {

    private Button bWroc;
    private Button bKorespondencja;
    private RequestQueue wizytaQueue;
    private String wizytaUrl = "http://mesomagik.ugu.pl/praca_inzynierska/showSzczegolyNadchodzaca.php";
    private TextView tvWizyta;
    private TextView tvWiadomosc;
    private ControlerWizyta controlerWizyta;
    private String godzina;
    private String dzien;
    private String wiadomosc;
    private String id_zalogowanego;
    private  String wiadomosc_id_user;
    private ProgressDialog progressDialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent_w = new Intent(getApplicationContext(),TwojeWizytyAcitvity.class);
            intent_w.putExtra("id_zalogowanego",id_zalogowanego);
            finish();
            startActivity(intent_w);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szczegoly_odbyta);

        Intent intent = getIntent();
        controlerWizyta = (ControlerWizyta) intent.getSerializableExtra("wizyta");
        id_zalogowanego = intent.getStringExtra("id_zalogowanego");

        bKorespondencja = (Button) findViewById(R.id.bKorespondencja);
        bWroc = (Button) findViewById(R.id.bWroc);
        tvWizyta = (TextView) findViewById(R.id.tvWizyta);
        tvWiadomosc = (TextView) findViewById(R.id.tvWiadomosc);

        wizytaQueue = Volley.newRequestQueue(getApplicationContext());

        final HashMap<String,String> params = new HashMap<>();
        params.put("id_wizyta",controlerWizyta.getId_wizyta().toString());
        params.put("wizyta_id_data",controlerWizyta.getWizyta_id_data().toString());
        params.put("wizyta_id_godzina",controlerWizyta.getWizyta_id_godzina().toString());

        progressDialog = new ProgressDialog(SzczegolyOdbytaActivity.this);
        progressDialog.setMessage("Pobieranie danych...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        CustomRequest request = new CustomRequest(wizytaUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    JSONArray wizytaL = response.getJSONArray("wizyta");
                    for (int i = 0; i < wizytaL.length(); i++) {
                        JSONObject wizyta  = wizytaL.getJSONObject(i);
                        dzien = wizyta.getString("dzien");
                        godzina = wizyta.getString("godzina");
                        wiadomosc = wizyta.getString("tekst");
                        wiadomosc_id_user = wizyta.getString("wiadomosc_id_user");
                    }

                    tvWizyta.setText("Wizyta w dniu " + dzien + "\no godzinie " + godzina.substring(0,5));
                    if(wiadomosc_id_user.equals("1")){
                        tvWiadomosc.setText("Administrator:\n"+wiadomosc);
                    }else {
                        tvWiadomosc.setText("Ty:\n"+wiadomosc);
                    }



                    bWroc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent_w = new Intent(getApplicationContext(),TwojeWizytyAcitvity.class);
                            intent_w.putExtra("id_zalogowanego",id_zalogowanego);
                            finish();
                            startActivity(intent_w);
                        }
                    });


                    bKorespondencja.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent_w = new Intent(getApplicationContext(),WiadomosciKorespondencjiActivity.class);
                            intent_w.putExtra("wizyta",controlerWizyta.getObj());
                            intent_w.putExtra("id_zalogowanego",id_zalogowanego.toString());
                            intent_w.putExtra("powrotDo","szczegolyOdbyta");
                            finish();
                            startActivity(intent_w);
                        }
                    });

                }catch (JSONException e){
                    Log.e("blad2",e.toString());
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("blad2",error.toString());
                progressDialog.dismiss();
                if (error instanceof NoConnectionError) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SzczegolyOdbytaActivity.this);

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
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        wizytaQueue.add(request);

    }
}
