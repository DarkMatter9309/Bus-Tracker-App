package com.tejo.android.bustracker;


import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.Manifest;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.tejo.android.bustracker.models.MyLocation;
import com.tejo.android.bustracker.utils.SocketUtils;
import com.tejo.android.bustracker.utils.Utils;

import static com.tejo.android.bustracker.MapsActivity.polylineList;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    private static final String CHANNEL_ID = "12";
    private boolean isDestroyCalled;
    private LatLng lastLocation;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mClient;
    private SocketUtils mSocketUtils;
    private boolean isDirectionDetermined;
    private int direction;

    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public void onCreate() {
        super.onCreate();
        mSocketUtils = SocketUtils.getInstance();
        buildNotification();
        requestLocationUpdates();
    }

    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.ic_tracker);
        startForeground(1, builder.build());
    }
    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            //android.os.Process.killProcess(android.os.Process.myPid());
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };



    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(2000);
        request.setFastestInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mClient = LocationServices.getFusedLocationProviderClient(this);
        final String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {

            mLocationCallback = new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if(location!=null && polylineList!=null){
                        LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        boolean isOnRoute = PolyUtil.isLocationOnPath(locationLatLng, polylineList, true, 1000);
                        Log.d(TAG, "location update " + location);
                        if(!isDirectionDetermined){
                            if(lastLocation!=null) {
                                if(Utils.distanceBetweenPoints(locationLatLng, lastLocation)>20) {
                                    direction = Utils.determineDirection(polylineList, locationLatLng, lastLocation);
                                }
                            } else {
                                lastLocation = locationLatLng;
                            }
                            isDirectionDetermined = true;
                        }
                        MyLocation myLocation = new MyLocation(android_id, isOnRoute, direction, location);
                        Gson gson = new Gson();
                        String locationString = gson.toJson(myLocation);
                        mSocketUtils.sendMyLocation(locationString);
                    }
                }
            };

            if(mLocationCallback!=null){
                mClient.requestLocationUpdates(request, mLocationCallback, null);
            }



        }
    }


    @Override
    public void onDestroy() {
        if(mClient!=null)
        mClient.removeLocationUpdates(mLocationCallback);
        else {
            mLocationCallback = null;
            isDestroyCalled = true;
        }
        mSocketUtils.disconnectFromServer();
        super.onDestroy();
    }
}