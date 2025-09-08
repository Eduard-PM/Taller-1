import React, { useState, useEffect } from 'react';
import { View, Text, Button, StyleSheet, Alert, ActivityIndicator } from 'react-native';
import * as Location from 'expo-location';
import { Accelerometer } from 'expo-sensors';

const BACKEND_URL = 'http://192.168.137.96:7070/api/emergency';

// --- Constantes para Detección de Caída ---
const FREEFALL_THRESHOLD = 0.5; // Magnitud cercana a 0 para caída libre
const IMPACT_THRESHOLD = 4.0;   // Un impacto fuerte (4G)
const TIME_WINDOW_MS = 500;     // Tiempo en ms entre caída libre e impacto

export default function HomeScreen() {
  const [isLoading, setIsLoading] = useState(false);
  const [sensorData, setSensorData] = useState({ x: 0, y: 0, z: 0, magnitude: 0 });
  const [status, setStatus] = useState('Monitoreando...');

  // --- Lógica de Alerta (Fase 1) ---
  const handleAlert = async () => {
    if (isLoading) return; // Evita múltiples activaciones
    setIsLoading(true);
    setStatus('Procesando alerta...');
    try {
      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== 'granted') {
        throw new Error('Permiso de ubicación denegado.');
      }
      const location = await Location.getCurrentPositionAsync({});
      const { latitude, longitude } = location.coords;
      const userName = 'Usuario de Prueba';

      const response = await fetch(BACKEND_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userName, latitude, longitude }),
      });

      if (!response.ok) {
        throw new Error('El servidor no respondió correctamente.');
      }
      Alert.alert('Éxito', 'La alerta de emergencia ha sido enviada.');
      setStatus('Alerta enviada con éxito.');
    } catch (error) {
      console.error(error);
      setStatus(`Error: ${error.message}`);
      Alert.alert('Error', `No se pudo completar el proceso de alerta. ${error.message}`);
    } finally {
      // Pequeño delay antes de volver a monitorear para no detectar la misma caída
      setTimeout(() => {
        setIsLoading(false);
        setStatus('Monitoreando...');
      }, 5000); // 5 segundos de espera
    }
  };

  // --- Lógica de Sensores (Fase 2) ---
  useEffect(() => {
    let potentialFallTimestamp = 0;

    const subscription = Accelerometer.addListener(accelerometerData => {
      const { x, y, z } = accelerometerData;
      const magnitude = Math.sqrt(x * x + y * y + z * z);
      setSensorData({ x, y, z, magnitude });

      if (isLoading) return; // No detectar caídas si ya se está procesando una alerta

      // 1. Detectar posible caída libre
      if (magnitude < FREEFALL_THRESHOLD) {
        potentialFallTimestamp = Date.now();
        setStatus('Posible caída libre detectada...');
      }

      // 2. Detectar impacto después de la caída libre
      if (potentialFallTimestamp > 0) {
        if (magnitude > IMPACT_THRESHOLD) {
          const timeDiff = Date.now() - potentialFallTimestamp;
          if (timeDiff < TIME_WINDOW_MS) {
            setStatus('¡IMPACTO DETECTADO! Iniciando alerta...');
            handleAlert();
          }
        }
      }

      // Resetear si ha pasado mucho tiempo desde la caída libre
      if (Date.now() - potentialFallTimestamp > TIME_WINDOW_MS) {
        potentialFallTimestamp = 0;
      }
    });

    // Limpiar la suscripción al desmontar el componente
    return () => subscription.remove();
  }, [isLoading]); // Se vuelve a ejecutar si isLoading cambia

  return (
    <View style={styles.container}>
      <Text style={styles.title}>App de Emergencia</Text>
      
      <View style={styles.statusBox}>
        <Text style={styles.statusText}>Estado: {status}</Text>
        <Text style={styles.sensorText}>X: {sensorData.x.toFixed(2)} Y: {sensorData.y.toFixed(2)} Z: {sensorData.z.toFixed(2)}</Text>
        <Text style={styles.sensorText}>Magnitud: {sensorData.magnitude.toFixed(2)} G</Text>
      </View>

      {isLoading ? (
        <ActivityIndicator size="large" color="#ecf0f1" style={{ marginVertical: 20 }}/>
      ) : (
        <Button
          title="Activar Alerta Manualmente"
          onPress={handleAlert}
          color="#c0392b"
          disabled={isLoading}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#34495e',
    padding: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#ecf0f1',
    marginBottom: 20,
  },
  statusBox: {
    backgroundColor: '#2c3e50',
    borderRadius: 10,
    padding: 15,
    marginBottom: 20,
    alignItems: 'center',
  },
  statusText: {
    fontSize: 18,
    color: '#ecf0f1',
    fontWeight: 'bold',
    marginBottom: 10,
  },
  sensorText: {
    fontSize: 14,
    color: '#bdc3c7',
  },
});