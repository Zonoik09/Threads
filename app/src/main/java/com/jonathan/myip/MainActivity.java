package com.jonathan.myip;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String error = "";
    String urlString = "https://google.es";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button myButton = findViewById(R.id.btn);

        // Crear el onClick -> OnClickListener
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Tarea de red en segundo plano
                        String resultado = getDataFromUrl(urlString);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> {
                            // Actualizar la UI
                            if (resultado != null) {
                                Log.i(TAG, "Response: " + resultado);
                                Toast.makeText(MainActivity.this, "Datos recibidos: " + resultado, Toast.LENGTH_LONG).show();
                            } else {
                                Log.e(TAG, "Error fetching data: " + error);
                                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });
    }

        private String getDataFromUrl(String Stringurl) {
            String result = null;
            int resCode;
            try {
                URL url = new URL(Stringurl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setAllowUserInteraction(false);
                urlConnection.setInstanceFollowRedirects(true);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                resCode = urlConnection.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    result = sb.toString();
                } else {
                    error = "Error code: " + resCode;
                }
            } catch (Exception e) {
                error = e.getMessage();
                e.printStackTrace();
            }
            return result;
        }

}
