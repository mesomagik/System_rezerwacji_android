package com.example.bartek.praca_inzynierska;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EdytujDaneImieNazwisko extends AppCompatActivity {

    private EditText etImie;
    private EditText etNazwisko;
    private RequestQueue updateQueue;
    private Button bWroc;
    private Button bEdytuj;
    private String edytujUrl = "http://mesomagik.ugu.pl/praca_inzynierska/updateUserImieNazwisko.php";


    ControlerUzytkownik controlerUzytkownik = new ControlerUzytkownik(-1,"1","1","1","1",1,"1",true,"1");

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent2 = new Intent(getApplicationContext(),DaneOsoboweActivity.class);
            intent2.putExtra("id_zalogowanego",controlerUzytkownik.getId().toString());
            finish();
            startActivity(intent2);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edytuj_dane_imie_nazwisko);

        updateQueue = Volley.newRequestQueue(getApplicationContext());

        final Intent intent = getIntent();
        controlerUzytkownik = (ControlerUzytkownik) intent.getSerializableExtra("controlerUzytkownik");

        etImie = (EditText) findViewById(R.id.etEmail1);
        etNazwisko = (EditText) findViewById(R.id.etEmail2);
        bWroc = (Button) findViewById(R.id.bWroc);
        bEdytuj = (Button) findViewById(R.id.bEdytuj);

        etImie.setText(controlerUzytkownik.getImie());
        etNazwisko.setText(controlerUzytkownik.getNazwisko());



        bEdytuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etImie.length()>0 && etNazwisko.length()>0){

                    controlerUzytkownik.setImie(etImie.getText().toString());
                    controlerUzytkownik.setNazwisko(etNazwisko.getText().toString());

                    Log.e("id",controlerUzytkownik.getId().toString());
                    Log.e("imie",controlerUzytkownik.getImie());
                    Log.e("nazwisko",controlerUzytkownik.getNazwisko());


                    final HashMap<String, String> edytujParams = new HashMap<>();
                    edytujParams.put("id",controlerUzytkownik.getId().toString());
                    edytujParams.put("imie",etImie.getText().toString());
                    edytujParams.put("nazwisko",etNazwisko.getText().toString());


                    CustomRequest edytuj = new CustomRequest(edytujUrl, edytujParams, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Intent intent2 = new Intent(getApplicationContext(),DaneOsoboweActivity.class);
                                if (response.getInt("success") == 1) {
                                    Log.e("success", response.getString("success"));
                                    Toast.makeText(getApplicationContext(), "Edytowano dane!", Toast.LENGTH_LONG).show();
                                    intent2.putExtra("id_zalogowanego",controlerUzytkownik.getId().toString());
                                    finish();
                                    startActivity(intent2);
                                } else {
                                    Log.e("success", response.getString("success"));
                                    Toast.makeText(getApplicationContext(), "Edytowano dane!", Toast.LENGTH_LONG).show();
                                    intent2.putExtra("id_zalogowanego",controlerUzytkownik.getId().toString());
                                    finish();
                                    startActivity(intent2);
                                }

                            } catch (JSONException e) {
                                Log.e("blad1",e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("blad2",error.toString());
                            if (error instanceof NoConnectionError) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(EdytujDaneImieNazwisko.this);

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
                            return edytujParams;
                        }
                    };
                        updateQueue.add(edytuj);
                }else {
                    Toast.makeText(getApplicationContext(), "Wypełnij odpowiednie pola!", Toast.LENGTH_LONG).show();
                }
            }
        });

        bWroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(),DaneOsoboweActivity.class);
                intent2.putExtra("id_zalogowanego",controlerUzytkownik.getId().toString());
                finish();
                startActivity(intent2);
            }
        });

    }
}
