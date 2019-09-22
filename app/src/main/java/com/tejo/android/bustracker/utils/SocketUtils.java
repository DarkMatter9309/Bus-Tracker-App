package com.tejo.android.bustracker.utils;

import android.location.Location;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.tejo.android.bustracker.interfaces.LocationObserver;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class SocketUtils implements LocationObserver {

    private static final String TAG = "SocketUtils";
    private Observer observer;
    private Socket socket;
    private static SocketUtils sSocketUtils;
    private boolean connected = false;

    public static SocketUtils getInstance(){
        if(sSocketUtils == null){
            sSocketUtils = new SocketUtils();
        }
        return sSocketUtils;

    }

    public void connectToServer(){
        try{
            String url = "http://192.168.0.118:5002";
            socket = IO.socket(url);
            socket.connect();
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    connected = true;
                }
            });
            if(connected) {
                Log.d(TAG, "Connected to socket server");
            } else {
                Log.d(TAG, "Unable to connect to server");
            }
            recieveUpdates();
        }catch (URISyntaxException e){
            e.printStackTrace();
        }
    }

    public void recieveUpdates(){
        socket.on("bus_location", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                Log.d(TAG, "String received: " + data.toString());
                try {
                    ArrayList<LatLng> latLngs = JSONUtils.parseBusLocation(data);
                    notifyObserver(latLngs);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMyLocation(String myLocation){
        if(socket != null) {
            socket.emit("my_location", myLocation);
        }
        else {
            Log.d(TAG, "Unable to emit, socket is null");
        }
    }

    public void disconnectFromServer(){
        socket.disconnect();
    }


    @Override
    public void register(Observer observer) {
        this.observer = observer;
    }

    @Override
    public void unregister(Observer observer) {

    }

    @Override
    public void notifyObserver(ArrayList<LatLng> latLngs) {
        observer.update(latLngs);

    }
}
