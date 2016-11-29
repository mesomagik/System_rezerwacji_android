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
import android.widget.TextView;
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

public class EdytujDaneEmail extends AppCompatActivity {

    private Button bEdytuj;
    private Button bWroc;
    private EditText etEmail1;
    private EditText etEmail2;
    private RequestQueue edytujQueue;
    private TextView tvEmail;
    private String edytujUrl = "http://mesomagik.ugu.pl/praca_inzynierska/updateUserEmail.php";

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
        setContentView(R.layout.activity_edytuj_dane_email);

        edytujQueue = Volley.newRequestQueue(getApplicationContext());

        final Intent intent = getIntent();
        controlerUzytkownik = (ControlerUzytkownik) intent.getSerializableExtra("controlerUzytkownik");

        bEdytuj = (Button) findViewById(R.id.bEdytuj);
        bWroc = (Button) findViewById(R.id.bWroc);

        etEmail1 = (EditText) findViewById(R.id.etEmail1);
        etEmail2 = (EditText) findViewById(R.id.etEmail2);

        etEmail1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etEmail2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvEmail.setText(controlerUzytkownik.getEmail());

        bEdytuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etEmail1.length()>0 && etEmail2.length()>0 ){

                    if(etEmail1.getText().toString().equals(etEmail2.getText().toString()) && etEmail1.getText().toString().contains("@")) {
                        controlerUzytkownik.setEmail(etEmail1.getText().toString());

                        final HashMap<String, String> edytujParams = new HashMap<>();
                        edytujParams.put("id", controlerUzytkownik.getId().toString());
                        edytujParams.put("email", etEmail1.getText().toString());


                        CustomRequest edytuj = new CustomRequest(edytujUrl, edytujParams, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Intent intent2 = new Intent(getApplicationContext(), DaneOsoboweActivity.class);
                                    if (response.getInt("success") == 1) {
                                        Log.e("success", response.getString("success"));
                                        Toast.makeText(getApplicationContext(), "Edytowano dane!", Toast.LENGTH_LONG).show();
                                        intent2.putExtra("id_zalogowanego", controlerUzytkownik.getId().toString());
                                        finish();
                                        startActivity(intent2);
                                    } else {
                                        Log.e("success", response.getString("success"));
                                        Toast.makeText(getApplicationContext(), "Wystąpił błąd w edycji danych!", Toast.LENGTH_LONG).show();
                                        intent2.putExtra("id_zalogowanego", controlerUzytkownik.getId().toString());
                                        finish();
                                        startActivity(intent2);
                                    }

                                } catch (JSONException e) {
                                    Log.e("blad1", e.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("blad2", error.toString());
                                if (error instanceof NoConnectionError) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(EdytujDaneEmail.this);

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
                        edytujQueue.add(edytuj);

                    }else{
                        Toast.makeText(getApplicationContext(), "Wpisany email jest niepoprawny!", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Wypełnij odpowiednio pola!", Toast.LENGTH_LONG).show();
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
