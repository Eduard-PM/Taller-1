# FallApp - Android Fall Detection App

**FallApp** es una aplicación Android que detecta caídas usando el **sensor de aceleración lineal** del dispositivo y envía alertas por **SMS** a un número de emergencia configurado. Además, permite visualizar el historial de datos de aceleración mediante gráficos en tiempo real.

---

## Características

- Lectura en tiempo real del **acelerómetro lineal**.
- Cálculo del **vector resultante de la aceleración**.
- Detección de caídas basada en un umbral configurable.
- Pantalla de aviso con **cuenta regresiva** antes de enviar SMS.
- Envío de **mensaje de emergencia** por SMS al número configurado.
- Pantalla de historial con **gráficos de línea en tiempo real** para cada componente de la aceleración.
- Configuración de:
    - Número de emergencia.
    - Tiempo de espera antes de enviar mensaje.

---

## Tecnologías y librerías

- **Lenguaje:** Java
- **Base de datos local:** SQLite
- **Gráficos:** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- **Android Components:**
    - `Service` para lectura de acelerómetro en background
    - `BroadcastReceiver` para comunicación con Activities
    - `CountDownTimer` para cuenta regresiva de alertas
- Compatible con Android 8.0+ (API 26+)

---

## Requisitos

- Android Studio 2022 o superior
- Dispositivo o emulador con sensor acelerómetro
- Permiso de **SMS** en AndroidManifest
- Conexión para instalar dependencias desde **Maven** o **Gradle**

---

## Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/FallApp.git
   cd FallApp
2. Abrir el proyecto en Android Studio.

3. Sincronizar Gradle y compilar el proyecto.

4. Instalar la app en un dispositivo con acelerómetro y tarjeta SIM.
