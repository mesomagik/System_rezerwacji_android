package com.example.bartek.praca_inzynierska;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WiadomosciKorespondencjiActivity extends AppCompatActivity {

    private Button bWroc;
    private EditText etWiadomosc;
    private Button bDodajWiadomosc;
    private ListView lvWiadomosci;
    private RequestQueue requestPobierzWiadomosci;
    private RequestQueue requestDodajWiadomosc;
    private List<ControlerWiadomosc> listaWiadomosci;
    private String urlPobierzWiadomosc = "http://mesomagik.ugu.pl/praca_inzynierska/showWiadomosciKorespondencji.php"; //10.0.2.2
    private String urlDodajWiadomosc = "http://mesomagik.ugu.pl/praca_inzynierska/insertWiadomosc.php";
    private ControlerWizyta wizyta;
    private Integer id_uzytkownik;
    private String powrotDo;
    private ArrayList wizytaSzczegoly;
    private ControlerDzien controlerDzien;
    private String powrotDoWczesniej;
    private ArrayList godzinyPracy;
    private ProgressDialog progressDialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(powrotDo.equals("korespondencja")){
                Intent intent_w = new Intent(getApplicationContext(),KorespondencjaWybranieWizytyActivity.class);
                intent_w.putExtra("id_zalogowanego",id_uzytkownik.toString());
                finish();
                startActivity(intent_w);
            }else if(powrotDo.equals("szczegolyNadchodzaca")){
                Intent intent_w = new Intent(getApplicationContext(),SzczegolyNadchodzacaActivity.class);
                intent_w.putExtra("id_zalogowanego",id_uzytkownik.toString());
                intent_w.putExtra("wizyta",wizyta.getObj());
                intent_w.putExtra("powrotDo",powrotDoWczesniej);
                intent_w.putExtra("controlerDzien",controlerDzien.getObj());
                intent_w.putExtra("godzinyPracy", godzinyPracy);
                finish();
                startActivity(intent_w);
            }else if(powrotDo.equals("szczegolyOdbyta")){
                Intent intent_w = new Intent(getApplicationContext(),SzczegolyOdbytaActivity.class);
                intent_w.putExtra("id_zalogowanego",id_uzytkownik.toString());
                intent_w.putExtra("wizyta",wizyta.getObj());
                finish();
                startActivity(intent_w);
            }else if(powrotDo.equals("korespondencjaAdmin")){
                Intent intent_w = new Intent(getApplicationContext(),AdminWybranieKorespondencji.class);
                intent_w.putExtra("id_zalogowanego",id_uzytkownik.toString());
                finish();
                startActivity(intent_w);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiadomosci_korespondencji);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        controlerDzien=new ControlerDzien(1,"1",0,0);
        powrotDoWczesniej = "";
        godzinyPracy=new ArrayList();

        Intent intent = getIntent();
        wizyta = (ControlerWizyta) intent.getSerializableExtra("wizyta");
        id_uzytkownik = Integer.valueOf(intent.getStringExtra("id_zalogowanego"));
        powrotDo = intent.getStringExtra("powrotDo");

        if(intent.hasExtra("controlerDzien")){
            powrotDoWczesniej = intent.getStringExtra("powrotDoWczesniej");
            controlerDzien = (ControlerDzien) intent.getSerializableExtra("controlerDzien");
            godzinyPracy = intent.getStringArrayListExtra("godzinyPracy");
        }


        bWroc = (Button) findViewById(R.id.bDodajWizyte);
        bDodajWiadomosc = (Button) findViewById(R.id.bDodajWiadomosc);
        lvWiadomosci = (ListView) findViewById(R.id.lvWiadomosci);
        etWiadomosc = (EditText) findViewById(R.id.etWiadomosc);


        requestPobierzWiadomosci = Volley.newRequestQueue(getApplicationContext());
        requestDodajWiadomosc = Volley.newRequestQueue(getApplicationContext());

        listaWiadomosci = new ArrayList<>();

        final HashMap<String,String> paramsPobierzWiadomosc = new HashMap<>();
        paramsPobierzWiadomosc.put("id_wizyta",wizyta.getId_wizyta().toString());

        progressDialog = new ProgressDialog(WiadomosciKorespondencjiActivity.this);
        progressDialog.setMessage("Pobieranie danych...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        CustomRequest pobierzWiadomosci = new CustomRequest(urlPobierzWiadomosc, paramsPobierzWiadomosc, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    JSONArray wiadomosci = response.getJSONArray("wiadomosc");
                    for (int i = 0; i < wiadomosci.length(); i++) {
                        JSONObject w = wiadomosci.getJSONObject(i);
                        ControlerWiadomosc ctrlWiadomosc = new ControlerWiadomosc(
                                w.getInt("id_wiadomosc"),
                                w.getInt("wiadomosc_id_wizyta"),
                                w.getInt("wiadomosc_id_user"),
                                w.getString("tekst")

                        );
                        Log.e("t wiad",w.getString("tekst"));
                        listaWiadomosci.add(ctrlWiadomosc);
                    }
                    WiadomosciAdapter adapter = new WiadomosciAdapter();
                    lvWiadomosci.setAdapter(adapter);
                }catch(JSONException e){
                    e.printStackTrace();
                }

                bDodajWiadomosc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(etWiadomosc.getText().length()>0){
                            final HashMap<String,String> paramsWiadomosc = new HashMap<String, String>();
                            paramsWiadomosc.put("id_wizyta",wizyta.getId_wizyta().toString());
                            paramsWiadomosc.put("id_uzytkownik", id_uzytkownik.toString());
                            paramsWiadomosc.put("tekst",etWiadomosc.getText().toString());
                            Log.e("wiad",etWiadomosc.getText().toString());

                            CustomRequest dodajWiadomosc = new CustomRequest(urlDodajWiadomosc, paramsWiadomosc, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    if (error instanceof NoConnectionError) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(WiadomosciKorespondencjiActivity.this);

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
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    return paramsWiadomosc;
                                }
                            };
                            requestDodajWiadomosc.add(dodajWiadomosc);
                            Intent intent1 = new Intent(getApplicationContext(),WiadomosciKorespondencjiActivity.class);
                            intent1.putExtra("wizyta",wizyta.getObj());
                            intent1.putExtra("id_zalogowanego",id_uzytkownik.toString());
                            intent1.putExtra("powrotDo",powrotDo);
                            intent1.putExtra("powrotDoWczesniej",powrotDoWczesniej);
                            intent1.putExtra("controlerDzien", controlerDzien.getObj());
                            intent1.putStringArrayListExtra("godzinyPracy", godzinyPracy);
                            finish();
                            startActivity(intent1);
                        }

                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                if (error instanceof NoConnectionError) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WiadomosciKorespondencjiActivity.this);

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
                return paramsPobierzWiadomosc;
            }
        };
        requestPobierzWiadomosci.add(pobierzWiadomosci);

        bWroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(powrotDo.equals("korespondencja")){
                    Intent intent_w = new Intent(getApplicationContext(),KorespondencjaWybranieWizytyActivity.class);
                    intent_w.putExtra("id_zalogowanego",id_uzytkownik.toString());
                    finish();
                    startActivity(intent_w);
                }else if(powrotDo.equals("szczegolyNadchodzaca")){
                    Intent intent_w = new Intent(getApplicationContext(),SzczegolyNadchodzacaActivity.class);
                    intent_w.putExtra("id_zalogowanego",id_uzytkownik.toString());
                    intent_w.putExtra("wizyta",wizyta.getObj());
                    intent_w.putExtra("powrotDo",powrotDoWczesniej);
                    intent_w.putExtra("controlerDzien",controlerDzien.getObj());
                    intent_w.putExtra("godzinyPracy", godzinyPracy);
                    finish();
                    startActivity(intent_w);
                }else if(powrotDo.equals("szczegolyOdbyta")){
                    Intent intent_w = new Intent(getApplicationContext(),SzczegolyOdbytaActivity.class);
                    intent_w.putExtra("id_zalogowanego",id_uzytkownik.toString());
                    intent_w.putExtra("wizyta",wizyta.getObj());
                    finish();
                    startActivity(intent_w);
                }else if(powrotDo.equals("korespondencjaAdmin")){
                    Intent intent_w = new Intent(getApplicationContext(),AdminWybranieKorespondencji.class);
                    intent_w.putExtra("id_zalogowanego",id_uzytkownik.toString());
                    finish();
                    startActivity(intent_w);
                }

            }
        });
    }
    private class WiadomosciAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (listaWiadomosci != null && listaWiadomosci.size() != 0)
                return listaWiadomosci.size();
            return 0;

        }

        @Override
        public Object getItem(int position) {
            return listaWiadomosci.get(position);
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
                LayoutInflater inflater = WiadomosciKorespondencjiActivity.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.lv_pojedyncza_wiadomosc, null);
                holder.textView = (TextView) convertView.findViewById(R.id.wizyta);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;


            //TODO dodać kolorowanie wiadomości
            if(listaWiadomosci.get(position).getUser_id_user().equals(1)){
                holder.textView.setText("\t"+listaWiadomosci.get(position).getTekst());
                holder.textView.setBackgroundColor(Color.parseColor("#d1f3ff"));
            }else{
                holder.textView.setText(listaWiadomosci.get(position).getTekst());
                holder.textView.setBackgroundColor(Color.parseColor("#ffffff"));
            }



            return convertView;
        }

        private class ViewHolder {
            TextView textView;
            int ref;
        }
    }
}
