package com.example.mcintee_michael_s1515941;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The info activity class is a class which displays information around the selected earthquake at
 * a glance.
 * @author michaelmcintee
 * @version 1.0
 * StudentID - S1515941
 * Programme - BSc SDfB
 */
public class InfoActivity extends AppCompatActivity {

    private TextView title;
    private TextView description;
    private TextView link;
    private TextView pubDate;
    private TextView category;
    private TextView lat;
    private TextView lon;
    private TextView mag;
    private TextView dep;
    private TextView origDate;
    private TextView loc;
    private Button backButton;
    private Persister p;
    private Channel c;
    private Intent in;
    private int selectedID;
    private Item i;
    private Button mapView;
    private Intent mapIn;
    private InfoActivity.MyReceiver rec;
    private IntentFilter ifi;

    /**
     * The onCreate method is what is first called when creating the activity and is used to set
     * everything up, make connections to the xml GUI and in most cases perform any required design
     * changes or adding things like listeners and stuff but not always.
     * @param savedInstanceState The bundle passed when destroying and recreating the activity which is populated
     *                           by onSaveInstanceState but I don't make use of it.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);             //Call the method of the super AppCompatActivity
        setContentView(R.layout.activity_info);         //Set the content of this activity to the activity info xml.
        title = findViewById(R.id.title);               //Set the title to the title from the id of the xml.
        description = findViewById(R.id.description);   //As above for description etc.
        link = findViewById(R.id.link);
        pubDate = findViewById(R.id.pubDate);
        category = findViewById(R.id.category);
        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);
        backButton = findViewById(R.id.back);
        mapView = findViewById(R.id.mapView);
        mag = findViewById(R.id.mag);
        dep = findViewById(R.id.dep);
        origDate = findViewById(R.id.origDate);
        loc = findViewById(R.id.loc);

        in = getIntent();                                                   //Get intent passed across activities.
        p = in.getParcelableExtra("persister");                       //Get the persister from the intent which was passed from previous activity and apply to p.
        selectedID = in.getIntExtra("ItemID",0);          //Get the selected item id from the internt passed from the previous activity.
        in = new Intent(this, ListDataActivity.class);       //Set up an intent to go back to main view.
        mapIn = new Intent(this,MapActivity.class);          //Set up an intent to go forward to the map activity.
        c = p.getChannel();                                                 //Get the passed in channel from p
        i = c.getItems().get(selectedID);                                   //Get selected item from channel using the selected id.
        title.setText(getResources().getString(R.string.title,i.getTitle()));                            //Populate fields with selected item info.
        description.setText(getResources().getString(R.string.desc,i.getDescription()));          //As above
        link.setText(getResources().getString(R.string.link,i.getLink()));
        pubDate.setText(getResources().getString(R.string.publish, i.getPubDate().toString()));
        category.setText(getResources().getString(R.string.category, i.getCategory()));
        lat.setText(getResources().getString(R.string.lat, i.getLat()));
        lon.setText(getResources().getString(R.string.lon, i.getLon()));
        mag.setText(getResources().getString(R.string.magnitude, i.getMagnitude()));
        dep.setText(getResources().getString(R.string.depth, i.getDepth(), "km"));
        origDate.setText(getResources().getString(R.string.orig, i.getOriginDate().toString()));
        loc.setText(getResources().getString(R.string.location, i.getLocation()));

        backButton.setOnClickListener(new View.OnClickListener() {          //Programmatically add the onclick listener to pack persister to the intent
            @Override
            public void onClick(View v) {
                in.putExtra("persister",p);
                startActivity(in);                                          //On click start the activity to go back.
            }
        });

        mapView.setOnClickListener(new View.OnClickListener() {            //As above
            @Override
            public void onClick(View v) {
                mapIn.putExtra("persister",p);
                mapIn.putExtra("ItemID",selectedID);
                startActivity(mapIn);                                      //Pack the intent for the map and then start the activity the intent is referencing.
            }
        });
        rec = new InfoActivity.MyReceiver();                                //Create receiver.
        ifi = new IntentFilter("com.example.mcintee_michael_s1515941.RefreshData");     //Create intent filter for what to listen to.
        registerReceiver(rec,ifi);                                    //Register the receiver to receive broadcasts from service.
    }

    /**
     * The on pause method which is called when the activity is paused this happens when going out of
     * the app etc. The goal here is to unregister the receiver to make sure that the service refreshers
     * are not picked up when out of the app.
     */
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(rec);
    }

    /**
     * The on resume method which is responsible for running when the app and activity is resumed like
     * back in to an activity where it was paused. This is responsible for re-registering the receiver
     * when back in the app to ensure refreshes occur.
     */
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
         * On Receive method called when the broadcast receives a broadcast from the service.
         * @param context the context in which the broadcast was received.
         * @param intent the intent in which the broadcast is received.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(InfoActivity.this,"The channel has been refreshed. Please return to the main menu to receive updates.",Toast.LENGTH_SHORT).show();
        }
    }
}
