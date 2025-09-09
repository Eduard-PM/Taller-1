package com.ramosuni.fallapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.ramosuni.fallapp.services.LinearAccelerometerService;

import java.util.ArrayList;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private LineChart chartX, chartY, chartZ, chartRes;
    private DatabaseHelper dbHelper;
    private boolean isCollecting;

    private Handler handler = new Handler();
    private Runnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        chartX = findViewById(R.id.chartX);
        chartY = findViewById(R.id.chartY);
        chartZ = findViewById(R.id.chartZ);
        chartRes = findViewById(R.id.chartRes);

        dbHelper = new DatabaseHelper(this);

        isCollecting = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getBoolean("isActive", false);

        // Botón retroceso
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(HistorialActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Botón iniciar/pausar servicio
        ImageButton btnToggle = findViewById(R.id.btnPlayPause);
        btnToggle.setImageResource(
                isCollecting ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play
        );

        btnToggle.setOnClickListener(v -> {
            if (isCollecting) {
                stopService(new Intent(this, LinearAccelerometerService.class));
                btnToggle.setImageResource(android.R.drawable.ic_media_play);
            } else {
                startService(new Intent(this, LinearAccelerometerService.class));
                btnToggle.setImageResource(android.R.drawable.ic_media_pause);
            }
            isCollecting = !isCollecting;

            // Guardar estado compartido
            getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isActive", isCollecting)
                    .apply();
        });

        // Cargar y actualizar cada segundo
        updateRunnable = () -> {
            loadData();
            handler.postDelayed(updateRunnable, 1000);
        };
        handler.post(updateRunnable);
    }

    private void loadData() {
        Cursor cursor = dbHelper.getAllData();
        List<Entry> entriesX = new ArrayList<>();
        List<Entry> entriesY = new ArrayList<>();
        List<Entry> entriesZ = new ArrayList<>();
        List<Entry> entriesRes = new ArrayList<>();

        int index = 0;
        while (cursor.moveToNext()) {
            float x = cursor.getFloat(cursor.getColumnIndexOrThrow("x"));
            float y = cursor.getFloat(cursor.getColumnIndexOrThrow("y"));
            float z = cursor.getFloat(cursor.getColumnIndexOrThrow("z"));
            float aRes = cursor.getFloat(cursor.getColumnIndexOrThrow("aRes"));

            entriesX.add(new Entry(index, x));
            entriesY.add(new Entry(index, y));
            entriesZ.add(new Entry(index, z));
            entriesRes.add(new Entry(index, aRes));

            index++;
        }
        cursor.close();

        updateChart(chartX, entriesX, "Aceleración X", Color.RED);
        updateChart(chartY, entriesY, "Aceleración Y", Color.GREEN);
        updateChart(chartZ, entriesZ, "Aceleración Z", Color.BLUE);
        updateChart(chartRes, entriesRes, "Aceleración Resultante", Color.MAGENTA);
    }

    private void updateChart(LineChart chart, List<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2f);
        dataSet.setColor(color);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        Description description = new Description();
        description.setText(label);
        chart.setDescription(description);

        chart.invalidate(); // refrescar gráfico
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }
}


