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

public class KorespondencjaWybranieWizytyActivity extends AppCompatActivity {

    private Button bWroc;
    private ListView lvWizyty;
    private RequestQueue requestQueue;
    private String url="http://mesomagik.ugu.pl/praca_inzynierska/showWizytyDoKorespondencji.php";
    private ArrayList<String> listaDni;
    private ArrayList<String> listaGodzin;
    private ArrayList<String> listaWiadomosci;
    private ArrayList<ControlerWizyta> listaWizyt;
    private ArrayList<String>listaIdUser;
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
        setContentView(R.layout.activity_korespondencja_wybranie_wizyty);

        Intent intent_id = getIntent();
        id_zalogowanego = Integer.valueOf(intent_id.getStringExtra("id_zalogowanego"));


        requestQueue = Volley.newRequestQueue(getApplicationContext());

        listaWizyt = new ArrayList<>();
        listaDni = new ArrayList<>();
        listaIdUser = new ArrayList<>();
        listaGodzin = new ArrayList<>();
        listaWiadomosci = new ArrayList<>();
        lvWizyty = (ListView) findViewById(R.id.lvWizyty);
        bWroc = (Button) findViewById(R.id.bDodajWizyte);

        final HashMap<String,String> params = new HashMap<>();
        params.put("id_uzytkownik",id_zalogowanego.toString());//TODO dodać id użytkownika po zalogowaniu

        progressDialog = new ProgressDialog(KorespondencjaWybranieWizytyActivity.this);
        progressDialog.setMessage("Pobieranie danych...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        CustomRequest request = new CustomRequest(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    JSONArray wizytaL = response.getJSONArray("wizyta");
                    for (int i = 0; i < wizytaL.length(); i++) {
                        JSONObject wizyta  = wizytaL.getJSONObject(i);

                        Log.e("wizyta",wizyta.getString("id_wizyta"));
                        Log.e("wizyta",wizyta.getString("wizyta_id_uzytkownik"));
                        Log.e("wizyta",wizyta.getString("wizyta_id_data"));
                        Log.e("wizyta",wizyta.getString("odbyta"));
                        Log.e("wizyta",wizyta.getString("wizyta_id_godzina"));
                        Log.e("wizyta",wizyta.getString("pierwsza"));

                        ControlerWizyta controlerWizyta = new ControlerWizyta(
                                wizyta.getInt("id_wizyta"),
                                wizyta.getInt("wizyta_id_uzytkownik"),
                                wizyta.getInt("wizyta_id_data"),
                                Boolean.valueOf(wizyta.getString("odbyta")),
                                wizyta.getInt("wizyta_id_godzina"),
                                Boolean.valueOf(wizyta.getString("pierwsza"))
                        );
                        listaWizyt.add(controlerWizyta);
                        listaDni.add(wizyta.getString("dzien"));
                        listaGodzin.add(wizyta.getString("godzina"));
                        listaWiadomosci.add(wizyta.getString("tekst"));
                        listaIdUser.add( wizyta.getString("wiadomosc_id_user"));
                    }

                    lvWizyty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getApplicationContext(),WiadomosciKorespondencjiActivity.class);
                            intent.putExtra("wizyta",listaWizyt.get(position).getObj());
                            intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                            intent.putExtra("powrotDo","korespondencja");
                            finish();
                            startActivity(intent);
                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
                WizytyAdapter adapter = new WizytyAdapter();
                lvWizyty.setAdapter(adapter);

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                if (error instanceof NoConnectionError) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(KorespondencjaWybranieWizytyActivity.this);

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

    private class WizytyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (listaWizyt != null && listaWizyt.size() != 0)
                return listaWizyt.size();
            return 0;

        }

        @Override
        public Object getItem(int position) {
            return listaWizyt.get(position);
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
                LayoutInflater inflater = KorespondencjaWybranieWizytyActivity.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.lv_korespondencja_wybranie_wizyty, null);
                holder.textView = (TextView) convertView.findViewById(R.id.wizyta);
                holder.tekst = (TextView)   convertView.findViewById(R.id.tekst);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;

            String[] dni_tygodnia = {"Pt","Sob","Ndz","Pon","Wt","Śr","Czw"};
            String data=listaDni.get(position);

            Calendar c = Calendar.getInstance();
            c.set(Integer.valueOf(data.split("-")[0]), Integer.valueOf(data.split("-")[1]),Integer.valueOf(data.split("-")[2])); //rok miesiac dzien

            int dzien_tygodnia = c.get(Calendar.DAY_OF_WEEK);
            Log.e("data:", String.valueOf(dzien_tygodnia));

            holder.textView.setText(dni_tygodnia[dzien_tygodnia-1] +" "+ listaGodzin.get(position).substring(0,5) + "\n" + listaDni.get(position));

           if(listaIdUser.get(position).equals("1")){
               holder.tekst.setText("Administrator: "+listaWiadomosci.get(position));
           }else {
               holder.tekst.setText("Ty: "+listaWiadomosci.get(position));
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

