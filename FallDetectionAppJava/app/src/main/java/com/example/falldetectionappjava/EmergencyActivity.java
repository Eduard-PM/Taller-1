package com.example.falldetectionappjava;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.telephony.SmsManager;

public class EmergencyActivity extends AppCompatActivity {

    private Button confirmButton, cancelButton;
    private String phoneNumber;  // se recibirá desde MainActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Recuperar el número del Intent
        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");

        confirmButton.setOnClickListener(v -> {
            sendSms();
            finish(); // cerrar pantalla
        });

        cancelButton.setOnClickListener(v -> {
            Toast.makeText(this, "Emergencia cancelada", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void sendSms() {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, "No se configuró número de emergencia", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null,
                    "¡Caída confirmada! Necesito ayuda.", null, null);
            Toast.makeText(this, "SMS enviado a " + phoneNumber, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error enviando SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
