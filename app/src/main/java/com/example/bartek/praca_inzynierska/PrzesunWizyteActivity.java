package com.example.bartek.praca_inzynierska;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class PrzesunWizyteActivity extends AppCompatActivity {

    private ListView lvDzien;
    private Spinner spGodzina;
    private Button bWroc;
    private Button bPrzenies;
    private RequestQueue pobierzDaneQueue;
    private RequestQueue updateQueue;
    private String pobierzDaneUrl = "http://mesomagik.ugu.pl/praca_inzynierska/showPrzesunDane.php";
    private String updateUrl = "http://mesomagik.ugu.pl/praca_inzynierska/showPrzesunUpdate.php";
    private String id_zalogowanego;
    private ControlerWizyta controlerWizyta;
    private ArrayList<ArrayList> listaGodzin;
    private ArrayList<ControlerDzien> listaDni;
    private ArrayList<ControlerGodzina> listaGodzinControler;
    private ControlerDzien wybranyDzien;
    private ControlerGodzina wybranaGodzina;
    private String powrotDo;
    private ControlerDzien controlerDzienAdmin;
    private ArrayList godzinyPracy;
    private ProgressDialog progressDialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent_w = new Intent(getApplicationContext(),SzczegolyNadchodzacaActivity.class);
            intent_w.putExtra("id_zalogowanego",id_zalogowanego.toString());
            intent_w.putExtra("wizyta",controlerWizyta.getObj());
            intent_w.putExtra("powrotDo",powrotDo);
            intent_w.putExtra("controlerDzien",controlerDzienAdmin.getObj());
            intent_w.putExtra("godzinyPracy", godzinyPracy);
            finish();
            startActivity(intent_w);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_przesun_wizyte);

        listaGodzin = new ArrayList<>();
        listaDni = new ArrayList<>();
        listaGodzinControler = new ArrayList<>();

        lvDzien = (ListView) findViewById(R.id.lvDzien);
        spGodzina = (Spinner) findViewById(R.id.spGodzina);
        bWroc = (Button) findViewById(R.id.bWroc);
        bPrzenies = (Button) findViewById(R.id.bPrzenies);

        pobierzDaneQueue = Volley.newRequestQueue(getApplicationContext());
        updateQueue = Volley.newRequestQueue(getApplicationContext());

        Intent intent = getIntent();
        id_zalogowanego = intent.getStringExtra("id_zalogowanego");
        controlerWizyta = (ControlerWizyta) intent.getSerializableExtra("wizyta");
        powrotDo = intent.getStringExtra("powrotDo");
        if(intent.hasExtra("controlerDzien")) {
            controlerDzienAdmin = (ControlerDzien) intent.getSerializableExtra("controlerDzien");
            godzinyPracy = intent.getStringArrayListExtra("godzinyPracy");
        }else{
            controlerDzienAdmin = new ControlerDzien(1,"",1,1);
            godzinyPracy=new ArrayList();
        }

        spGodzina.setEnabled(false);
        spGodzina.setClickable(false);
        bPrzenies.setEnabled(false);

        final HashMap<String, String> paramsPobierzDane = new HashMap<>();
        paramsPobierzDane.put("id", id_zalogowanego.toString());

        progressDialog = new ProgressDialog(PrzesunWizyteActivity.this);
        progressDialog.setMessage("Pobieranie danych...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        CustomRequest pobierzDane = new CustomRequest(pobierzDaneUrl, paramsPobierzDane, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    JSONArray lista = response.getJSONArray("dzien");
                    if(lista.length()==0) {

                        Intent intent_w = new Intent(getApplicationContext(),SzczegolyNadchodzacaActivity.class);
                        intent_w.putExtra("id_zalogowanego",id_zalogowanego.toString());
                        intent_w.putExtra("wizyta",controlerWizyta.getObj());
                        intent_w.putExtra("powrotDo",powrotDo);
                        intent_w.putExtra("controlerDzien",controlerDzienAdmin.getObj());
                        intent_w.putExtra("godzinyPracy", godzinyPracy);
                        Toast.makeText(PrzesunWizyteActivity.this, "Brak wolnych terminów", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(intent_w);


                    }
                    for (int i = 0; i < lista.length(); i++) {
                        JSONObject dz = lista.getJSONObject(i);

                        ControlerDzien ctrlDzien = new ControlerDzien(
                                dz.getInt("id_dzien"),
                                dz.getString("data"),
                                dz.getInt("dzien_id_godzina_pocz"),
                                dz.getInt("dzien_id_godzina_kon")
                        );
                        Log.e("ctrlDzien",ctrlDzien.getData());
                        listaDni.add(ctrlDzien);
                        JSONArray godzLista = dz.getJSONArray("godziny");
                        listaGodzinControler = new ArrayList<>();
                        for (int j = 0; j < godzLista.length(); j++) {

                            JSONObject godz = godzLista.getJSONObject(j);
                            ControlerGodzina ctrlGodzina = new ControlerGodzina(
                                    godz.getInt("id_godzina"),
                                    godz.getString("godzina")
                            );
                            Log.e("ctrlGodzina",String.valueOf(i)+ " "+ctrlGodzina.getGodzina());
                            listaGodzinControler.add(ctrlGodzina);
                        }
                        listaGodzin.add(listaGodzinControler);




                    }

                    lvDzien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            final int positionD = position;
                            ArrayAdapter spinnerAdapter = new ArrayAdapter(getApplicationContext(),R.layout.spinner_item, listaGodzin.get(positionD));
                            spGodzina.setAdapter(spinnerAdapter);
                            spGodzina.setEnabled(true);
                            spGodzina.setClickable(true);


                            wybranyDzien = listaDni.get(positionD);
                            Log.e("dzien",wybranyDzien.getId_dzien().toString());

                            spGodzina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    wybranaGodzina = (ControlerGodzina)listaGodzin.get(positionD).get(position);
                                    Log.e("godzina",wybranaGodzina.getId_godzina().toString());
                                    bPrzenies.setEnabled(true);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    });

                    bWroc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent_w = new Intent(getApplicationContext(),SzczegolyNadchodzacaActivity.class);
                            intent_w.putExtra("id_zalogowanego",id_zalogowanego.toString());
                            intent_w.putExtra("wizyta",controlerWizyta.getObj());
                            intent_w.putExtra("powrotDo",powrotDo);
                            intent_w.putExtra("controlerDzien",controlerDzienAdmin.getObj());
                            intent_w.putExtra("godzinyPracy", godzinyPracy);
                            finish();
                            startActivity(intent_w);
                        }
                    });

                    bPrzenies.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final HashMap<String,String> updateParams = new HashMap<>();
                            updateParams.put("id_wizyta",controlerWizyta.getId_wizyta().toString());
                            updateParams.put("wizyta_id_data",wybranyDzien.getId_dzien().toString());
                            updateParams.put("wizyta_id_godzina",wybranaGodzina.getId_godzina().toString());
                            updateParams.put("pierwsza","0");


                            progressDialog = new ProgressDialog(PrzesunWizyteActivity.this);
                            progressDialog.setMessage("Przenoszenie wizyty...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                            CustomRequest request = new CustomRequest(updateUrl, updateParams, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progressDialog.dismiss();
                                    if(powrotDo.equals("adminSzczegolyDzien")) {

                                        Intent intent_w = new Intent(getApplicationContext(),AdminSzczegolyDzienActivity.class);
                                        intent_w.putExtra("id_zalogowanego",id_zalogowanego.toString());
                                        intent_w.putExtra("wizyta",controlerWizyta.getObj());
                                        intent_w.putExtra("powrotDo",powrotDo);
                                        intent_w.putExtra("controlerDzien",controlerDzienAdmin.getObj());
                                        Toast.makeText(PrzesunWizyteActivity.this, "Wizyta została przeniesiona", Toast.LENGTH_SHORT).show();
                                        intent_w.putExtra("godzinyPracy", godzinyPracy);
                                        finish();
                                        startActivity(intent_w);
                                    }else{
                                        Intent intent_w = new Intent(getApplicationContext(), TwojeWizytyAcitvity.class);
                                        intent_w.putExtra("id_zalogowanego", id_zalogowanego);
                                        Toast.makeText(PrzesunWizyteActivity.this, "Wizyta została przeniesiona", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(intent_w);
                                    }
                                }
                            }, new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("blad2",error.toString());
                                    progressDialog.dismiss();
                                    if (error instanceof NoConnectionError) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(PrzesunWizyteActivity.this);

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
                                    return updateParams;
                                }
                            };
                            updateQueue.add(request);

                        }
                    });



                } catch (JSONException e) {
                    Log.e("blad1",e.toString());
                    progressDialog.dismiss();
                }
                WizytyAdapter adapter = new WizytyAdapter();
                lvDzien.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("blad2",error.toString());
                progressDialog.dismiss();
                if (error instanceof NoConnectionError) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PrzesunWizyteActivity.this);

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
                return paramsPobierzDane;
            }
        };
        pobierzDaneQueue.add(pobierzDane);

    }




    private class WizytyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (listaDni != null && listaDni.size() != 0)
                return listaDni.size();
            return 0;

        }

        @Override
        public Object getItem(int position) {
            return listaDni.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = PrzesunWizyteActivity.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.lv_admin_szczegoly_dzien, null);
                holder.tekst = (TextView) convertView.findViewById(R.id.tekst);
                holder.wizyta = (TextView) convertView.findViewById(R.id.wizyta);



                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;

            String[] dni_tygodnia = {"Pt ", "Sob", "Ndz", "Pon", "Wt ", "Śr ", "Czw"};
            String data = listaDni.get(position).getData();

            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(data.split("-")[0]), Integer.valueOf(data.split("-")[1]), Integer.valueOf(data.split("-")[2])); //rok miesiac dzien

            int dzien_tygodnia = c.get(Calendar.DAY_OF_WEEK);

            holder.wizyta.setText(dni_tygodnia[dzien_tygodnia-1]);
            holder.tekst.setText( listaDni.get(position).getData() );
            return convertView;
        }

        private class ViewHolder {
            TextView tekst;
            TextView wizyta;
            int ref;
        }
    }


}
