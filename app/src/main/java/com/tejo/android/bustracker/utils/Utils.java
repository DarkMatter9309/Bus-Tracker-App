package com.tejo.android.bustracker.utils;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.tejo.android.bustracker.models.BusStop;
import com.tejo.android.bustracker.models.UserLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class Utils {

    private static final String TAG = "Utils";
    public static ArrayList<LatLng> getAverage(HashMap<String, UserLocation> locations) {
        double latitude1 = 0, longitude1 = 0;
        double latitude2 = 0, longitude2 = 0;
        Collection<UserLocation> userLocations = locations.values();
        ArrayList<LatLng> latLngs = new ArrayList<>(2);
        latLngs.add(null);
        latLngs.add(null);
        int count1=0, count2=0;
        for(UserLocation userLocation: userLocations){
            int direction = userLocation.getDirection();
            if(direction == 1){
                latitude1+=userLocation.getLatLng().latitude;
                longitude1+=userLocation.getLatLng().longitude;
                count1++;
            }else {

                latitude2+=userLocation.getLatLng().latitude;
                longitude2+=userLocation.getLatLng().longitude;
                count2++;
            }
        }
        if(count1!=0){
            latitude1/=count1;
            longitude1/=count1;
            latLngs.add(0,new LatLng(latitude1, longitude1));
        }
        if(count2!=0){
            latitude2/=count2;
            longitude2/=count2;
            latLngs.add(1,new LatLng(latitude2, longitude2));
        }
        Log.d(TAG, "Latitude1: "+ latitude1 + " Longitude1: "+ longitude1);

        return latLngs;
    }

    //TODO implement direction
    public static int determineDirection(ArrayList<LatLng> polyLine, LatLng userLocation, LatLng lastLocation){
        Log.d("Polyline size", String.valueOf(polyLine.size()));
//        double distance = distanceBetweenPoints(polyLine.get(0), userLocation);
//        for(LatLng latLng : polyLine) {
//            double distance1 = distanceBetweenPoints(latLng, userLocation);
//            if(distance<distance1){
//                distance = distance1;
//            }
//        }
//        System.out.println("Nearest distance" + distance);
        return 1;
    }

    public static double distanceBetweenPoints(LatLng StartP, LatLng EndP) {
        float[] results = new float[3];
        Location.distanceBetween(StartP.latitude, StartP.longitude, EndP.latitude, EndP.longitude, results);
        return results[0];
    }

    public static ArrayList<BusStop> addBusStops(int busType){
        ArrayList<BusStop> busStops = new ArrayList<>();
        busStops.add(new BusStop("Thumkunta", new LatLng(17.565460, 78.552751)));
        busStops.add(new BusStop("Hakimpet Air base", new LatLng(17.546954, 78.535078)));
        busStops.add(new BusStop("Bolarum", new LatLng(17.519088, 78.516421)));
        busStops.add(new BusStop("Alwal", new LatLng(17.501726, 78.514018)));
        busStops.add(new BusStop("Lothkunta", new LatLng(17.493973, 78.512683)));
        //busStops.add(new BusStop("Lothkunta", new LatLng(17.493971, 78.512623), 2))
        busStops.add(new BusStop("Lal Bazar", new LatLng(17.479183, 78.511043)));
        //busStops.add(new BusStop("Lal Bazar", new LatLng(17.479313, 78.510971), 2));
        busStops.add(new BusStop("Tirumalgherry", new LatLng(17.472454, 78.509927)));
        //busStops.add(new BusStop("Tirumalgherry", new LatLng(17.472471, 78.509838),2));
        busStops.add(new BusStop("Kharkhana", new LatLng(17.461475, 78.500496)));
        //busStops.add(new BusStop("Kharkhana", new LatLng(17.461545, 78.500430),2));
        busStops.add(new BusStop("JBS", new LatLng(17.449393, 78.496595)));
        //busStops.add(new BusStop("JBS", new LatLng(17.449408, 78.496442),2));
        busStops.add(new BusStop("Patny", new LatLng(17.444330, 78.495915)));
        //busStops.add(new BusStop("Patny", new LatLng(17.444167, 78.495774),2));
        busStops.add(new BusStop("Clock Tower", new LatLng(17.440705, 78.496994)));
        //busStops.add(new BusStop("Clock Tower", new LatLng(17.440615, 78.497016),2));

        if(busType==1){
            Collections.reverse(busStops);
        }
        return busStops;
    }
}
