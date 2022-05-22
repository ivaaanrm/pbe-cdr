package com.example.iwell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public OkHttpClient client;
    public Button b_login;
    public EditText e_username, e_password, e_path;
    public TextView t_login_fail;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // mostrem la MainActivity
        client = new OkHttpClient(); // creem un nou objecte OkHttpClient
        this.b_login = findViewById(R.id.button_login); // associem el botó login a la variable
        this.e_path = findViewById(R.id.path); // associem el valor del path a la variable
        this.e_username = findViewById(R.id.username); // associem el valor del username a la variable
        this.e_password = findViewById(R.id.password); // associem el valor del password a la variable
        this.t_login_fail = findViewById(R.id.login_fail); // associem el missatge de login fallit a la variable
        t_login_fail.setVisibility(View.INVISIBLE); // associem el valor del password a la variable

        this.b_login.setOnClickListener(view -> {
            String host_port = e_path.getText().toString(); // associem el que hem escrit a la pestanya path a la variable
            String user = e_username.getText().toString(); // associem el que hem escrit a la pestanya username a la variable
            String uid = e_password.getText().toString(); // associem el que hem escrit a la pestanya password a la variable
            String url = "http://" + host_port + "/" + uid; // creem el url a partir de les dades agafades
            Request request = new Request.Builder().url(url).build(); // construim un nou request
            client.newCall(request).enqueue(new Callback() { // fem la request
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) { // si el request resulta fallit
                    login(user, url, true); // cridem la funció login passant un true ja que l'inici de sessió no s'ha pogut fer
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException { // si el request resulta correcte
                    try {
                        JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).string()); // creem un JSONObject per tal d'obtenir la informació necessària
                        if (json.getString("id").equals(uid) && json.getString("username").equals(user)) { // comprovem que el usuari esta registrat
                            login(user, url, false); // cridem la funció login passant un fale ja que l'inici de sessió s'ha pogut fer
                        } else {
                            login(user, url, true); // cridem la funció login passant un true ja que l'inici de sessió no s'ha pogut fer
                        }
                    } catch (JSONException e) {
                        login(user, url, true); // cridem la funció login passant un true ja que l'inici de sessió no s'ha pogut fer
                    }
                }
            });
        });
    }

    public void login(String username, String url, @NonNull Boolean fail) {
        if (fail) {
            MainActivity.this.runOnUiThread(() -> t_login_fail.setVisibility(View.VISIBLE)); // fem visible el missatge d'inici de sessió erroni
        } else {
            Intent intent = new Intent(this, QueryActivity.class); // creem unaintent relacionat amb la QueryActivity
            intent.putExtra("username", username); // afegim el username al intent
            intent.putExtra("url", url); // afegim el url al intent
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // evitem que es pugui tornar enrere un cop llançem el intent
            startActivity(intent); // començem una nova activity amb el intent creat
        }
    }
}