
# Proyecto de Detección de Caídas

Este proyecto contiene una aplicación para detectar caídas de usuarios y notificar a contactos de emergencia usando un mensaje generado por IA.

## Estructura

- `/mobile-app`: Contiene el código fuente de la aplicación móvil (ej. Android).
- `/backend`: Contiene los servicios del lado del servidor.
  - `/api-java`: API en Java (Spring Boot/Javalin) que la app móvil consume.
  - `/service-langchain`: Servicio en Node.js que usa LangChain para generar y enviar notificaciones de emergencia.

