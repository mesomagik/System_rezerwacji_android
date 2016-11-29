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

public class AdminSzczegolyDzienActivity extends AppCompatActivity {

    private TextView tvDzien;
    private ListView lvWizyty;
    private Button bWroc;
    private RequestQueue wizytyQueue;
    private RequestQueue wizytaSzczegolyQueue;
    private String wizytyUrl = "http://mesomagik.ugu.pl/praca_inzynierska/showAdminDzienSzczegoly.php";
    private String wizytaSzczegolyUrl= "http://mesomagik.ugu.pl/praca_inzynierska/showAdminWizyta.php";
    private String id_zalogowanego;
    private ControlerDzien controlerDzien;
    private String[] dni_tygodnia = {"Pt","Sob","Ndz","Pon","Wt","Śr","Czw"};
    private ArrayList godzinyPracy;
    private ArrayList godzinyLista;
    private ArrayList<ArrayList> wizytaLista;
    private ArrayList wizytaSzczegoly;
    private ControlerWizyta controlerWizyta;
    private ProgressDialog progressDialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(),AdminWizytyActivity.class);
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

        setContentView(R.layout.activity_admin_szczegoly_dzien);

        Intent intent_id = getIntent();
        id_zalogowanego = intent_id.getStringExtra("id_zalogowanego");
        controlerDzien = (ControlerDzien) intent_id.getSerializableExtra("controlerDzien");
        godzinyPracy = intent_id.getStringArrayListExtra("godzinyPracy");
        godzinyLista = new ArrayList();
        wizytaLista = new ArrayList<ArrayList>();
        wizytaSzczegoly = new ArrayList<>();

        lvWizyty = (ListView) findViewById(R.id.lvWizyty);
        bWroc = (Button) findViewById(R.id.bWroc);
        wizytyQueue = Volley.newRequestQueue(getApplicationContext());
        wizytaSzczegolyQueue = Volley.newRequestQueue(getApplicationContext());
        tvDzien = (TextView) findViewById(R.id.tvDzien);

        String data=controlerDzien.getData();

        Calendar c = Calendar.getInstance();
        c.set(Integer.valueOf(data.split("-")[0]), Integer.valueOf(data.split("-")[1]),Integer.valueOf(data.split("-")[2])); //rok miesiac dzien
        int dzien_tygodnia = c.get(Calendar.DAY_OF_WEEK);
        tvDzien.setText(dni_tygodnia[dzien_tygodnia-1] + ' ' +controlerDzien.getData()+"   "+ godzinyPracy.get(0)+" - "+ godzinyPracy.get(1));

        final HashMap<String, String> params = new HashMap<>();
        params.put("id_dzien",controlerDzien.getId_dzien().toString());

        progressDialog = new ProgressDialog(AdminSzczegolyDzienActivity.this);
        progressDialog.setMessage("Pobieranie danych...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        CustomRequest dni = new CustomRequest(wizytyUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    JSONArray dzien = response.getJSONArray("dzien");
                    for (int i = 0; i < dzien.length(); i++) {
                        JSONObject dz = dzien.getJSONObject(i);
                        JSONArray godziny = dz.getJSONArray("godziny");
                        for(int j=0; j<godziny.length();j++){
                            JSONObject godzina = godziny.getJSONObject(j);
                            godzinyLista.add(godzina.getString("godzina").substring(0,5));
                            JSONArray wizytaA = godzina.getJSONArray("wizyta");
                            JSONObject wizyta = wizytaA.getJSONObject(0);
                            if(wizyta.getString("id_wizyta").equals("-1")){
                                wizytaSzczegoly = new ArrayList<>();
                                wizytaSzczegoly.add("-");
                                wizytaLista.add(wizytaSzczegoly);

                            }else if(wizyta.getString("wizyta_id_uzytkownik").equals("0")){
                                //TODO: niezarejestrowany
                                wizytaSzczegoly = new ArrayList<>();
                                wizytaSzczegoly.add(wizyta.getString("opis"));
                                wizytaLista.add(wizytaSzczegoly);

                            }else if(Integer.valueOf(wizyta.getString("wizyta_id_uzytkownik"))>0){
                                //TODO:zarejestrowany
                                wizytaSzczegoly = new ArrayList<>();
                                wizytaSzczegoly.add(wizyta.getString("imie_nazwisko"));
                                wizytaSzczegoly.add(wizyta.getString("id_wizyta"));
                                wizytaSzczegoly.add(wizyta.getString("wizyta_id_uzytkownik"));
                                wizytaSzczegoly.add(wizyta.getString("wizyta_id_data"));
                                wizytaSzczegoly.add(wizyta.getString("wizyta_id_godzina"));
                                wizytaSzczegoly.add(wizyta.getString("pierwsza"));
                                wizytaLista.add(wizytaSzczegoly);
                            }

                        }



                    }



                    WizytyAdapter adapter = new WizytyAdapter();
                    lvWizyty.setAdapter(adapter);


                    lvWizyty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            if (wizytaLista.get(position).size()>1) { //TODO: coś z tym jest nie tak, nie widzi że to ten element który trzeba
                                final HashMap<String,String> paramsWizyta = new HashMap<>();
                                paramsWizyta.put("id_wizyta",wizytaLista.get(position).get(1).toString());

                                CustomRequest request = new CustomRequest(wizytaSzczegolyUrl, paramsWizyta, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONArray wizytaL = response.getJSONArray("wizyta");
                                            for (int i = 0; i < wizytaL.length(); i++) {
                                                JSONObject wizyta  = wizytaL.getJSONObject(i);

                                                controlerWizyta = new ControlerWizyta(
                                                        wizyta.getInt("id_wizyta"),
                                                        wizyta.getInt("wizyta_id_uzytkownik"),
                                                        wizyta.getInt("wizyta_id_data"),
                                                        Boolean.valueOf(wizyta.getString("odbyta")),
                                                        wizyta.getInt("wizyta_id_godzina"),
                                                        Boolean.valueOf(wizyta.getString("pierwsza"))
                                                );

                                            }
                                            Intent intent = new Intent(getApplicationContext(),SzczegolyNadchodzacaActivity.class);
                                            intent.putExtra("wizyta",controlerWizyta.getObj());
                                            intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                                            intent.putExtra("powrotDo","adminSzczegolyDzien");
                                            intent.putExtra("controlerDzien",controlerDzien.getObj());
                                            intent.putExtra("godzinyPracy", godzinyPracy);
                                            finish();
                                            startActivity(intent);

                                        }catch (JSONException e){
                                           Log.e("blad1",e.toString());
                                        }
                                        WizytyAdapter adapter = new WizytyAdapter();
                                        lvWizyty.setAdapter(adapter);

                                    }
                                }, new Response.ErrorListener(){
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("blad1",error.toString());
                                        if (error instanceof NoConnectionError) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminSzczegolyDzienActivity.this);

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
                                        return paramsWizyta;
                                    }
                                };
                                wizytaSzczegolyQueue.add(request);
                            }


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
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminSzczegolyDzienActivity.this);

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

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        wizytyQueue.add(dni);

        bWroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminWizytyActivity.class);
                intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                finish();
                startActivity(intent);
            }
        });

    }
    private class WizytyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (wizytaLista != null && wizytaLista.size() != 0)
                return wizytaLista.size();
            return 0;

        }

        @Override
        public Object getItem(int position) {
            return wizytaLista.get(position);
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
                LayoutInflater inflater = AdminSzczegolyDzienActivity.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.lv_admin_szczegoly_dzien, null);
                holder.textView = (TextView) convertView.findViewById(R.id.wizyta);
                holder.tekst = (TextView) convertView.findViewById(R.id.tekst);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;

            holder.textView.setText(godzinyLista.get(position).toString());

            Log.e("pos", String.valueOf(position));
             if(position>=1 && wizytaLista.get(position-1).size()>1 && wizytaLista.get(position-1).get(5).equals("1")){
                holder.tekst.setText("^ pierwsza wizyta ^"); //wypisz ze poprzednia jest pierwsza
             }else if (wizytaLista.get(position).get(0).equals("-")) {
                 holder.tekst.setText(wizytaLista.get(position).get(0).toString()); //brak wizyty
             }else if (wizytaLista.get(position).size()==6){
                holder.tekst.setText(wizytaLista.get(position).get(0).toString()); //wypisz imie i nazwisko
             }else {
                holder.tekst.setText(wizytaLista.get(position).get(0).toString()); //wypisz opis niezarejestrowanego
             }
            return convertView;
        }

        private class ViewHolder {
            TextView textView;
            TextView tekst;
            int ref;
        }
    }
}
