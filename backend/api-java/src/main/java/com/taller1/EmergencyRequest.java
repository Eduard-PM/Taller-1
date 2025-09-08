package com.taller1;

public class EmergencyRequest {
    private String userName;
    private double latitude;
    private double longitude;

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

    @Override
    public String toString() {
        return "EmergencyRequest{"
                + "userName='" + userName + "'" + ", latitude=" + latitude + ", longitude=" + longitude + 
                '}';
    }
}
