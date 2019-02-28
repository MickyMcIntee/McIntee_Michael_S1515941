package com.example.mcintee_michael_s1515941;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * The map activity is used to update and hold the map and display it to the user.
 * When retrieving the map it does it in Async method, and a second thread is created to query
 * if the map is ready before applying the points.
 * @author michaelmcintee
 * @version 1.0
 * @see java.io.Serializable
 * @see OnMapReadyCallback
 * @see FragmentActivity
 * StudentID - S1515941
 * Programme - BSc SDfB
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent in;
    private Intent i;
    private Persister p;
    private int id;
    private Item item;
    private Button infoButton;
    LatLng area;
    LatLng origarea;
    SupportMapFragment mapFragment;
    private MyReceiver rec;
    private IntentFilter ifi;

    /**
     * On create method responsible for setting up the stage and making connections from XML to java.
     * It is part of the the activity lifecycle and is called when the activity is created.
     * @param savedInstanceState the bundle that can be passed through and read from certain
     *                           calls of the lifecycle like from an on resume after saved instance state
     *                           to save values.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        in = getIntent();                                           //Set up intent.
        i = new Intent(this,InfoActivity.class);      //Set next intent to info activity.
        p = in.getParcelableExtra("persister");               //Get the persister from previous activity.
        id = in.getIntExtra("ItemID",0);          //Get the selected item from previous activity.
        item = p.getChannel().getItems().get(id);                   //Get the specified item from channel.
        infoButton = findViewById(R.id.back);                       //Set up button
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                       //Anonymous on click to package up values to return to info activity.
                i.putExtra("persister",p);
                i.putExtra("ItemID",id);
                startActivity(i);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rec = new MyReceiver();     //Create new receiver to receive broadcasts from service.
        ifi = new IntentFilter("com.example.mcintee_michael_s1515941.RefreshData"); //Intent filter for channel specified.
        registerReceiver(rec,ifi);  //Register the receiver to receive broadcasts from intent filter.
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker for the relevant item, get a size for circles of all quakes
     * based on the magnitude and plotting them all over the map. We move the camera to a point
     * set the zoom of the item to 12 and stop movement of the camera, only zoom is allowed.
     * @param googleMap is the google map api call to get the map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; //Set map parameter to the googlemap passed from the async map fragment task.

        // Add a marker on the item selected param and move the camera to that area using lat and long.
        origarea = new LatLng(item.getLat(),item.getLon());
        double radius;
        mMap.addMarker(new MarkerOptions().position(origarea).title("Earthquake on: " + item.getPubDate().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(Float.parseFloat("12.0")));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origarea));

        //For each item in the channels list of items get the location, get the magnitude. This will make the size of the circle dependant on the quake.
        for(Item i:p.getChannel().getItems()) {
            area = new LatLng(i.getLat(),i.getLon());
            double mag = i.getMagnitude();
            radius = mag * 1200; //Multiply by grand but arbitrary value to show the circles on map
            if(radius < 0) {
                radius = radius * -1; //If negative make positive so can be used as radius for circle
            }
            mMap.addCircle(new CircleOptions().clickable(false).center(area).radius(radius).fillColor(Color.BLUE)); //Add the circle, make it blue and make the size dependant on the magnitude
        }
        mMap.getUiSettings().setZoomControlsEnabled(true); //Allow the user to zoom
        mMap.getUiSettings().setScrollGesturesEnabled(false); //Disallow the user to deviate from the position of selected earth quake.
    }

    /**
     * The on pause event which is part of the activity lifecycle. This is run when the user deviates
     * from the activity through a push of the home button etc.
     */
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(rec); //Unregister the receiver so the messages aren't received when the user is not in the app.
    }

    /**
     * The on resume event which is part of the activity lifecycle. This is run when the user resumes from
     * a pause likes going back in to the app after coming out of it.
     */
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(rec,ifi);  //Re-register the receiver to start picking up messages again for data updates.
    }

    /**
     * The MyReceiver class is responsible for running as a broadcast receiver and defines
     * what should be done when a broadcast from the service is received on the specified channel.
     * @see android.content.BroadcastReceiver
     */
    public class MyReceiver extends BroadcastReceiver {

        /**
         * On Receive method called when the broadcast receives a broadcast from the service.
         * @param context the context in which the broadcast was received.
         * @param intent the intent in which the broadcast is received.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MapActivity.this,"The channel has been refreshed. Please return to the main menu to receive updates.",Toast.LENGTH_SHORT).show();
        }
    }
}
