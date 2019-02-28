package com.example.mcintee_michael_s1515941;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the search results activity and it is used when the user enters a filter criteria, it has a spinner
 * to aid selection in a compact way and also has a scroll view for information and a map fragment to show
 * the selected map. In this a map uses an async get task so a loop on a separate thread is required to stop
 * map processing until the map is loaded then perform the actions on the main UI thread as map tasks
 * must be done on main.
 * @author michaelmcintee
 * @version 1.0
 * @see OnMapReadyCallback
 * StudentID - S1515941
 * Programme - BSc SDfB
 */
public class SearchResults extends AppCompatActivity implements OnMapReadyCallback {

    private Intent in;
    private SearchResults.MyReceiver rec;
    private IntentFilter ifi;
    private Spinner selectRecord;
    private Channel channel;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private LatLng origarea;
    private Item selectedItem;
    private Button backButton;
    private boolean mapready;
    private Persister p;
    private LinearLayout info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        selectRecord = findViewById(R.id.selectRecord);
        info = findViewById(R.id.showInfo);
        mapready = false;

        in = getIntent();
        channel = (Channel)in.getSerializableExtra("filtered");
        p = in.getParcelableExtra("persister");
        List<String> array = new ArrayList<>();
        for(Item i : channel.getItems()) {
            array.add(i.getOriginDate() + " - " + i.getLocation());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.spin, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectRecord.setAdapter(adapter);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapInfo);
        mapFragment.getMapAsync(this);

        selectRecord.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (Item i : channel.getItems()) {
                    if((i.getOriginDate() + " - " + i.getLocation()).equals(selectRecord.getSelectedItem().toString())) {
                        selectedItem = i;
                    }
                }
                origarea = new LatLng(selectedItem.getLat(),selectedItem.getLon());
                Thread thread = new Thread() {
                    public void run() {
                        while(!mapready) {
                            try { Thread. sleep(2000); }
                            catch (InterruptedException e) {

                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMap.clear();
                                mMap.addMarker(new MarkerOptions().position(origarea).title("Earthquake on: " + selectedItem.getPubDate().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                mMap.moveCamera(CameraUpdateFactory.zoomTo(Float.parseFloat("12.0")));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(origarea));
                                mMap.getUiSettings().setZoomControlsEnabled(true); //Allow the user to zoom
                                mMap.getUiSettings().setScrollGesturesEnabled(false); //Disallow the user to deviate from the position of selected earth quake.
                            }
                        });
                    }
                };

                thread.start();

                TextView title = findViewById(R.id.title);
                title.setText(getResources().getString(R.string.title,selectedItem.getTitle()));
                title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView link = findViewById(R.id.link);
                link.setText(getResources().getString(R.string.link,selectedItem.getLink()));
                link.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                link.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView cat = findViewById(R.id.cat);
                cat.setText(getResources().getString(R.string.category, selectedItem.getCategory()));
                cat.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                cat.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView lat = findViewById(R.id.lat);
                lat.setText(getResources().getString(R.string.lat, selectedItem.getLat()));
                lat.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                lat.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView lon = findViewById(R.id.lon);
                lon.setText(getResources().getString(R.string.lon, selectedItem.getLon()));
                lon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                lon.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView origin = findViewById(R.id.origin);
                origin.setText(getResources().getString(R.string.orig, selectedItem.getOriginDate().toString()));
                origin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                origin.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView loc = findViewById(R.id.loc);
                loc.setText(getResources().getString(R.string.location, selectedItem.getLocation()));
                loc.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                loc.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView mag = findViewById(R.id.mag);
                mag.setText(getResources().getString(R.string.magnitude,selectedItem.getMagnitude()));
                mag.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mag.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView dep = findViewById(R.id.dep);
                dep.setText(getResources().getString(R.string.depth, selectedItem.getDepth(),"km"));
                dep.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                dep.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        backButton = findViewById(R.id.back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in = new Intent(SearchResults.this,ListDataActivity.class);
                in.putExtra("persister",p);
                startActivity(in);
            }
        });

        rec = new SearchResults.MyReceiver();
        ifi = new IntentFilter("com.example.mcintee_michael_s1515941.RefreshData");
        registerReceiver(rec,ifi);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(rec);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mapready = true;

    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(rec,ifi);
    }

    /**
     * The MyReceiver class is responsible for running as a broadcast receiver and defines
     * what should be done when a broadcast from the service is received on the specified channel.
     * @see android.content.BroadcastReceiver
     */
    public class MyReceiver extends BroadcastReceiver {

        /**
         * On receive method called when the broadcast receives a broadcast from the service.
         * @param context the context in which the broadcast was received.
         * @param intent the intent in which the broadcast is received.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(SearchResults.this,"The channel has been refreshed. Please return to the main menu to receive updates.",Toast.LENGTH_SHORT).show();
        }
    }
}
