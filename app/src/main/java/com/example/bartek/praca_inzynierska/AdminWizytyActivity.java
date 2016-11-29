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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminWizytyActivity extends AppCompatActivity {

    private ListView lvDniPracy;
    private Button bWroc;
    private RequestQueue dniQueue;
    private ArrayList<ControlerDzien> listaDni;
    private ArrayList<String> godzinyPracyDnia;
    private ArrayList<ArrayList> godzinyPracy;
    private String dniUrl = "http://mesomagik.ugu.pl/praca_inzynierska/showAdminDniPracy.php";
    private String wizytaUrl ="";
    private String[] dni_tygodnia = {"Pt","Sob","Ndz","Pon","Wt","Śr","Czw"};
    private Integer id_zalogowanego;
    private ProgressDialog progressDialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(),AdminPanelActivity.class);
            intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
            finish();
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizyty);

        Intent intent_id = getIntent();
        id_zalogowanego = Integer.valueOf(intent_id.getStringExtra("id_zalogowanego"));

        lvDniPracy = (ListView) findViewById(R.id.lvDniPracy);
        bWroc = (Button) findViewById(R.id.bWroc);
        dniQueue = Volley.newRequestQueue(getApplicationContext());
        listaDni = new ArrayList<>();
        godzinyPracy = new ArrayList<>();
        godzinyPracyDnia = new ArrayList<>();

        final HashMap<String, String> params = new HashMap<>();

        progressDialog = new ProgressDialog(AdminWizytyActivity.this);
        progressDialog.setMessage("Pobieranie danych...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        CustomRequest dni = new CustomRequest(dniUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    final ArrayList<ControlerDzien> arrayDzien = new ArrayList<ControlerDzien>();
                    JSONArray dzien = response.getJSONArray("dzien");
                    for (int i = 0; i < dzien.length(); i++) {
                        JSONObject dz = dzien.getJSONObject(i);
                        ControlerDzien ctrlDzien = new ControlerDzien(
                                dz.getInt("id_dzien"),
                                dz.getString("data"),
                                dz.getInt("dzien_id_godzina_pocz"),
                                dz.getInt("dzien_id_godzina_kon")
                        );
                        arrayDzien.add(ctrlDzien);
                        godzinyPracyDnia = new ArrayList<>();
                        godzinyPracyDnia.add(dz.getString("poczatek").substring(0,5));
                        godzinyPracyDnia.add(dz.getString("koniec").substring(0,5));
                        godzinyPracy.add(godzinyPracyDnia);

                    }

                    final ArrayList viewList = new ArrayList();
                    for (int j = 0; j < arrayDzien.size(); j++) {

                        String data=arrayDzien.get(j).getData();

                        Calendar c = Calendar.getInstance();
                        c.set(Integer.valueOf(data.split("-")[0]), Integer.valueOf(data.split("-")[1]),Integer.valueOf(data.split("-")[2])); //rok miesiac dzien



                        int dzien_tygodnia = c.get(Calendar.DAY_OF_WEEK);
                        Log.e("data:", String.valueOf(dzien_tygodnia));

                        viewList.add(dni_tygodnia[dzien_tygodnia-1] + ' ' +arrayDzien.get(j).getData()+"   "+ godzinyPracy.get(j).get(0)+" - "+ godzinyPracy.get(j).get(1));
                    }

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            AdminWizytyActivity.this,
                            android.R.layout.simple_list_item_1,
                            viewList);
                    lvDniPracy.setAdapter(arrayAdapter);


                    lvDniPracy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            //Toast.makeText(getApplicationContext(), "Position= " + df.parse(arrayDzien.get(position).getData().toString()), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), AdminSzczegolyDzienActivity.class);
                            intent.putExtra("controlerDzien", arrayDzien.get(position).getObj());
                            intent.putExtra("godzinyPracy",godzinyPracy.get(position));
                            intent.putExtra("id_zalogowanego", id_zalogowanego.toString());

                            finish();
                            startActivity(intent);


                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                if (error instanceof NoConnectionError) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminWizytyActivity.this);

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

        );

        dniQueue.add(dni);

        bWroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminPanelActivity.class);
                intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent);
            }
        });

    }
}
