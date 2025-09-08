# Servicio LangChain (Node.js)

Este servicio utiliza LangChain y Twilio para generar un mensaje de emergencia y enviarlo por SMS a un contacto predefinido.

## Requisitos

- Node.js.
- npm.

## Configuración

1.  **Crear archivo de entorno:**
    En la raíz del proyecto (directorio `Taller-1`), crea un archivo llamado `.env`.

2.  **Añadir variables de entorno:**
    Copia el contenido de `.env.example` (ubicado en `backend/service-langchain`) a tu nuevo archivo `.env` y rellena los valores:

    ```
    # Clave de API de Google para el modelo Gemini
    GOOGLE_API_KEY="TU_API_KEY_DE_GOOGLE"

    # Credenciales de Twilio
    TWILIO_ACCOUNT_SID="TU_SID_DE_CUENTA_DE_TWILIO"
    TWILIO_AUTH_TOKEN="TU_TOKEN_DE_AUTENTICACION_DE_TWILIO"

    # Números de teléfono
    TWILIO_PHONE_NUMBER="TU_NUMERO_DE_TELEFONO_DE_TWILIO" # Formato: +1234567890
    EMERGENCY_CONTACT_NUMBER="NUMERO_DEL_CONTACTO_DE_EMERGENCIA" # Formato: +1234567890
    ```

## Instalación

1.  Navega al directorio `backend/service-langchain`.
2.  Ejecuta el siguiente comando para instalar las dependencias:
    ```sh
    npm install
    ```

## Cómo funciona

Este servicio **es invocado automáticamente por la `api-java`**. No necesita ser ejecutado de forma independiente en el flujo normal.

Sin embargo, puedes ejecutarlo manualmente para pruebas desde la terminal. Asegúrate de pasar los argumentos necesarios:

```sh
node src/index.js "NombrePrueba" 12.345 -67.890
```
