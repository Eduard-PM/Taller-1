package com.ramosuni.fallapp;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView tvAccelValues;
    private SwitchCompat switchActivate;

    private boolean isActive = false;

    private static final float FALL_THRESHOLD = 25.0f; // Ajustable
    private boolean fallDetected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvAccelValues = findViewById(R.id.tvAccelValues);
        switchActivate = findViewById(R.id.switchActivate);
        Button btnHistorial = findViewById(R.id.btnHistorial);
        Button btnConfiguracion = findViewById(R.id.btnConfiguracion);
        ImageView ivAppIcon = findViewById(R.id.ivAppIcon);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        switchActivate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isActive = isChecked;
            if(isActive) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                sensorManager.unregisterListener(this);
                tvAccelValues.setText("X: 0.0, Y: 0.0, Z: 0.0");
            }
        });

        btnHistorial.setOnClickListener(v -> {
            // Aquí iría la navegación al historial
        });

        btnConfiguracion.setOnClickListener(v -> {
            // Aquí iría la navegación a configuración
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(isActive && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            tvAccelValues.setText(
                    String.format(Locale.getDefault(), getString(R.string.accel_format), x, y, z)
            );

            double magnitude = Math.sqrt(x*x + y*y + z*z);

            if (!fallDetected && magnitude > FALL_THRESHOLD) {
                fallDetected = true;
                Intent intent = new Intent(MainActivity.this, AviseActivity.class);
                startActivity(intent);
                new android.os.Handler().postDelayed(() -> fallDetected = false, 5000);
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}

