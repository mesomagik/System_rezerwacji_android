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
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminDodajDzienActivity extends AppCompatActivity {

    private ListView lvDzien;
    private Button bWroc;
    private Button bDodaj;
    private Spinner spPoczatek;
    private Spinner spKoniec;
    private RequestQueue pobierzDniQueue;
    private RequestQueue dodajDzienQueue;
    private String pobierzDniUrl = "http://mesomagik.ugu.pl/praca_inzynierska/adminWolneDni.php"; //10.0.2.2
    private String dodajDzienUrl = "http://mesomagik.ugu.pl/praca_inzynierska/adminInsertDzien.php";
    private ArrayList listaDni;
    private ArrayList<ControlerGodzina> listaGodzin;
    private String wybranyDzien;
    private ControlerGodzina wybranyPoczatek;
    private  ControlerGodzina wybranyKoniec;
    private  String id_zalogowanego;
    private ProgressDialog progressDialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent_w = new Intent(getApplicationContext(),AdminPanelActivity.class);
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


        setContentView(R.layout.activity_admin_dodaj_dzien);

        Intent intent = getIntent();
        id_zalogowanego = intent.getStringExtra("id_zalogowanego");

        lvDzien = (ListView) findViewById(R.id.lvDzien);
        bWroc = (Button) findViewById(R.id.bWroc);
        bDodaj = (Button) findViewById(R.id.bDodaj);
        spKoniec = (Spinner) findViewById(R.id.spKoniec);
        spPoczatek = (Spinner) findViewById(R.id.spPoczatek);
        pobierzDniQueue = Volley.newRequestQueue(getApplicationContext());
        dodajDzienQueue = Volley.newRequestQueue(getApplicationContext());
        listaDni = new ArrayList();
        listaGodzin = new ArrayList();

        wybranyDzien = "";
        spPoczatek.setEnabled(false);
        spKoniec.setEnabled(false);
        bDodaj.setEnabled(false);

        final HashMap<String,String> params = new HashMap<>();

        progressDialog = new ProgressDialog(AdminDodajDzienActivity.this);
        progressDialog.setMessage("Pobieranie danych...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        CustomRequest pobierzDni = new CustomRequest(pobierzDniUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    progressDialog.dismiss();
                    JSONArray dni = response.getJSONArray("dni");
                    for(int i=0;i<dni.length();i++){
                        JSONObject dzien = dni.getJSONObject(i);
                        listaDni.add(dzien.getString("data"));
                    }

                    JSONArray godziny = response.getJSONArray("godziny");
                    for(int i=0;i<godziny.length();i++){
                        JSONObject godzina = godziny.getJSONObject(i);
                        ControlerGodzina controlerGodzina = new ControlerGodzina(
                                godzina.getInt("id_godzina"),
                                godzina.getString("godzina")
                        );
                        listaGodzin.add(controlerGodzina);
                    }

                    final DzienAdapter dzienAdapter = new DzienAdapter();
                    lvDzien.setAdapter(dzienAdapter);

                    lvDzien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            wybranyDzien = listaDni.get(position).toString();

                            ArrayAdapter spinnerAdapter = new ArrayAdapter(getApplicationContext(),R.layout.spinner_item, listaGodzin);
                            spPoczatek.setAdapter(spinnerAdapter);
                            spPoczatek.setEnabled(true);

                            spKoniec.setAdapter(spinnerAdapter);
                            spKoniec.setEnabled(true);
                            spKoniec.setSelection(listaGodzin.size()-1);
                            bDodaj.setEnabled(true);

                        }
                    });

                    bDodaj.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                           if(spKoniec.getSelectedItemId()<=spPoczatek.getSelectedItemId()){
                               Toast.makeText(AdminDodajDzienActivity.this, "Podano błędne godziny pracy!", Toast.LENGTH_SHORT).show();

                           }else{

                               final HashMap<String,String> paramsDodaj = new HashMap<>();
                               params.put("data",wybranyDzien);
                               params.put("dzien_id_godzina_pocz",String.valueOf(spPoczatek.getSelectedItemId()+1));
                               params.put("dzien_id_godzina_kon",String.valueOf(spKoniec.getSelectedItemId()+1));

                               progressDialog = new ProgressDialog(AdminDodajDzienActivity.this);
                               progressDialog.setMessage("Dodawanie dnia...");
                               progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                               progressDialog.show();
                               CustomRequest request = new CustomRequest(dodajDzienUrl, paramsDodaj, new Response.Listener<JSONObject>() {
                                   @Override
                                   public void onResponse(JSONObject response) {

                                       progressDialog.dismiss();
                                       Log.e("data",wybranyDzien);
                                       Log.e("dzien_id_godzina_pocz",String.valueOf(spPoczatek.getSelectedItemId()));
                                       Log.e("dzien_id_godzina_kon",String.valueOf(spKoniec.getSelectedItemId()));

                                       Toast.makeText(AdminDodajDzienActivity.this, "Dodano dzień " + wybranyDzien, Toast.LENGTH_LONG).show();
                                       Intent intent_w = new Intent(getApplicationContext(),AdminPanelActivity.class);
                                       intent_w.putExtra("id_zalogowanego",id_zalogowanego);
                                       finish();
                                       startActivity(intent_w);
                                   }
                               }, new Response.ErrorListener(){
                                   @Override
                                   public void onErrorResponse(VolleyError error) {
                                       Log.e("blad2",error.toString());
                                       progressDialog.dismiss();
                                       Log.e("blad1",error.toString());
                                       if (error instanceof NoConnectionError) {
                                           AlertDialog.Builder builder = new AlertDialog.Builder(AdminDodajDzienActivity.this);

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
                               dodajDzienQueue.add(request);
                           }
                        }
                    });

                }catch(JSONException e){

                    progressDialog.dismiss();
                    Log.e("blad1",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Log.e("blad1",error.toString());
                if (error instanceof NoConnectionError) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminDodajDzienActivity.this);

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
                return params;
            }

        };
        pobierzDniQueue.add(pobierzDni);

        bWroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_w = new Intent(getApplicationContext(),AdminPanelActivity.class);
                intent_w.putExtra("id_zalogowanego",id_zalogowanego);
                finish();
                startActivity(intent_w);
            }
        });
    }

    private class DzienAdapter extends BaseAdapter {

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
                LayoutInflater inflater = AdminDodajDzienActivity.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.lv_admin_dodaj_dzien, null);
                holder.godzina = (TextView) convertView.findViewById(R.id.wizyta);
                holder.dzien = (TextView)   convertView.findViewById(R.id.tekst);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;

            String[] dni_tygodnia = {"Pt","Sob","Ndz","Pon","Wt","Śr","Czw"};
            String data=listaDni.get(position).toString();

            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(data.split("-")[0]), Integer.valueOf(data.split("-")[1]),Integer.valueOf(data.split("-")[2])); //rok miesiac dzien

            int dzien_tygodnia = c.get(Calendar.DAY_OF_WEEK);
            Log.e("data:", String.valueOf(dzien_tygodnia));

            holder.godzina.setText(dni_tygodnia[dzien_tygodnia-1] );
            holder.dzien.setText(listaDni.get(position).toString());


            return convertView;
        }

        private class ViewHolder {
            TextView godzina;
            TextView dzien;
            int ref;
        }
    }
}
