package com.ramosuni.fallapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        // Aquí podrías enviar alerta real
    }
}
