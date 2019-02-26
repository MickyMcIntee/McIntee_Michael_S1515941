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
    private MyReciever rec;
    private IntentFilter ifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        in = getIntent();
        i = new Intent(this,InfoActivity.class);
        p = in.getParcelableExtra("persister");
        id = in.getIntExtra("ItemID",0);
        item = p.getChannel().getItems().get(id);
        infoButton = (Button)findViewById(R.id.back);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("persister",p);
                i.putExtra("ItemID",id);
                startActivity(i);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        rec = new MyReciever();
        ifi = new IntentFilter("com.example.mcintee_michael_s1515941.RefreshData");
        registerReceiver(rec,ifi);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        origarea = new LatLng(item.getLat(),item.getLon());
        String magnitude;
        double radius;
        mMap.addMarker(new MarkerOptions().position(origarea).title("Earthquake on: " + item.getPubDate().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(Float.parseFloat("12.0")));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origarea));
        for(Item i:p.getChannel().getItems()) {
            area = new LatLng(i.getLat(),i.getLon());
            int mag = i.getDescription().indexOf("Magnitude:  ");
            if(mag!=-1) {
                magnitude = i.getDescription().substring(mag+12); //Get the magnitude value from the string which is formatted the same way
            } else {
                magnitude = "0.01"; //Make small if magnitude is unknown
            }
            radius = Double.parseDouble(magnitude) * 1200; //Multiply by grand but arbitrary value to show the circles on map
            if(radius < 0) {
                radius = radius * -1; //If negative make positive so can be used as radius for circle
            }
            mMap.addCircle(new CircleOptions().clickable(false).center(area).radius(radius).fillColor(Color.BLUE)); //Add the circle, make it blue and make the size dependant on the magnitude
        }
        mMap.getUiSettings().setZoomControlsEnabled(true); //Allow the user to zoom
        mMap.getUiSettings().setScrollGesturesEnabled(false); //Disallow the user to deviate from the position of selected earth quake.
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(rec);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(rec,ifi);
    }

    public class MyReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MapActivity.this,"The channel has been refreshed. Please return to the main menu to receive updates.",Toast.LENGTH_SHORT).show();
        }
    }
}
