package com.ramosuni.fallapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.SmsManager;


import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings("FieldMayBeFinal")
public class AviseActivity extends AppCompatActivity {
    private TextView tvCount;
    private Button btnYes, btnNo;
    private CountDownTimer countDownTimer;
    private int time = 10; // segundos por defecto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mostrar actividad encima de la pantalla bloqueada
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        );

        setContentView(R.layout.activity_avise);

        tvCount = findViewById(R.id.tvCount);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);

        // Iniciar contador
        startCount();

        btnYes.setOnClickListener(v -> {
            stopCount();
            Toast.makeText(this, "Has confirmado que estás bien ✅", Toast.LENGTH_SHORT).show();
            finish(); // Solo se cierra con "Sí"
        });

        btnNo.setOnClickListener(v -> setNo());
    }

    private void startCount() {
        countDownTimer = new CountDownTimer(time * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCount.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tvCount.setText("0");
                setNo(); // cuando se acaba el tiempo, se dispara como "No"
            }
        };
        countDownTimer.start();
    }

    private void stopCount() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    private void setNo() {
        stopCount();
        Toast.makeText(this, "Se detectó que no estás bien ❌", Toast.LENGTH_SHORT).show();

        String emergencyNumber = "+51964141128"; // <-- sin espacios
        String emergencyMessage = "ALERTA: Posible caída detectada";

        // Verificar permiso
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 100);
        } else {
            // Permiso ya otorgado, enviar SMS
            sendSMS(emergencyNumber, emergencyMessage);
        }

        finish();
    }

    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS enviado a " + phoneNumber, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error enviando SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso fue concedido, podrías reenviar el SMS si es necesario
                Toast.makeText(this, "Permiso para enviar SMS concedido ✅", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso para enviar SMS denegado ❌", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

