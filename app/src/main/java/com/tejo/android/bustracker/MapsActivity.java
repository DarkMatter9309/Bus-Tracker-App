package com.tejo.android.bustracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import com.tejo.android.bustracker.interfaces.LocationObserver;
import com.tejo.android.bustracker.models.BusStop;
import com.tejo.android.bustracker.models.UserLocation;
import com.tejo.android.bustracker.utils.SocketUtils;
import com.tejo.android.bustracker.utils.JSONUtils;
import com.tejo.android.bustracker.utils.Utils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationObserver.Observer {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int NUM_PAGES = 2;
    public ArrayList<BusStop> busStops;
    public ArrayList<Marker> stopMarkers = new ArrayList<>();
    public ArrayList<LatLng> busLatLngs = new ArrayList<>();
    private GoogleMap mMap;
    private ArrayList<Marker> mMarkers = new ArrayList<>();
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private WormDotsIndicator dotsIndicator;
    private View bottomSheetView;
    private SocketUtils mSocketUtils;
    private Location myLocation;

    public static ArrayList<LatLng> polylineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mMarkers.add(null);
        mMarkers.add(null);
        mMarkers.add(null);
        mSocketUtils = SocketUtils.getInstance();
        myLocation = getIntent().getParcelableExtra("MyLocation");
        busStops = Utils.addBusStops(0);
        if(myLocation!=null)
        Log.d(TAG, "My location + " + myLocation.getLatitude());

        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        dotsIndicator = findViewById(R.id.worm_dots_indicator);
        dotsIndicator.setViewPager(mPager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (mMarkers.get(1) != null && mMarkers.get(2) != null) {
                    zoomToLocation(i + 1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bottomSheetView = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        BottomSheetBehavior.from(bottomSheetView).setState(BottomSheetBehavior.STATE_EXPANDED);

        FloatingActionButton fab = findViewById(R.id.go_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTrackerService();
                updateBottomSheetUI();
            }
        });

    }

    private void startTrackerService(){
        startService(new Intent(this, TrackerService.class));
    }

    private void updateBottomSheetUI(){

    }

    private void zoomToLocation(int markerIndex) {
        Projection projection = mMap.getProjection();
        LatLng markerPosition = mMarkers.get(markerIndex).getPosition();
        Point markerPoint = projection.toScreenLocation(markerPosition);
        View view = getWindow().getDecorView();
        int height = 0;
        if (BottomSheetBehavior.from(bottomSheetView).getState() == BottomSheetBehavior.STATE_EXPANDED) {
            height = view.getHeight() / 4;
        }
        Point targetPoint = new Point(markerPoint.x, markerPoint.y + height);
        LatLng targetPosition = projection.fromScreenLocation(targetPoint);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetPosition, 15), 1000, null);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        polylineList = new ArrayList<>();
        try {
            polylineList = JSONUtils.getPolyline(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(polylineList.toArray(new LatLng[polylineList.size()])));
        polyline.setColor(Color.BLUE);

        for(BusStop busStop: busStops) {
            MarkerOptions markerOptions = new MarkerOptions().position(busStop.getLatLng()).title(busStop.getName());
            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tracker));
            stopMarkers.add(mMap.addMarker(markerOptions));
        }

        if(myLocation!=null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 15));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(17.547432, 78.572484), 15));
        }


        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                CameraPosition cameraPosition = googleMap.getCameraPosition();
                //Log.d(TAG, "Camera zoom: " + cameraPosition.zoom);
                if(cameraPosition.zoom < 13.0) {
                    for(Marker marker: stopMarkers) {
                        marker.setVisible(false);
                    }
                }
                else {
                    for(Marker marker: stopMarkers) {
                        marker.setVisible(true);
                    }
                }
            }
        });

        mSocketUtils.connectToServer();
        mSocketUtils.register(this);
    }

    @Override
    public void update(final ArrayList<LatLng> latLngs) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                busLatLngs = latLngs;
                LatLng busLocation1 = busLatLngs.get(0);
                Marker marker1 = mMarkers.get(1);
                if (busLocation1 != null) {
                    if (marker1 == null) {
                        MarkerOptions markerOptions1 = new MarkerOptions().position(busLocation1);
                        mMarkers.add(1, mMap.addMarker(markerOptions1));
                    } else {
                        mMarkers.get(1).setPosition(busLocation1);
                    }

                }

                LatLng busLocation2 = busLatLngs.get(1);
                Marker marker2 = mMarkers.get(2);
                if (busLocation2 != null) {
                    if (marker2 == null) {
                        MarkerOptions markerOptions2 = new MarkerOptions().position(busLocation2);
                        mMarkers.add(2, mMap.addMarker(markerOptions2));
                    } else {
                        mMarkers.get(2).setPosition(busLocation2);
                        Log.d(TAG, "Bus location 2 changed");
                    }

                }
            }
        });

    }



    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            Log.d(TAG, "busType: " + position);

            bundle.putInt("busType", position);
            BusInfoFragment busInfoFragment = new BusInfoFragment();
            busInfoFragment.setArguments(bundle);
            return busInfoFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocketUtils.unregister(this);
        mSocketUtils.disconnectFromServer();
    }


}
