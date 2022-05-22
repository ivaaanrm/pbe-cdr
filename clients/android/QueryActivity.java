package com.example.iwell;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QueryActivity extends AppCompatActivity {
    public Button b_logout, b_send;
    public EditText e_query;
    public String url;
    public TextView t_query_fail, t_username;
    public TableLayout table_layout;
    public Info info;
    public OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query); // mostrem la QueryActivity
        client = new OkHttpClient(); // creem un nou objecte OkHttpClient
        this.b_logout = findViewById(R.id.button_logout); // associem el botó logout a la variable
        this.b_send = findViewById(R.id.button_send); // associem el botó send a la variable
        this.table_layout = findViewById(R.id.table); // associem la taula de informació a la variable
        this.e_query = findViewById(R.id.query); // associem el valor de la query a la variable
        this.t_username = findViewById(R.id.text_username); // associem el el valor del username a la variable
        this.t_query_fail = findViewById(R.id.query_fail); // associem el missatge de wrong query a la variable
        this.t_query_fail.setVisibility(View.INVISIBLE); // fem invisible el missatge de wrong query
        this.url = getIntent().getStringExtra("url"); // obtenim el url del intent que ha llenát la QueryActivity
        this.t_username.setText(getIntent().getStringExtra("username")); // mostrem el username per pantalla
        this.info = new Info(table_layout, getApplicationContext());

        this.b_logout.setOnClickListener(v -> { // si es prem el botó logout tornem a la MainActivity
            Intent intent = new Intent(this, MainActivity.class); // creem una intent relacionada amb la MainActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // evitem que es pugui tornar enrere un cop llançem el intent
            startActivity(intent); // començem una nova activity amb el intent creat
            finish(); // finalitzem la QueryActivity
        });

        this.b_send.setOnClickListener(v -> send_query(client, url));
    }

    public void send_query(OkHttpClient client, String url) {
        String query = e_query.getText().toString().trim(); // agafem el string query sense espais
        if (query.equals("timetables") || query.equals("marks") || query.equals("tasks")) { // si la query introduida pertany a les dissenyades
            url = url + "/" + query; // creeem el url amb la query corresponent
            Request request = new Request.Builder().url(url).build(); // construim un nou request
            client.newCall(request).enqueue(new Callback() { // fem el request
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) { // si el request resulta fallit
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException { // si el request resulta correcte
                    try {
                        query(false); // cridem la funció query passant un false ja que la query es correcte
                        JSONArray json = new JSONArray(Objects.requireNonNull(response.body()).string()); // creem un JSONArray per tal d'obtenir la informació necessària
                        runOnUiThread(() -> table_layout.removeAllViews()); // esborrem la taula previa
                        query_table(json); // creem la nova taula amb la información nova
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            query(true); // cridem la funció query passant un true ja que la query no es correcte
        }
    }

    public void query_table(@NonNull JSONArray query) throws JSONException {
        ArrayList<String[]> data = new ArrayList<>(); // fem una llista de vectors de strings amb la informació
        ArrayList<String> headers = new ArrayList<>(); // fem una llista  de strings amb les capçeleres
        Iterator<String> iterator = null; // declarem un iterador
        try {
            iterator = query.getJSONObject(0).keys(); //  asociem el iterador amb el objecte json on les keys seran les capçeleres i els values la informació
        } catch (JSONException e) {
            e.printStackTrace();
        }
        while (Objects.requireNonNull(iterator).hasNext()) { // mira si la següent key no es null
            headers.add(iterator.next()); // afegeix la key a la llista de capçaleres
        }
        runOnUiThread(() -> this.info.put_header(headers.toArray(new String[0]))); // mostrem les capçeleres passant aquestes com un sol vector de strings
        for (int row = 0; row < query.length(); row++) {
            String[] value = new String[headers.toArray().length]; // creem un vector buit on anirà la informació relacionada amb les capçaleres
            for (int col = 0; col < headers.toArray().length; col++) {
                value[col] = query.getJSONObject(row).get(String.valueOf(headers.toArray()[col])).toString(); // afegeix la informació de cada una de les capçaleres al vector
            }
            data.add(value); // afegim a la llista de vectors de strings el vector omplert anteriorment
        }
        runOnUiThread(() -> {
            info.put_data(data); // afegim la informació a la taula
            info.color_line(Color.BLACK); // camviem el color de les línies de separació de la taula
            info.header_color(Color.BLACK); // posem color als strings de les capçaleres
            info.data_color(Color.BLACK, data.size()); // posem color a la informació que surt per la taula
            info.header_background(Color.rgb(210, 145, 136)); // camviem el color de fons de les cel·les
            info.data_background(Color.rgb(153, 85, 176), Color.rgb(45, 160, 23), data.size()); // camviem el color de fons de les cel·les
        });
    }

    public void query(Boolean fail) {
        if (fail) {
            QueryActivity.this.runOnUiThread(() -> t_query_fail.setVisibility(View.VISIBLE)); // fem visible el missatge de query errònia
        } else {
            QueryActivity.this.runOnUiThread(() -> t_query_fail.setVisibility(View.INVISIBLE)); // amaguem el missatge de query errònia
        }
    }
}
