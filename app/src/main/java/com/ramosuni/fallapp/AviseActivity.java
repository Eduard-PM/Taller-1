package com.ramosuni.fallapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ramosuni.fallapp.services.LinearAccelerometerService;

public class AviseActivity extends AppCompatActivity {

    private TextView tvCount;
    private Button btnYes, btnNo;

    private LinearAccelerometerService service;
    private boolean bound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            LinearAccelerometerService.LocalBinder localBinder = (LinearAccelerometerService.LocalBinder) binder;
            service = localBinder.getService();
            bound = true;

            // Registrar listener del countdown
            service.setCountdownListener(new LinearAccelerometerService.CountdownListener() {
                @Override
                public void onTick(long millisUntilFinished) {
                    runOnUiThread(() -> tvCount.setText(String.valueOf(millisUntilFinished / 1000)));
                }

                @Override
                public void onFinish() {
                    runOnUiThread(() -> tvCount.setText(R.string.mensaje_enviado));
                    // Cerrar la Activity automáticamente
                    finish();
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avise);

        tvCount = findViewById(R.id.tvCount);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);

        btnYes.setOnClickListener(v -> {
            if (bound && service != null) service.cancelEmergencyMessage();
            finish();
        });

        btnNo.setOnClickListener(v -> {
            runOnUiThread(() -> tvCount.setText(R.string.mensaje_enviado));
            finish();
        });

        // Mostrar sobre la pantalla bloqueada
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Conectar con el servicio
        Intent intent = new Intent(this, LinearAccelerometerService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }
}
