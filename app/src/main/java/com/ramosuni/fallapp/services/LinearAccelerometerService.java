package com.ramosuni.fallapp.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ramosuni.fallapp.AviseActivity;
import com.ramosuni.fallapp.DatabaseHelper;

public class LinearAccelerometerService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor linearAccelerometer;
    private DatabaseHelper dbHelper;

    private static final float THRESHOLD = 15.0f; // ajusta según pruebas

    private Handler handler = new Handler();
    private Runnable sendSmsRunnable;

    private CountDownTimer smsCountdown;
    private long tiempoRestante = 0; // ms

    private final IBinder binder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            dbHelper = new DatabaseHelper(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (linearAccelerometer != null) {
            sensorManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        if (sendSmsRunnable != null) handler.removeCallbacks(sendSmsRunnable);
    }

    public class LocalBinder extends Binder {
        public LinearAccelerometerService getService() {
            return LinearAccelerometerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calcular vector resultante
            double aRes = Math.sqrt(x * x + y * y + z * z);

            // Guardar en SQLite
            dbHelper.insertData(x, y, z, aRes, System.currentTimeMillis());

            // Emitir datos al Activity
            Intent accelIntent = new Intent("ACCELEROMETER_DATA");
            accelIntent.putExtra("x", x);
            accelIntent.putExtra("y", y);
            accelIntent.putExtra("z", z);
            accelIntent.putExtra("aRes", aRes);
            LocalBroadcastManager.getInstance(this).sendBroadcast(accelIntent);

            // Detectar caída
            detectarCaida((float) aRes);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    private void detectarCaida(float aRes) {
        if (aRes > THRESHOLD) {
            // Abrir pantalla de aviso
            Intent intent = new Intent(this, AviseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            // Iniciar countdown de SMS
            startCountdownSMS();
        }
    }

    private void startCountdownSMS() {
        if (smsCountdown != null) smsCountdown.cancel();

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String numero = prefs.getString("numero_emergencia", "");
        int tiempoEspera = prefs.getInt("tiempo_espera", 10);

        if (numero.isEmpty()) return;

        tiempoRestante = tiempoEspera * 1000;

        smsCountdown = new CountDownTimer(tiempoRestante, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestante = millisUntilFinished;
                if (countdownListener != null) countdownListener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (countdownListener != null) countdownListener.onFinish();
                sendEmergencyMessage(numero);
            }
        }.start();
    }

    private void sendEmergencyMessage(String numero) {
        String mensaje = "⚠️ Posible caída detectada. Por favor, verifica mi estado.";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, mensaje, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelEmergencyMessage() {
        // Llamar desde actividad si usuario cancela alerta
        if (smsCountdown != null) smsCountdown.cancel();
        tiempoRestante = 0;
        if (countdownListener != null) countdownListener.onFinish();
    }

    public interface CountdownListener {
        void onTick(long millisUntilFinished);
        void onFinish();
    }

    private CountdownListener countdownListener;

    public void setCountdownListener(CountdownListener listener) {
        this.countdownListener = listener;
    }

}
