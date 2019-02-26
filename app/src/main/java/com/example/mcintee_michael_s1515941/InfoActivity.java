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
 * The info activity class is the class which displays more information about the selected earthquake. It's the java code which runs on the back of the activity.
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
    private InfoActivity.MyReciever rec;
    private IntentFilter ifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        title = (TextView)findViewById(R.id.title);
        description = (TextView)findViewById(R.id.description);
        link = (TextView)findViewById(R.id.link);
        pubDate = (TextView)findViewById(R.id.pubDate);
        category = (TextView)findViewById(R.id.category);
        lat = (TextView)findViewById(R.id.lat);
        lon = (TextView)findViewById(R.id.lon);
        backButton = (Button)findViewById(R.id.back);
        mapView = (Button)findViewById(R.id.mapView);
        mag = findViewById(R.id.mag);
        dep = findViewById(R.id.dep);
        origDate = findViewById(R.id.origDate);
        loc = findViewById(R.id.loc);

        in = getIntent();
        p = in.getParcelableExtra("persister");
        selectedID = in.getIntExtra("ItemID",0);
        in = new Intent(this, ListDataActivity.class);
        mapIn = new Intent(this,MapActivity.class);
        c = p.getChannel();
        i = c.getItems().get(selectedID);
        title.setText(getResources().getString(R.string.title)  + i.getTitle());
        description.setText(getResources().getString(R.string.desc) + i.getDescription());
        link.setText(getResources().getString(R.string.link) + i.getLink());
        pubDate.setText(getResources().getString(R.string.publish) + i.getPubDate().toString());
        category.setText(getResources().getString(R.string.category) + i.getCategory());
        lat.setText(getResources().getString(R.string.lat) + Double.toString(i.getLat()));
        lon.setText(getResources().getString(R.string.lon) + Double.toString(i.getLon()));
        mag.setText(getResources().getString(R.string.magnitude) + Double.toString(i.getMagnitude()));
        dep.setText(getResources().getString(R.string.depth) + Integer.toString(i.getDepth()) + " km");
        origDate.setText(getResources().getString(R.string.orig) + i.getOriginDate().toString());
        loc.setText(getResources().getString(R.string.location) + i.getLocation());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in.putExtra("persister",p);
                startActivity(in);
            }
        });

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapIn.putExtra("persister",p);
                mapIn.putExtra("ItemID",selectedID);
                startActivity(mapIn);
            }
        });
        rec = new InfoActivity.MyReciever();
        ifi = new IntentFilter("com.example.mcintee_michael_s1515941.RefreshData");
        registerReceiver(rec,ifi);
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
            Toast.makeText(InfoActivity.this,"The channel has been refreshed. Please return to the main menu to receive updates.",Toast.LENGTH_SHORT).show();
        }
    }


}
