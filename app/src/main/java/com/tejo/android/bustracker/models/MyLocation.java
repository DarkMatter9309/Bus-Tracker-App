package com.tejo.android.bustracker.models;

import android.location.Location;

public class MyLocation {
    String id;
    boolean isOnRoute;
    int direction;
    CustomLocation location;

    public MyLocation(String id, boolean isOnRoute, int direction, Location location) {
        this.id = id;
        this.isOnRoute = isOnRoute;
        this.direction = direction;
        CustomLocation customLocation = new CustomLocation(location.getAltitude(), location.getLatitude(),
                location.getLongitude(), location.getSpeed(), location.getAccuracy(), location.getTime());
        this.location = customLocation;
    }

}
