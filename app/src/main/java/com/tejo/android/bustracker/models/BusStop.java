package com.tejo.android.bustracker.models;

import com.google.android.gms.maps.model.LatLng;

public class BusStop {

    LatLng latLng;
    String name;

    public BusStop(String name, LatLng latLng) {
        this.latLng = latLng;
        this.name = name;
    }


    public LatLng getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }
}
