package com.example.falldetectionapp  // <-- nombre de paquete

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlin.math.sqrt
import com.example.falldetectionapp.ui.theme.FallDetectionAppTheme  // <-- theme adaptado

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var _magnitude = mutableStateOf(0f)
    private var _fallDetected = mutableStateOf(false)

    // Variables para lógica de caída
    private var fallStage = 0 // 0=nada, 1=caída libre detectada

    // Temporizador para SMS
    private var handler: Handler? = null

    // Registrar permisos de SMS
    private val requestSmsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(this, "Permiso SMS denegado, no se podrá enviar alerta", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Preparar sensores
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Preparar temporizador
        handler = Handler(Looper.getMainLooper())

        // Pedir permiso SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestSmsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        }

        setContent {
            var emergencyNumber by remember { mutableStateOf(loadEmergencyNumber() ?: "") }

            FallDetectionUI(
                magnitude = _magnitude.value,
                fallDetected = _fallDetected.value,
                emergencyNumber = emergencyNumber,
                onEmergencyNumberChange = { emergencyNumber = it },
                onSaveNumber = {
                    saveEmergencyNumber(emergencyNumber)
                    Toast.makeText(this, "Número de emergencia guardado", Toast.LENGTH_SHORT).show()
                },
                onCancel = {
                    _fallDetected.value = false
                    Toast.makeText(this, "Alerta cancelada", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    override fun onResume() {
        super.onResume()
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]
                val magnitude = sqrt(x * x + y * y + z * z) //formula usada para detectar velocidad
                _magnitude.value = magnitude

                //Configuración de valores para detección de caída

                when (fallStage) {
                    0 -> if (magnitude < 2) fallStage = 1 // caída libre detectada
                    1 -> if (magnitude > 15) {
                        fallStage = 2
                        _fallDetected.value = true
                        startFallTimer()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No necesario en este ejemplo
    }

    private fun startFallTimer() {
        handler?.postDelayed({
            if (_fallDetected.value) {
                val emergencyNumber = loadEmergencyNumber()
                if (!emergencyNumber.isNullOrBlank()) {
                    sendEmergencySms(emergencyNumber)
                } else {
                    Toast.makeText(this, "No hay número de emergencia configurado", Toast.LENGTH_SHORT).show()
                }
                _fallDetected.value = false
            }
        }, 30000)
    }

    private fun sendEmergencySms(phoneNumber: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                phoneNumber,
                null,
                "Posible caída detectada. Necesito ayuda.",
                null,
                null
            )
            Toast.makeText(this, "SMS de emergencia enviado a $phoneNumber", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al enviar SMS", Toast.LENGTH_SHORT).show()
        }
    }
    // --- Persistencia con SharedPreferences ---
    private fun saveEmergencyNumber(number: String) {
        val prefs = getSharedPreferences("fall_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("emergency_number", number).apply()
    }

    private fun loadEmergencyNumber(): String? {
        val prefs = getSharedPreferences("fall_prefs", Context.MODE_PRIVATE)
        return prefs.getString("emergency_number", null)
    }
}

@Composable
fun FallDetectionUI(
    magnitude: Float,
    fallDetected: Boolean,
    emergencyNumber: String,
    onEmergencyNumberChange: (String) -> Unit,
    onSaveNumber: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Magnitud actual: ${"%.2f".format(magnitude)} m/s²")
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = emergencyNumber,
                onValueChange = onEmergencyNumberChange,
                label = { Text("Número de emergencia") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onSaveNumber) {
                Text("Guardar número")
            }

            Spacer(modifier = Modifier.height(30.dp))

            if (fallDetected) {
                Text("¡Posible caída detectada!")
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = onCancel) {
                    Text("Estoy bien, cancelar alerta")
                }
            } else {
                Text("Sin caídas detectadas")
            }
        }
    }
}