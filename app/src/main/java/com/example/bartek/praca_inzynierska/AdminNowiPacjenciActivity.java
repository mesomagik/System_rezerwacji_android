package com.example.bartek.praca_inzynierska;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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

public class AdminNowiPacjenciActivity extends AppCompatActivity {

    private ListView lvNowiPacjenci;
    private Button bWroc;
    private RequestQueue listaPacjentowQueue;
    private RequestQueue aktywujQueue;
    private String listaPacjentowUrl = "http://mesomagik.ugu.pl/praca_inzynierska/adminNowiPacjenci.php";
    private String aktywujUrl = "http://mesomagik.ugu.pl/praca_inzynierska/adminNowiPacjenciAktywuj.php";
    private ArrayList<ControlerUzytkownik> listaUzytkownikow;
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

        setContentView(R.layout.activity_admin_nowi_pacjenci);

        lvNowiPacjenci = (ListView) findViewById(R.id.lvNowiPacjenci);
        bWroc = (Button) findViewById(R.id.bWroc);
        listaPacjentowQueue = Volley.newRequestQueue(getApplicationContext());
        aktywujQueue = Volley.newRequestQueue(getApplicationContext());

        Intent intent_id = getIntent();
        id_zalogowanego = Integer.valueOf(intent_id.getStringExtra("id_zalogowanego"));

        listaUzytkownikow = new ArrayList<>();

        final HashMap<String,String> params = new HashMap<>();

        progressDialog = new ProgressDialog(AdminNowiPacjenciActivity.this);
        progressDialog.setMessage("Pobieranie danych...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        CustomRequest request = new CustomRequest(listaPacjentowUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    JSONArray userList = response.getJSONArray("uzytkownik");
                    for (int i = 0; i < userList.length(); i++) {
                        JSONObject user  = userList.getJSONObject(i);

                        boolean active = Boolean.parseBoolean(user.getString("aktywne")); //parsowanie z bazy czy aktywne
                        ControlerUzytkownik controlerUzytkownik = new ControlerUzytkownik(
                                user.getInt("id"),
                                user.getString("email"),
                                user.getString("imie"),
                                user.getString("nazwisko"),
                                user.getString("adres"),
                                user.getInt("telefon"),
                                user.getString("haslo"),
                                active,
                                user.getString("pesel"));
                        listaUzytkownikow.add(controlerUzytkownik);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
                WizytyAdapter adapter = new WizytyAdapter();
                lvNowiPacjenci.setAdapter(adapter);

                lvNowiPacjenci.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        final HashMap<String,String> params = new HashMap<>();
                        params.put("id_uzytkownik",listaUzytkownikow.get(position).getId().toString());

                        CustomRequest requestAktywuj = new CustomRequest(aktywujUrl, params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressDialog.dismiss();
                            }
                        }, new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                if (error instanceof NoConnectionError) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminNowiPacjenciActivity.this);

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
                        aktywujQueue.add(requestAktywuj);
                        Intent intent = new Intent(getApplicationContext(),AdminNowiPacjenciActivity.class);
                        intent.putExtra("id_zalogowanego",id_zalogowanego.toString());
                        finish();
                        startActivity(intent);

                        return true;
                    }
                });

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("blad1",error.toString());
                if (error instanceof NoConnectionError) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminNowiPacjenciActivity.this);

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
        listaPacjentowQueue.add(request);

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


    private class WizytyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (listaUzytkownikow != null && listaUzytkownikow.size() != 0)
                return listaUzytkownikow.size();
            return 0;

        }

        @Override
        public Object getItem(int position) {
            return listaUzytkownikow.get(position);
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
                LayoutInflater inflater = AdminNowiPacjenciActivity.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.lv_nowi_pacjenci, null);
                holder.fontimie = (TextView) convertView.findViewById(R.id.fontimie);
                holder.fontemail = (TextView)   convertView.findViewById(R.id.fontemail);
                holder.fonttelefon = (TextView)   convertView.findViewById(R.id.fonttelefon);
                holder.fontadres = (TextView)   convertView.findViewById(R.id.fontadres);
                holder.imie = (TextView) convertView.findViewById(R.id.imie);
                holder.email = (TextView)   convertView.findViewById(R.id.email);
                holder.telefon = (TextView)   convertView.findViewById(R.id.telefon1);
                holder.adres = (TextView)   convertView.findViewById(R.id.adres);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = position;


            Typeface fontFamily = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

            holder.fontimie.setTypeface(fontFamily);
            holder.fontemail.setTypeface(fontFamily);
            holder.fonttelefon.setTypeface(fontFamily);
            holder.fontadres.setTypeface(fontFamily);

            holder.fontimie.setText(" \uF007 ");
            holder.fontemail.setText(" @ ");
            holder.fonttelefon.setText(" \uF10B ");
            holder.fontadres.setText(" \uF041 ");
            holder.imie.setText(listaUzytkownikow.get(position).getImie()+" "+listaUzytkownikow.get(position).getNazwisko());
            holder.email.setText(listaUzytkownikow.get(position).getEmail());
            holder.telefon.setText(listaUzytkownikow.get(position).getTelefon().toString());
            holder.adres.setText(listaUzytkownikow.get(position).getAdres());

            return convertView;
        }

        private class ViewHolder {
            TextView imie;
            TextView email;
            TextView telefon;
            TextView adres;

            TextView fontimie;
            TextView fontemail;
            TextView fonttelefon;
            TextView fontadres;

            int ref;
        }
    }
}
