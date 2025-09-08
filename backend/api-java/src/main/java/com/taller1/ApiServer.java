
package com.taller1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import io.javalin.Javalin;

public class ApiServer {

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                });
            });
        }).start("0.0.0.0", 7070);

        System.out.println("Servidor Javalin iniciado en el puerto 7070.");

        app.get("/hello", ctx -> ctx.result("Hola Mundo"));

        app.post("/api/emergency", ctx -> {
            EmergencyRequest request = ctx.bodyAsClass(EmergencyRequest.class);
            System.out.println("Alerta de emergencia recibida: " + request);

            // --- Inicio: Lógica para llamar al script de Node.js ---
            try {
                // Construye la ruta al directorio del servicio de Node.js
                String serviceDir = Paths.get("..", "service-langchain").toFile().getCanonicalPath();

                ProcessBuilder pb = new ProcessBuilder(
                        "node",
                        "src/index.js",
                        request.getUserName(),
                        String.valueOf(request.getLatitude()),
                        String.valueOf(request.getLongitude())
                );
                pb.directory(new java.io.File(serviceDir)); 

                System.out.println("Ejecutando script de Node.js en: " + serviceDir);
                Process process = pb.start();

                // Captura y muestra la salida estándar del script
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Salida del script de Node.js:\n" + output);
                } else {
                    // Si hay un error, captura y muestra la salida de error
                    StringBuilder errorOutput = new StringBuilder();
                    try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            errorOutput.append(line).append("\n");
                        }
                    }
                    System.err.println("Error al ejecutar script (código " + exitCode + "):\n" + errorOutput);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                ctx.status(500).result("Error interno al procesar la notificación.");
                return;
            }

            ctx.status(200).result("Reporte de emergencia para '" + request.getUserName() + "' procesado.");
        });
    }
}
