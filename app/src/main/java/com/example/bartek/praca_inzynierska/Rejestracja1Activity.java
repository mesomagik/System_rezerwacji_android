package com.example.bartek.praca_inzynierska;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Rejestracja1Activity extends AppCompatActivity {

    private EditText etImie;
    private EditText etNazwisko;
    private EditText etEmail;
    private EditText etHaslo;
    private Button bPrzejdDalej;
    private Button bWroc;
    private RequestQueue emailQueue;
    private String emailURL= "http://mesomagik.ugu.pl/praca_inzynierska/showUsersEmail.php";
    private List<String> listaEmail;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            startActivity(new Intent(getApplicationContext(), LogowanieActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejestracja1);

        emailQueue = Volley.newRequestQueue(getApplicationContext());


        listaEmail = new ArrayList<>();

        etImie = (EditText) findViewById(R.id.etEmail1);
        etNazwisko = (EditText) findViewById(R.id.etEmail2);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etHaslo = (EditText) findViewById(R.id.etHaslo);
        bPrzejdDalej = (Button) findViewById(R.id.bDalej);
        bWroc = (Button) findViewById(R.id.bWroc);

        HashMap params = new HashMap();
        CustomRequest emailRequest = new CustomRequest(emailURL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray emails = response.getJSONArray("emails");
                    for (int i = 0; i < emails.length(); i++) {
                        JSONObject email = emails.getJSONObject(i);
                        listaEmail.add(email.getString("email"));
                        Log.e("email", listaEmail.get(i).toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        bWroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), LogowanieActivity.class));
            }
        });
        emailQueue.add(emailRequest);

        bPrzejdDalej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etImie.length() > 0 && etNazwisko.length() > 0 && etEmail.length() > 0 && etHaslo.length() > 0) {
                    if(etEmail.getText().toString().contains("@")) {
                        if (!listaEmail.contains(etEmail.getText().toString())) {
                            Intent intent_dalej = new Intent(getApplicationContext(), Rejestracja2Activity.class);
                            intent_dalej.putExtra("imie", etImie.getText().toString());
                            intent_dalej.putExtra("nazwisko", etNazwisko.getText().toString());
                            intent_dalej.putExtra("haslo", etHaslo.getText().toString());
                            intent_dalej.putExtra("email", etEmail.getText().toString());
                            finish();
                            startActivity(intent_dalej);
                        } else {
                            Toast.makeText(getApplicationContext(), "Taki Email już istnieje", Toast.LENGTH_SHORT).show();

                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Niepoprawny adres Email", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Wypełnij wszystkie dane", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
