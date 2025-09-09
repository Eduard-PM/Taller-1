package com.ramosuni.fallapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ramosuni.fallapp.services.LinearAccelerometerService;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvAccelValues;
    private TextView tvAccelResultant;

    private boolean isActive = false;

    private final BroadcastReceiver accelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            float x = intent.getFloatExtra("x", 0f);
            float y = intent.getFloatExtra("y", 0f);
            float z = intent.getFloatExtra("z", 0f);
            double aRes = intent.getDoubleExtra("aRes", 0f);

            tvAccelValues.setText(
                    String.format(Locale.getDefault(),
                            getString(R.string.accel_format),
                            x, y, z)
            );
            tvAccelResultant.setText(
                    String.format(Locale.getDefault(),
                            getString(R.string.accel_res_format),
                            aRes)
            );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvAccelValues = findViewById(R.id.tvAccelValues);
        tvAccelResultant = findViewById(R.id.tvAccelResultant);
        SwitchCompat switchActivate = findViewById(R.id.switchActivate);
        Button btnHistorial = findViewById(R.id.btnHistorial);
        Button btnConfiguracion = findViewById(R.id.btnConfiguracion);
        ImageView ivAppIcon = findViewById(R.id.ivAppIcon);

        boolean savedState = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getBoolean("isActive", false);

        switchActivate.setChecked(savedState);

        if (savedState) {
            startService(new Intent(this, LinearAccelerometerService.class));
        }

        switchActivate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isActive = isChecked;

            // Guardar estado en SharedPreferences
            getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isActive", isActive)
                    .apply();

            if (isActive) {
                startService(new Intent(this, LinearAccelerometerService.class));
            } else {
                stopService(new Intent(this, LinearAccelerometerService.class));
                tvAccelValues.setText(R.string.accel_default);
                tvAccelResultant.setText(R.string.accel_res_default);
            }
        });

        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            startActivity(intent);
        });

        btnConfiguracion.setOnClickListener(v -> {
            // abrir configuración
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(accelReceiver, new IntentFilter("ACCELEROMETER_DATA"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(accelReceiver);
    }
}
