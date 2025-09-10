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

## Hardware utilizado

- **Dispositivo Android** con acelerómetro lineal (tipo teléfono o tablet).
- **Sensor de aceleración lineal** integrado en el dispositivo (no se requiere hardware externo).
- **SIM con plan de SMS** activo para enviar alertas.

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

4. Instalar la app en un dispositivo con acelerómetro.

---

## Referencias sobre la ecuación de aceleración total

La aceleración total o **aceleración resultante** se calcula como la magnitud del vector aceleración tridimensional:

![fórmula](https://latex.codecogs.com/svg.latex?\color{White}a_{res}=\sqrt{x^2+y^2+z^2})

donde:

- \(x, y, z\) son las componentes de la aceleración en los ejes del dispositivo (m/s²).
- a_res es la aceleración total medida por el acelerómetro lineal.

Esta fórmula proviene de la **geometría vectorial** para obtener la magnitud de un vector en 3 dimensiones.

### Referencias:

1. Halliday, D., Resnick, R., & Walker, J. (2014). *Fundamentals of Physics* (10th ed.). Wiley.
    - Sección: Movimiento en tres dimensiones, magnitud de un vector.
2. Android Developers. *Sensors Overview*  
   [https://developer.android.com/guide/topics/sensors/sensors_overview](https://developer.android.com/guide/topics/sensors/sensors_overview)
3. Tipler, P. A., & Mosca, G. (2008). *Physics for Scientists and Engineers* (6th ed.). W.H. Freeman.
    - Capítulo: Vectores y movimiento en el espacio.

