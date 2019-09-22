package com.tejo.android.bustracker.models;

import com.google.android.gms.maps.model.LatLng;

public class UserLocation {
    private int direction;

    private boolean isOnRoute;

    private LatLng latLng;

    public int getDirection() {
        return direction;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public UserLocation(LatLng latLng, int direction) {
        this.latLng = latLng;
        this.direction = direction;
    }
}
