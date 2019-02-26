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

public class SearchResults extends AppCompatActivity implements OnMapReadyCallback {

    private Intent in;
    private SearchResults.MyReciever rec;
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
                title.setText(Html.fromHtml("<b>Title: </b>" + selectedItem.getTitle()));
                title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView link = findViewById(R.id.link);
                link.setText(Html.fromHtml("<b>Link: </b>" + selectedItem.getLink()));
                link.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                link.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView cat = findViewById(R.id.cat);
                cat.setText(Html.fromHtml("<b>Category: </b>" + selectedItem.getCategory()));
                cat.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                cat.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView lat = findViewById(R.id.lat);
                lat.setText(Html.fromHtml("<b>Latitude: </b>" + Double.toString(selectedItem.getLat())));
                lat.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                lat.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView lon = findViewById(R.id.lon);
                lon.setText(Html.fromHtml("<b>Longitude: </b>" + Double.toString(selectedItem.getLon())));
                lon.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                lon.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView origin = findViewById(R.id.origin);
                origin.setText(Html.fromHtml("<b>Origin Date: </b>" + selectedItem.getOriginDate().toString()));
                origin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                origin.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView loc = findViewById(R.id.loc);
                loc.setText(Html.fromHtml("<b>Location: </b>" + selectedItem.getLocation()));
                loc.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                loc.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView mag = findViewById(R.id.mag);
                mag.setText(Html.fromHtml("<b>Magnitude: </b>" + Double.toString(selectedItem.getMagnitude())));
                mag.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mag.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

                TextView dep = findViewById(R.id.dep);
                dep.setText(Html.fromHtml("<b>Depth: </b>" + Integer.toString(selectedItem.getDepth()) + "km"));
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

        rec = new SearchResults.MyReciever();
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

    public class MyReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(SearchResults.this,"The channel has been refreshed. Please return to the main menu to receive updates.",Toast.LENGTH_SHORT).show();
        }
    }
}
