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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TwojeWizytyAcitvity extends AppCompatActivity {

    private ListView lvNadchodzace;
    private ListView lvOdbyte;
    private Button bWroc;
    private RequestQueue requestQueue;
    private String url="http://mesomagik.ugu.pl/praca_inzynierska/showTwojeWizyty.php";
    private ArrayList<String> listaDni_nadchodzace;
    private ArrayList<String> listaGodzin_nadchodzace;
    private ArrayList<String> listaDni_odbyte;
    private ArrayList<String> listaGodzin_odbyte;
    private ArrayList<ControlerWizyta> listaWizyt_odbyte;
    private ArrayList<ControlerWizyta> listaWizyt_nadchodzace;
    private Integer id_zalogowanego;
    private ProgressDialog progressDialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent_w = new Intent(getApplicationContext(),UserPanelActivity.class);
            intent_w.putExtra("id_zalogowanego",id_zalogowanego.toString());
            finish();
            startActivity(intent_w);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twoje_wizyty_acitvity);

        Intent intent_id = getIntent();
        id_zalogowanego = Integer.valueOf(intent_id.getStringExtra("id_zalogowanego"));

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        listaWizyt_odbyte = new ArrayList<>();
        listaWizyt_nadchodzace = new ArrayList<>();
        listaDni_nadchodzace = new ArrayList<>();
        listaGodzin_nadchodzace = new ArrayList<>();
        listaDni_odbyte = new ArrayList<>();
        listaGodzin_odbyte = new ArrayList<>();
        lvNadchodzace = (ListView) findViewById(R.id.lvNadchodzace);
        lvOdbyte = (ListView) findViewById(R.id.lvOdbyte);
        bWroc = (Button) findViewById(R.id.bWroc);

        final HashMap<String,String> params = new HashMap<>();
        params.put("id_uzytkownik",id_zalogowanego.toString());

        progressDialog = new ProgressDialog(TwojeWizytyAcitvity.this);
        progressDialog.setMessage("Pobieranie danych...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        CustomRequest request = new CustomRequest(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    if( response.getString("success_nadchodzace").equals("1")) {
                        JSONArray wizytaL = response.getJSONArray("wizyty_nadchodzace");
                        for (int i = 0; i < wizytaL.length(); i++) {
                            JSONObject wizyta = wizytaL.getJSONObject(i);

                            Log.e("wizyta", wizyta.getString("id_wizyta"));
                            Log.e("wizyta", wizyta.getString("wizyta_id_uzytkownik"));
                            Log.e("wizyta", wizyta.getString("wizyta_id_data"));
                            Log.e("wizyta", wizyta.getString("odbyta"));
                            Log.e("wizyta", wizyta.getString("wizyta_id_godzina"));
                            Log.e("wizyta", wizyta.getString("pierwsza"));

                            ControlerWizyta controlerWizyta = new ControlerWizyta(
                                    wizyta.getInt("id_wizyta"),
                                    wizyta.getInt("wizyta_id_uzytkownik"),
                                    wizyta.getInt("wizyta_id_data"),
                                    Boolean.valueOf(wizyta.getString("odbyta")),
                                    wizyta.getInt("wizyta_id_godzina"),
                                    Boolean.valueOf(wizyta.getString("pierwsza"))
                            );

                            listaWizyt_nadchodzace.add(controlerWizyta);
                            listaDni_nadchodzace.add(wizyta.getString("dzien"));
                            listaGodzin_nadchodzace.add(wizyta.getString("godzina"));
                        }
                    } else {
                        // TODO: co jeśli nadchodzace = 0;
                    }

                    if( response.getString("success_odbyte").equals("1")) {
                        JSONArray wizytaL = response.getJSONArray("wizyty_odbyte");
                        for (int i = 0; i < wizytaL.length(); i++) {
                            JSONObject wizyta = wizytaL.getJSONObject(i);

                            Log.e("wizyta", wizyta.getString("id_wizyta"));
                            Log.e("wizyta", wizyta.getString("wizyta_id_uzytkownik"));
                            Log.e("wizyta", wizyta.getString("wizyta_id_data"));
                            Log.e("wizyta", wizyta.getString("odbyta"));
                            Log.e("wizyta", wizyta.getString("wizyta_id_godzina"));
                            Log.e("wizyta", wizyta.getString("pierwsza"));

                            ControlerWizyta controlerWizyta = new ControlerWizyta(
                                    wizyta.getInt("id_wizyta"),
                                    wizyta.getInt("wizyta_id_uzytkownik"),
                                    wizyta.getInt("wizyta_id_data"),
                                    Boolean.valueOf(wizyta.getString("odbyta")),
                                    wizyta.getInt("wizyta_id_godzina"),
                                    Boolean.valueOf(wizyta.getString("pierwsza"))
                            );

                            listaWizyt_odbyte.add(controlerWizyta);
                            listaDni_odbyte.add(wizyta.getString("dzien"));
                            listaGodzin_odbyte.add(wizyta.getString("godzina"));
                        }
                    } else {
                        // TODO: co jeśli odbyte = 0;
                    }

                    lvNadchodzace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getApplicationContext(),SzczegolyNadchodzacaActivity.class);
                            intent.putExtra("wizyta",listaWizyt_nadchodzace.get(position).getObj());
                            intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                            intent.putExtra("powrotDo","twojeWizyty");
                            finish();
                            startActivity(intent);
                        }
                    });

                    Wizyty2Adapter adapter = new Wizyty2Adapter();
                    lvNadchodzace.setAdapter(adapter);

                    lvOdbyte.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getApplicationContext(),SzczegolyOdbytaActivity.class); //TODO: zrobic szczegóły odbytej wizyty
                            intent.putExtra("wizyta",listaWizyt_odbyte.get(position).getObj());
                            intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                            finish();
                            startActivity(intent);
                        }
                    });
                }catch (JSONException e){
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
                Wizyty1Adapter adapter = new Wizyty1Adapter();
                lvOdbyte.setAdapter(adapter);

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof NoConnectionError) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TwojeWizytyAcitvity.this);

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
                final HashMap<String,String> putParams = new HashMap<>();
                putParams.put("id_uzytkownik",id_zalogowanego.toString());//TODO dodać id użytkownika po zalogowaniu
                return putParams;
            }
        };
        requestQueue.add(request);

        bWroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_w = new Intent(getApplicationContext(),UserPanelActivity.class);
                intent_w.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent_w);
            }
        });
    }

    private class Wizyty2Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (listaWizyt_nadchodzace != null && listaWizyt_nadchodzace.size() != 0)
                return listaWizyt_nadchodzace.size();
            return 0;

        }

        @Override
        public Object getItem(int position) {
            return listaWizyt_nadchodzace.get(position);
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
                LayoutInflater inflater = TwojeWizytyAcitvity.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.lv_admin_szczegoly_dzien, null);
                holder.tekst = (TextView) convertView.findViewById(R.id.tekst);
                holder.wizyta = (TextView) convertView.findViewById(R.id.wizyta);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;

            String[] dni_tygodnia = {"Pt","Sob","Ndz","Pon","Wt","Śr","Czw"};
            String data=listaDni_nadchodzace.get(position);

            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(data.split("-")[0]), Integer.valueOf(data.split("-")[1]),Integer.valueOf(data.split("-")[2])); //rok miesiac dzien

            int dzien_tygodnia = c.get(Calendar.DAY_OF_WEEK);
            Log.e("data:", String.valueOf(dzien_tygodnia));

            if(!listaDni_nadchodzace.isEmpty()) {
                holder.wizyta.setText(dni_tygodnia[dzien_tygodnia - 1]);
                holder.tekst.setText(listaDni_nadchodzace.get(position) + " " + listaGodzin_nadchodzace.get(position).substring(0, 5));

            }
            return convertView;
        }

        private class ViewHolder {
            TextView tekst;
            TextView wizyta;
            Integer ref;
        }
    }

    private class Wizyty1Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (listaWizyt_odbyte != null && listaWizyt_odbyte.size() != 0)
                return listaWizyt_odbyte.size();
            return 0;

        }

        @Override
        public Object getItem(int position) {
            return listaWizyt_odbyte.get(position);
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
                LayoutInflater inflater = TwojeWizytyAcitvity.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.lv_admin_szczegoly_dzien, null);
                holder.tekst = (TextView) convertView.findViewById(R.id.tekst);
                holder.wizyta = (TextView) convertView.findViewById(R.id.wizyta);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;

            String[] dni_tygodnia = {"Pt","Sob","Ndz","Pon","Wt","Śr","Czw"};
            String data=listaDni_odbyte.get(position);

            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(data.split("-")[0]), Integer.valueOf(data.split("-")[1]),Integer.valueOf(data.split("-")[2])); //rok miesiac dzien

            int dzien_tygodnia = c.get(Calendar.DAY_OF_WEEK);
            Log.e("data:", String.valueOf(dzien_tygodnia));

            if(!listaDni_odbyte.isEmpty()) {
                holder.wizyta.setText(dni_tygodnia[dzien_tygodnia - 1]);
                holder.tekst.setText(listaDni_odbyte.get(position) + " " + listaGodzin_odbyte.get(position).substring(0, 5));

            }
            return convertView;
        }

        private class ViewHolder {
            TextView tekst;
            TextView wizyta;
            int ref;
        }
    }
}
