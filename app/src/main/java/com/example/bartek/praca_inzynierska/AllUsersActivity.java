package com.example.bartek.praca_inzynierska;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AllUsersActivity extends AppCompatActivity {

    String url = "";
    ListAdapter adapter;
    private String getAllUsers = "http://mesomagik.ugu.pl/praca_inzynierska/showUsersCRUD.php";
    private RequestQueue requestQueue;

    private ListView lv1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        lv1 = (ListView) findViewById(R.id.lvDzien);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                getAllUsers, "", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final ArrayList<ControlerUzytkownik> userList = new ArrayList<ControlerUzytkownik>();
                    JSONArray users = response.getJSONArray("users");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        boolean active = Boolean.parseBoolean(user.getString("active")); //parsowanie z bazy active
                        ControlerUzytkownik controlerUzytkownik = new ControlerUzytkownik(
                                user.getInt("id_user"),
                                user.getString("email"),
                                user.getString("name"),
                                user.getString("surname"),
                                user.getString("adress"),
                                user.getInt("telefon"),
                                user.getString("password"),
                                active,
                                user.getString("pesel"));


                        userList.add(controlerUzytkownik);

                    }

                    final ArrayList viewList = new ArrayList();
                    for (int i = 0; i < userList.size(); i++) {
                        viewList.add(userList.get(i).getImie() + ' ' + userList.get(i).getNazwisko() );
                    }

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            AllUsersActivity.this,
                            android.R.layout.simple_list_item_1,
                            viewList);
                    lv1.setAdapter(arrayAdapter);


                    lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(getApplicationContext(),"Position= "+userList.get(position).getId().toString(),Toast.LENGTH_LONG).show();
                            //Intent intent = new Intent(AllUsersActivity.this, UpdateUserActivity.class);
                            //intent.putExtra("ControlerUzytkownik", userList.get(position).getObj());
                            //startActivity(intent);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error");
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

}
