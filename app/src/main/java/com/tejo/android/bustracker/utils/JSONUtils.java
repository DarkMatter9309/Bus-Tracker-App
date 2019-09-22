package com.tejo.android.bustracker.utils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class JSONUtils {
    private static final String TAG = "JSONUtils";
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static ArrayList<LatLng> getPolyline(Context context) throws JSONException {
        ArrayList<LatLng> resultLine = new ArrayList<>();
        String result = loadJSONFromAsset(context, "directions.json");
        JSONObject res = new JSONObject(result);
        JSONArray steps = res.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        for(int i=0; i<steps.length();i++){
            String encodedPoly = steps.getJSONObject(i).getJSONObject("polyline").getString("points");
            resultLine.addAll(PolyUtil.decode(encodedPoly));
        }
        return resultLine;
    }

    public static ArrayList<LatLng> parseBusLocation(JSONObject data) throws JSONException {
        JSONObject bus1 = (JSONObject) data.get("bus1");
        JSONObject bus2 = (JSONObject) data.get("bus2");
        Boolean isLive1 = (Boolean) bus1.get("is_live");
        Boolean isLive2 = (Boolean) bus2.get("is_live");
        JSONObject location1 = (JSONObject) bus1.get("location");
        JSONObject location2 = (JSONObject) bus2.get("location");
        Double lat1 = (Double) location1.get("latitude");
        Double lng1 = (Double) location1.get("longitude");
        Double lat2 = (Double) location2.get("latitude");
        Double lng2 = (Double) location2.get("longitude");
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(lat1, lng1));
        latLngs.add(new LatLng(lat2, lng2));
        Log.d(TAG, "BUS1 Latitude: " + lat1 + " Bus1 lng: " + lng1);
        return latLngs;
    }
}
