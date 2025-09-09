package com.ramosuni.fallapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigurationActivity extends AppCompatActivity {

    private EditText etTiempoEspera, etNumeroEmergencia;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        etTiempoEspera = findViewById(R.id.etTiempoEspera);
        etNumeroEmergencia = findViewById(R.id.etNumeroEmergencia);
        btnGuardar = findViewById(R.id.btnGuardar);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // Cargar valores guardados si existen
        int tiempo = prefs.getInt("tiempo_espera", 10);
        String numero = prefs.getString("numero_emergencia", "");

        etTiempoEspera.setText(String.valueOf(tiempo));
        etNumeroEmergencia.setText(numero);

        btnGuardar.setOnClickListener(v -> {
            String tiempoStr = etTiempoEspera.getText().toString().trim();
            String numeroStr = etNumeroEmergencia.getText().toString().trim();

            if (tiempoStr.isEmpty() || numeroStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int tiempoGuardado = Integer.parseInt(tiempoStr);

            prefs.edit()
                    .putInt("tiempo_espera", tiempoGuardado)
                    .putString("numero_emergencia", numeroStr)
                    .apply();

            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();
            finish(); // cerrar pantalla
        });
    }
}
