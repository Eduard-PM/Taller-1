package com.example.falldetectionappjava;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.content.Intent;
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int SMS_PERMISSION_CODE = 100;
    private EditText phoneNumberInput;
    private Button testButton;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float FALL_THRESHOLD = 25.0f; // ajusta según pruebas
    private boolean fallDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        testButton = findViewById(R.id.testButton);

        testButton.setOnClickListener(v -> {
            if (checkSmsPermission()) {
                sendSms("Prueba manual: ¡Caída detectada!");
            } else {
                requestSmsPermission();
            }
        });

        // Inicializamos SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Magnitud de la aceleración
            double magnitude = Math.sqrt(x * x + y * y + z * z);

            if (magnitude > FALL_THRESHOLD && !fallDetected) {
                fallDetected = true;

                // Lanzar la pantalla de confirmación
                String phone = phoneNumberInput.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this, EmergencyActivity.class);
                intent.putExtra("PHONE_NUMBER", phone);
                startActivity(intent);

                // Reiniciar detección tras un tiempo
                phoneNumberInput.postDelayed(() -> fallDetected = false, 5000);
            }

        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No usamos
    }


    private void sendSms(String message) {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Ingresa un número de emergencia", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS enviado a " + phoneNumber, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error enviando SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS},
                SMS_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSms("Permiso concedido, reenviando alerta de caída");
            } else {
                Toast.makeText(this, "Permiso de SMS denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
