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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PrzypomnienieHaslaActivity extends AppCompatActivity {

    private Button bWroc;
    private Button bWyslij;
    private EditText etEmail;
    private RequestQueue emailQueue;
    private String emailUrl= "http://mesomagik.ugu.pl/praca_inzynierska/przypomnienieHasla.php";
    private ProgressDialog progressDialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent2 = new Intent(getApplicationContext(),LogowanieActivity.class);
            finish();
            startActivity(intent2);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_przypomnienie_hasla);

        emailQueue = Volley.newRequestQueue(getApplicationContext());
        bWroc = (Button) findViewById(R.id.bWroc);
        bWyslij = (Button) findViewById(R.id.bWyslij);
        etEmail = (EditText) findViewById(R.id.etEmail);

        bWroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LogowanieActivity.class);
                finish();
                startActivity(intent);
            }
        });

        bWyslij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etEmail.getText().length()>3 && etEmail.getText().toString().contains("@")){

                    final HashMap<String,String> params = new HashMap<>();
                    params.put("email",etEmail.getText().toString());

                    progressDialog = new ProgressDialog(PrzypomnienieHaslaActivity.this);
                    progressDialog.setMessage("Wysyłanie");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();

                    CustomRequest email = new CustomRequest(emailUrl, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                progressDialog.dismiss();
                                Log.e("success",String.valueOf(response.getInt("success")));
                                if(response.getString("success").compareTo("1")==0){
                                    Toast.makeText(PrzypomnienieHaslaActivity.this, "Wysłano wiadomość na podany adres", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(),LogowanieActivity.class));
                                }else{
                                    Toast.makeText(PrzypomnienieHaslaActivity.this, "Brak podanego adresu", Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException error){

                                Log.e("blad",error.toString());
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("volley_error",error.toString());
                            progressDialog.dismiss();
                            if (error instanceof NoConnectionError) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PrzypomnienieHaslaActivity.this);

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
                                        startActivity(new Intent(getApplicationContext(), LogowanieActivity.class));
                                    }
                                });
                                builder.show();
                            }
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            return params;
                        }
                    };
                    email.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    emailQueue.add(email);
                }else{
                    Toast.makeText(PrzypomnienieHaslaActivity.this, "Niepoprawnie podany adres e-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
