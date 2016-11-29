package com.example.bartek.praca_inzynierska;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Rejestracja2Activity extends AppCompatActivity {


    private EditText etPesel;
    private EditText etTelefon;
    private EditText etAdres;
    private Button bRejestruj;
    private Button bWroc;
    private String imie;
    private String nazwisko;
    private String email;
    private String haslo;
    private RequestQueue insertUserQueue;
    private String insertUserURL = "http://mesomagik.ugu.pl/praca_inzynierska/insertUzytkownik.php";

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            startActivity(new Intent(getApplicationContext(),Rejestracja1Activity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejestracja2);

        insertUserQueue = Volley.newRequestQueue(getApplicationContext());

        Intent intent_dane = getIntent();
        imie = intent_dane.getStringExtra("imie");
        nazwisko = intent_dane.getStringExtra("nazwisko");
        email = intent_dane.getStringExtra("email");
        haslo = intent_dane.getStringExtra("haslo");

        etPesel = (EditText)findViewById(R.id.etPesel);
        etTelefon = (EditText) findViewById(R.id.etTelefon);
        etAdres = (EditText) findViewById(R.id.etAdres);
        bRejestruj = (Button) findViewById(R.id.bRejestruj);
        bWroc = (Button) findViewById(R.id.bWroc);

        bWroc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(),Rejestracja1Activity.class));
            }
        });

        bRejestruj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etPesel.length()>0 && etTelefon.length()>0 && etAdres.length()>0 ){
                    PeselValidator peselValidator = new PeselValidator(etPesel.getText().toString());
                    if(peselValidator.isValid()) {
                        final HashMap<String, String> insertUserParams = new HashMap<String, String>();
                        insertUserParams.put("imie", imie);
                        insertUserParams.put("nazwisko", nazwisko);
                        insertUserParams.put("email", email);
                        insertUserParams.put("haslo", haslo);
                        insertUserParams.put("pesel", etPesel.getText().toString());
                        insertUserParams.put("adres", etAdres.getText().toString());
                        insertUserParams.put("telefon", etTelefon.getText().toString());

                        CustomRequest insertUserRequest = new CustomRequest(insertUserURL, insertUserParams, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    if (response.getInt("success") == 1) {
                                        Log.e("success", response.getString("success").toString());
                                        Toast.makeText(getApplicationContext(), "Pomyślnie zarejestrowano, oczekiwanie na aktywowanie konta przez administratora", Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), LogowanieActivity.class));
                                    } else {
                                        Log.e("success", response.getString("success").toString());
                                        Toast.makeText(getApplicationContext(), "wystąpił błąd, przepraszamy", Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), LogowanieActivity.class));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                return insertUserParams;
                            }
                        };
                        insertUserQueue.add(insertUserRequest);
                    }else {
                        Toast.makeText(getApplicationContext(),"Niepoprawny numer PESEL",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Wypełnij wszystkie dane",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private class PeselValidator {

        private byte PESEL[] = new byte[11];
        private boolean valid = false;

        public PeselValidator(String PESELNumber) {
            if (PESELNumber.length() != 11){
                valid = false;
            }
            else {
                for (int i = 0; i < 11; i++){
                    PESEL[i] = Byte.parseByte(PESELNumber.substring(i, i+1));
                }
                if (checkSum() && checkMonth() && checkDay()) {
                    valid = true;
                }
                else {
                    valid = false;
                }
            }
        }

        public boolean isValid() {
            return valid;
        }

        public int getBirthYear() {
            int year;
            int month;
            year = 10 * PESEL[0];
            year += PESEL[1];
            month = 10 * PESEL[2];
            month += PESEL[3];
            if (month > 80 && month < 93) {
                year += 1800;
            }
            else if (month > 0 && month < 13) {
                year += 1900;
            }
            else if (month > 20 && month < 33) {
                year += 2000;
            }
            else if (month > 40 && month < 53) {
                year += 2100;
            }
            else if (month > 60 && month < 73) {
                year += 2200;
            }
            return year;
        }

        public int getBirthMonth() {
            int month;
            month = 10 * PESEL[2];
            month += PESEL[3];
            if (month > 80 && month < 93) {
                month -= 80;
            }
            else if (month > 20 && month < 33) {
                month -= 20;
            }
            else if (month > 40 && month < 53) {
                month -= 40;
            }
            else if (month > 60 && month < 73) {
                month -= 60;
            }
            return month;
        }


        public int getBirthDay() {
            int day;
            day = 10 * PESEL[4];
            day += PESEL[5];
            return day;
        }

        public String getSex() {
            if (valid) {
                if (PESEL[9] % 2 == 1) {
                    return "Mezczyzna";
                }
                else {
                    return "Kobieta";
                }
            }
            else {
                return "---";
            }
        }

        private boolean checkSum() {
            int sum = 1 * PESEL[0] +
                    3 * PESEL[1] +
                    7 * PESEL[2] +
                    9 * PESEL[3] +
                    1 * PESEL[4] +
                    3 * PESEL[5] +
                    7 * PESEL[6] +
                    9 * PESEL[7] +
                    1 * PESEL[8] +
                    3 * PESEL[9];
            sum %= 10;
            sum = 10 - sum;
            sum %= 10;

            if (sum == PESEL[10]) {
                return true;
            }
            else {
                return false;
            }
        }

        private boolean checkMonth() {
            int month = getBirthMonth();
            int day = getBirthDay();
            if (month > 0 && month < 13) {
                return true;
            }
            else {
                return false;
            }
        }

        private boolean checkDay() {
            int year = getBirthYear();
            int month = getBirthMonth();
            int day = getBirthDay();
            if ((day >0 && day < 32) &&
                    (month == 1 || month == 3 || month == 5 ||
                            month == 7 || month == 8 || month == 10 ||
                            month == 12)) {
                return true;
            }
            else if ((day >0 && day < 31) &&
                    (month == 4 || month == 6 || month == 9 ||
                            month == 11)) {
                return true;
            }
            else if ((day >0 && day < 30 && leapYear(year)) ||
                    (day >0 && day < 29 && !leapYear(year))) {
                return true;
            }
            else {
                return false;
            }
        }

        private boolean leapYear(int year) {
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                return true;
            else
                return false;
        }
    }
}
