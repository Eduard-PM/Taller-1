# API Java (Javalin)

Este servicio es una API en Java que recibe solicitudes de emergencia desde la aplicación móvil.

## Requisitos

- Java 17 o superior.
- Maven.

## Cómo ejecutar

1.  **Compilar el proyecto:**
    Navega al directorio `backend/api-java` y ejecuta el siguiente comando para compilar el proyecto y crear un archivo JAR ejecutable:
    ```sh
    mvn clean package
    ```

2.  **Ejecutar el servidor:**
    Una vez compilado, ejecuta el siguiente comando para iniciar el servidor:
    ```sh
    java -jar target/api-java-1.0-SNAPSHOT.jar
    ```

El servidor se iniciará en el puerto `7070`.

## Endpoint de Emergencia

-   **URL:** `/api/emergency`
-   **Método:** `POST`
-   **Cuerpo (JSON):**
    ```json
    {
      "userName": "Nombre del Usuario",
      "latitude": 12.345,
      "longitude": -67.890
    }
    ```

Al recibir una solicitud en este endpoint, la API invocará al servicio de LangChain (`service-langchain`) para generar y enviar una notificación de emergencia.
