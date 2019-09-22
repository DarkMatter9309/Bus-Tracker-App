package com.tejo.android.bustracker.models;

public class CustomLocation {
    private double altitude;
    private double latitude;
    private double longitude;
    private double speed;
    private double accuracy;
    private long time;

    public CustomLocation(double altitude, double latitude, double longitude, double speed, double accuracy, long time) {
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.accuracy = accuracy;
        this.time = time;
    }


}
