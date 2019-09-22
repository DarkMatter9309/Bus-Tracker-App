package com.tejo.android.bustracker.interfaces;


import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public interface LocationObserver {
    public void register(Observer observer);

    public void unregister(Observer observer);

    public void notifyObserver(ArrayList<LatLng> latLngs);

    interface Observer {
        public void update(ArrayList<LatLng> latLngs);
    }
}
