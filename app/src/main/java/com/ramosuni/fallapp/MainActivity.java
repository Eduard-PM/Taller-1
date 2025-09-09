package com.ramosuni.fallapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    private static final int REQUEST_SMS_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvAccelValues = findViewById(R.id.tvAccelValues);
        tvAccelResultant = findViewById(R.id.tvAccelResultant);
        SwitchCompat switchActivate = findViewById(R.id.switchActivate);
        Button btnHistorial = findViewById(R.id.btnHistorial);
        Button btnConfiguration = findViewById(R.id.btnConfiguracion);
        ImageView ivAppIcon = findViewById(R.id.ivAppIcon);

        boolean savedState = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getBoolean("isActive", false);

        switchActivate.setChecked(savedState);

        // Pedir permiso si no está concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SMS_PERMISSION);
        }

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

        btnConfiguration.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConfigurationActivity.class);
            startActivity(intent);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de SMS concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de SMS denegado. No se podrán enviar alertas", Toast.LENGTH_LONG).show();
            }
        }
    }
}
