package com.example.bartek.praca_inzynierska;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogowanieActivity extends AppCompatActivity {


    private Button bLogin;
    private EditText etLogin;
    private EditText etHaslo;
    private RequestQueue zaloguj;
    private String zalogujURL = "http://mesomagik.ugu.pl/praca_inzynierska/zaloguj.php"; //10.0.2.2
    private Integer id = -1;
    private Integer zalogowany = -1;
    private Button bRejestracja;
    private Integer aktywny = -1;
    private ProgressDialog progressDialog;
    private Button bHaslo;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie);

        etLogin = (EditText) findViewById(R.id.etLogin);
        etHaslo = (EditText) findViewById(R.id.etHaslo);
        bLogin = (Button) findViewById(R.id.bLogin);
        bRejestracja = (Button) findViewById(R.id.bRejestracja);
        bHaslo = (Button) findViewById(R.id.bHaslo);

        zaloguj = Volley.newRequestQueue(getApplicationContext());

        bHaslo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(),PrzypomnienieHaslaActivity.class));
            }
        });

        bRejestracja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(),Rejestracja1Activity.class));
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etLogin.getText().length()>0 && etHaslo.getText().length()>0) {

                    final HashMap<String, String> zalogujParams = new HashMap<>();
                    zalogujParams.put("login",etLogin.getText().toString());
                    zalogujParams.put("haslo",etHaslo.getText().toString());


                    progressDialog = new ProgressDialog(LogowanieActivity.this);
                    progressDialog.setMessage("Logowanie");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    CustomRequest zalogujRequest = new CustomRequest(zalogujURL, zalogujParams, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{progressDialog.dismiss();

                            zalogowany = Integer.parseInt(response.getString("zalogowany"));
                            Log.e("zalogowany",zalogowany.toString());
                            id = Integer.parseInt(response.getString("id"));
                            Log.e("id",id.toString());
                            aktywny = Integer.parseInt(response.getString("aktywny"));
                            Log.e("aktywny",aktywny.toString());
                            if(zalogowany.intValue()==1) {
                                Log.e("id", id.toString());
                                if (aktywny.intValue() == 1) {
                                    if (id.intValue() == 1) {
                                        Intent intent = new Intent(getApplicationContext(), AdminPanelActivity.class);//TODO panel admina
                                        intent.putExtra("id_zalogowanego", id.toString());
                                        Toast.makeText(getApplicationContext(), "Pomyślnie zalogowano!", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), UserPanelActivity.class);//TODO panel uzytkownika
                                        intent.putExtra("id_zalogowanego", id.toString());
                                        Toast.makeText(getApplicationContext(), "Pomyślnie zalogowano!", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(intent);
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Konto nie jest aktywne!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), LogowanieActivity.class);
                                    finish();
                                    startActivity(intent);
                                }

                            }else {
                                Toast.makeText(getApplicationContext(), "Niepoprawne dane!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LogowanieActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(LogowanieActivity.this, "brak połączenia z internetem", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return zalogujParams;
                    }
                };
                zaloguj.add(zalogujRequest);

            }
            }
        });




    }
}
