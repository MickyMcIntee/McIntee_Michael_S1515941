package com.mcintech.mcintee_michael_s1515941;

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

    /**
     * The onCreate method is what is first called when creating the activity and is used to set
     * everything up, make connections to the xml GUI and in most cases perform any required design
     * changes or adding things like listeners and stuff but not always.
     * @param savedInstanceState The bundle passed when destroying and recreating the activity which is populated
     *                           by onSaveInstanceState but I don't make use of it.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        selectRecord = findViewById(R.id.selectRecord);
        info = findViewById(R.id.showInfo);
        mapready = false; //Map is not ready.

        in = getIntent();
        channel = (Channel)in.getSerializableExtra("filtered");
        p = in.getParcelableExtra("persister");

        //For each item in the channel, which is the filtered list of elements which met the criteria received from search results.
        List<String> array = new ArrayList<>();
        for(Item i : channel.getItems()) {
            //Create a date location string to display the item on the spinner.
            array.add(i.getOriginDate() + " - " + i.getLocation());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.spin, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectRecord.setAdapter(adapter); //Add the adapter to the spinner.

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapInfo); //Get the map fragment.
        mapFragment.getMapAsync(this); //Get the map async

        selectRecord.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**
             * On the change of the spinner get the value of the selected item. It does this by when the item is changed
             * Gets the item at position selected which should correspond to the id of the item in the filtered list.
             * as they were populated sequentially.
             * @param parent The adapter from which the item has been selected.
             * @param view The view of the changed selection.
             * @param position The position of the item selected.
             * @param id The id of the item selected.
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedItem = channel.getItems().get(position);
                origarea = new LatLng(selectedItem.getLat(),selectedItem.getLon()); //Create new lat long object for map pos.

                //Create a new thread whcih when started runs the run.
                Thread thread = new Thread() {
                    public void run() {
                        while(!mapready) { //In this new thread if map is not ready loop
                            try { Thread. sleep(2000); } //Make the thread sleep for 2 seconds.
                            catch (InterruptedException e) {

                            }
                        }

                        //Once the map is ready it breaks the loop and runs a new runnable on the UI thread
                        runOnUiThread(new Runnable() {

                            /**
                             * Run method of runnable runs as the first point of the thread.
                             */
                            @Override
                            public void run() {

                                //Below must be ran on UI as maps maps and GUI changes must be made on the main user interface thread.
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

                thread.start(); //Start the thread.

                //All the below sets the text view for the relevant piece of information to the value of the selected item. Commenting top block to show structure but rest are the same.
                TextView title = findViewById(R.id.title); //Connect the gui element to java.
                title.setText(getResources().getString(R.string.title,selectedItem.getTitle()));    //Set the text of the view to the string resource using formatting for the parameters.
                title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)); //Set the layout parameters for the view.
                title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20); //Set the text size of the view to 20dp for readability.

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

            /**
             * Required as part of interface but not used as something will always be selected.
             * @param parent The parent adapter the item was selected from.
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        backButton = findViewById(R.id.back);

        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             * The onclicklistener interface defines that the onclick event must be defined. This is done
             * anonymously so is used to define what happens when the above view is clicked.
             * @param v The view from which the on click event was called. Not necessary as this is
             *          anonymous and only relevant to the view above.
             */
            @Override
            public void onClick(View v) {
                in = new Intent(SearchResults.this,ListDataActivity.class);
                in.putExtra("persister",p);
                startActivity(in);
            }
        });

        rec = new SearchResults.MyReceiver(); //Create new receiver to receive broadcasts from service.
        ifi = new IntentFilter("com.example.mcintee_michael_s1515941.RefreshData"); //Intent filter for channel specified.
        registerReceiver(rec,ifi); //Register the receiver to receive broadcasts from intent filter.
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
     * Must be implemented as part of on map callback this is called when the map is loaded and ready
     * to go.
     * @param googleMap The googlemap which is ready to be applied to the map fragment.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap; //Set the map fragment to the ready googlemap.
        mapready = true; //Inform the second thread that the map is ready and changes can be made to it.

    }

    /**
     * The on resume event which is part of the activity lifecycle. This is run when the user resumes from
     * a pause likes going back in to the app after coming out of it.
     */
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(rec,ifi); //Re-register the receiver to start picking up messages again for data updates.
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
