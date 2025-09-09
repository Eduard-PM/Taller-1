package com.ramosuni.fallapp.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ramosuni.fallapp.DatabaseHelper;

public class LinearAccelerometerService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor linearAccelerometer;
    private DatabaseHelper dbHelper;

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.insertData(x, y, z, aRes, System.currentTimeMillis());

            // Emitir datos al Activity
            Intent accelIntent = new Intent("ACCELEROMETER_DATA");
            accelIntent.putExtra("x", x);
            accelIntent.putExtra("y", y);
            accelIntent.putExtra("z", z);
            accelIntent.putExtra("aRes", aRes);
            LocalBroadcastManager.getInstance(this).sendBroadcast(accelIntent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}