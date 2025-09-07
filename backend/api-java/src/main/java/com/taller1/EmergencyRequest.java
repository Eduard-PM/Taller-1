package com.taller1;

// Esta es una clase POJO (Plain Old Java Object).
// Jackson la usará para convertir automáticamente el JSON que llega en la petición
// a un objeto Java con el que podemos trabajar.
public class EmergencyRequest {
    private String userName;
    private double latitude;
    private double longitude;

    // Getters y Setters son necesarios para que la librería Jackson funcione.
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Un método toString() es útil para imprimir el objeto y depurar.
    @Override
    public String toString() {
        return "EmergencyRequest{"
                + "userName='" + userName + "'" + ", latitude=" + latitude + ", longitude=" + longitude + 
                '}';
    }
}
