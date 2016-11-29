package com.example.bartek.praca_inzynierska;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class UstalWizyteGodzinaNotkaActivity extends AppCompatActivity {

    private EditText etWiadomosc;
    private ListView lvGodzina;
    private RequestQueue requestQueue;
    private RequestQueue requestWyslij;
    private String urlPost="http://mesomagik.ugu.pl/praca_inzynierska/showgodzinawolneterminydzien.php";
    private ControlerGodzina wybranaGodzina;
    private Button bDodajWizyte;
    private String urlDodajWizyte = "http://mesomagik.ugu.pl/praca_inzynierska/insertWizyta.php";
    private String pierwsza;
    private Integer id_zalogowanego;
    private Button bWroc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ustal_wizyte_godzina_notka);

        requestWyslij = Volley.newRequestQueue(getApplicationContext());
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        wybranaGodzina = new ControlerGodzina(-1,"-1");
        lvGodzina = (ListView) findViewById(R.id.lvDzien);
        etWiadomosc = (EditText) findViewById(R.id.etWiadomosc);
        bWroc = (Button) findViewById(R.id.bWroc);
        bDodajWizyte = (Button) findViewById(R.id.bDodajWizyte);

        final Intent intent = getIntent();
        pierwsza = intent.getStringExtra("pierwsza");
        id_zalogowanego = Integer.valueOf(intent.getStringExtra("id_zalogowanego"));

        final ControlerDzien controlerDzien = (ControlerDzien) intent.getSerializableExtra("ControlerDzien");

        final Integer data=controlerDzien.getId_dzien();
        final HashMap<String,String> postParams = new HashMap<>();
        postParams.put("dzien", controlerDzien.getId_dzien().toString());
        postParams.put("id_user", id_zalogowanego.toString());


        Log.e("dzien", controlerDzien.getId_dzien().toString());


        bWroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_w = new Intent(getApplicationContext(),UstalWizyteActivity.class);
                intent_w.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent_w);
            }
        });

        CustomRequest requestDzien = new CustomRequest(urlPost,
                postParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                        try {
                            final ArrayList<ControlerGodzina> arrayGodzina = new ArrayList<ControlerGodzina>();
                            JSONArray godzina = response.getJSONArray("godzina");
                            for (int i = 0; i < godzina.length(); i++) {
                                JSONObject godz = godzina.getJSONObject(i);
                                ControlerGodzina ctrlGodzina = new ControlerGodzina(
                                        godz.getInt("id_godzina"),
                                        godz.getString("godzina")
                                );
                                Log.e("dzien id",ctrlGodzina.getId_godzina().toString());
                                Log.e("dzien godzina",ctrlGodzina.getGodzina().toString());
                                arrayGodzina.add(ctrlGodzina);
                            }

                        final ArrayList viewList = new ArrayList();
                        for (int j = 0; j < arrayGodzina.size(); j++) {
                            viewList.add(arrayGodzina.get(j).getGodzina());
                        }

                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                UstalWizyteGodzinaNotkaActivity.this,
                                android.R.layout.simple_list_item_1,
                                viewList);
                        lvGodzina.setAdapter(arrayAdapter);


                        lvGodzina.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    Toast.makeText(getApplicationContext(), "Wybrano godzinę: " + arrayGodzina.get(position).getGodzina(), Toast.LENGTH_LONG).show();
                                    wybranaGodzina = arrayGodzina.get(position).getObj();
                            }
                        });
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                            Log.e("json exception","");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("volley error","");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return postParams;
            }
        };
        requestQueue.add(requestDzien);



        bDodajWizyte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.e("wybrana godzina id", wybranaGodzina.getId_godzina().toString());
            //Log.e("wybrana godzina id", String.valueOf(wybranaGodzina.getId_godzina().toString().compareTo("-1") > 0));
                if(Boolean.valueOf(wybranaGodzina.getId_godzina().compareTo(new Integer(-1))>0)){
                    final HashMap<String,String> paramsDodajWizyte = new HashMap<>();
                    paramsDodajWizyte.put("wizyta_id_uzytkownik",id_zalogowanego.toString()); //TODO dodać id użytkownika zalogowanego, na razie jest 0 na sztywno
                    paramsDodajWizyte.put("wizyta_id_data", data.toString());
                    paramsDodajWizyte.put("wizyta_id_godzina", wybranaGodzina.getId_godzina().toString());
                    paramsDodajWizyte.put("tekst_wiadomosci", etWiadomosc.getText().toString());
                    paramsDodajWizyte.put("pierwsza",pierwsza);
                    CustomRequest dodajWizyte = new CustomRequest(urlDodajWizyte, paramsDodajWizyte, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("volley exception","");
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("wizyta_id_uzytkownik",id_zalogowanego.toString()); //TODO dodać id użytkownika zalogowanego, na razie jest 0 na sztywno
                            params.put("wizyta_id_data", data.toString());
                            params.put("wizyta_id_godzina", wybranaGodzina.getId_godzina().toString());
                            params.put("tekst_wiadomosci", etWiadomosc.getText().toString());
                            params.put("pierwsza",pierwsza);
                            return params;
                        }
                    };
                    requestWyslij.add(dodajWizyte);
                    Toast.makeText(getApplicationContext(),"Dodano wizytę!",Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(getApplicationContext(),UserPanelActivity.class);
                    intent1.putExtra("id_zalogowanego",id_zalogowanego.toString());
                    finish();
                    startActivity(intent1);
                }
            }
        });
        }
    }




