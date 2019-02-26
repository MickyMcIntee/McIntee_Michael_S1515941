package com.example.mcintee_michael_s1515941;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 *  ListDataActivity - This class is used to load data in to the screen for selecting elements for more info. It also allows some search functionality to show calculated greatest of elements.
 * @author michaelmcintee
 * @version 1.0
 * S1515941
 * BSc SDfB
 */
public class ListDataActivity extends AppCompatActivity {

    private LinearLayout holder;
    private Channel channel;
    private Intent in;
    private ImageView imageView;
    private ImageView searchButton;
    private int id = 0;
    private Persister p;
    private EditText fromDate;
    private EditText toDate;
    private DatePickerDialog.OnDateSetListener dateFrom;
    private DatePickerDialog.OnDateSetListener dateTo;
    private Calendar myCal;
    private Button calculateButton;
    private int northid;
    private int southid;
    private int westid;
    private int eastid;
    private int largemagid;
    private int deepid;
    private int shallowid;
    private TextView northernly;
    private TextView southernly;
    private TextView westernly;
    private TextView easternly;
    private TextView shallow;
    private TextView deep;
    private TextView largemag;
    private Intent globalIn;
    private MyReciever rec;
    private IntentFilter ifi;
    private ImageDownloader idd;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        holder = findViewById(R.id.selectionDisplay);
        in = getIntent();
        p = in.getParcelableExtra("persister");
        channel = p.getChannel();
        id = 0;
        for(Item i:channel.getItems()) {
            Button b = new Button(this);
            b.setText(i.getDescription());
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.setMargins(8,5,8,5);
            b.setLayoutParams(param);
            b.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            b.setId(id);
            b.setBackground(getDrawable(R.drawable.button));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    in.putExtra("ItemID",v.getId());
                    in.putExtra("persister",p);
                    startActivity(in);
                }
            });
            registerForContextMenu(b);
            holder.addView(b);
            id++;
        }

        myCal = Calendar.getInstance();
        dateFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCal.set(Calendar.YEAR, year);
                myCal.set(Calendar.MONTH, month);
                myCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFrom();
            }
        };

        dateTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCal.set(Calendar.YEAR, year);
                myCal.set(Calendar.MONTH, month);
                myCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelTo();
            }
        };

        fromDate = findViewById(R.id.fromDate);
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(ListDataActivity.this,dateFrom,myCal.get(Calendar.YEAR),myCal.get(Calendar.MONTH),myCal.get(Calendar.DAY_OF_MONTH));
                dp.show();
            }
        });

        toDate = findViewById(R.id.toDate);
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(ListDataActivity.this,dateTo,myCal.get(Calendar.YEAR),myCal.get(Calendar.MONTH),myCal.get(Calendar.DAY_OF_MONTH));
                dp.show();
            }
        });

        imageView = findViewById(R.id.imageView);
        idd = new ImageDownloader(channel.getImage().getURL());
        Thread tt = new Thread(idd);
        tt.start();
        try {
            tt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(image);

        searchButton = findViewById(R.id.searchIcon);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                SearchDialog searchFrag = SearchDialog.newInstance(getResources().getString(R.string.searchterms),p);
                searchFrag.show(fm, "search_dialog");
            }
        });

        in = new Intent(this,InfoActivity.class);

        calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runCalculations("fromButton");
            }
        });

        northernly = findViewById(R.id.northernly);
        southernly = findViewById(R.id.southernly);
        westernly = findViewById(R.id.westernly);
        easternly = findViewById(R.id.easternly);
        deep = findViewById(R.id.greatDepth);
        shallow = findViewById(R.id.minDepth);
        largemag = findViewById(R.id.greatMag);

        globalIn = new Intent(this, Reloader.class);
        rec = new MyReciever();
        ifi = new IntentFilter("com.example.mcintee_michael_s1515941.RefreshData");
        registerReceiver(rec,ifi);
        if(!Reloader.isInstanceCreated()) {
            Thread t = new Thread() {
                public void run() {
                    startService(globalIn);
                }
            };
            t.start();
        }
    }

    private void updateData(Intent intent) {
        p = intent.getParcelableExtra("persister");
        channel = p.getChannel();
        holder.removeAllViews();
        id = 0;
        for(Item i:channel.getItems()) {
            Button b = new Button(this);
            b.setText(i.getDescription());
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.setMargins(8,5,8,5);
            b.setLayoutParams(param);
            b.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            b.setId(id);
            b.setBackground(getDrawable(R.drawable.button));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    in.putExtra("ItemID",v.getId());
                    in.putExtra("persister",p);
                    startActivity(in);
                }
            });
            registerForContextMenu(b);
            holder.addView(b);
            id++;
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        runCalculations("fromResume");
        registerReceiver(rec,ifi);
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(rec);
    }

    private void updateLabelFrom() {
        String myFormat = "E, dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        fromDate.setText(sdf.format(myCal.getTime()));
    }

    private void updateLabelTo() {
        String myFormat = "E, dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        toDate.setText(sdf.format(myCal.getTime()));
    }

    private void runCalculations(String calling) {

        if((fromDate.getText().length() == 0 || toDate.getText().length() == 0) && calling.equals("fromButton")) {
            Toast.makeText(ListDataActivity.this,getResources().getString(R.string.dateerror),Toast.LENGTH_SHORT).show();
            return;
        }

        if((fromDate.getText().length() == 0 || toDate.getText().length() == 0) && calling.equals("fromResume")) {
            //Don't raise toast on resume just on button press.
            return;
        }

        int counter = 0;
        double toplat = 0.0;
        double toplon = 0.0;
        double minlat = 0.0;
        double minlon = 0.0;
        double greatmag = 0.0;
        int deepest = 0;
        int shallowest = 0;

        LinkedList<Item> filteredList = new LinkedList<>();

        for(Item i : channel.getItems()) {
            if((i.getPubDate().after(parseDate(fromDate.getText().toString()))) && (i.getPubDate().before(parseDate(toDate.getText().toString())))) {
                filteredList.add(i);
            }
        }

        if(filteredList.size() == 0) {
            Toast.makeText(ListDataActivity.this,getResources().getString(R.string.nodata),Toast.LENGTH_SHORT).show();
            return;
        }

        for(Item i : filteredList) {
            if(counter == 0) {
                toplat = i.getLat();
                greatmag = i.getMagnitude();
                toplon = i.getLon();
                minlat = i.getLat();
                minlon = i.getLon();
                deepest = i.getDepth();
                shallowest = i.getDepth();
            }
            if(i.getLat() > toplat) {
                northid = counter;
                toplat = i.getLat();
            }
            if(i.getLat() < minlat) {
                southid = counter;
                minlat = i.getLat();
            }
            if(i.getLon() > toplon) {
                eastid = counter;
                toplon = i.getLon();
            }
            if(i.getLon() < minlon) {
                westid = counter;
                minlon = i.getLon();
            }
            if(greatmag < i.getMagnitude()) {
                largemagid = counter;
                greatmag = i.getMagnitude();
            }
            if(deepest < i.getDepth()) {
                deepid = counter;
                deepest = i.getDepth();
            }
            if(shallowest > i.getDepth()) {
                shallowid = counter;
                shallowest = i.getDepth();
            }
            counter++;
        }

        northernly.setText(getResources().getString(R.string.northernmost,toplat,filteredList.get(northid).getTitle()));
        southernly.setText(getResources().getString(R.string.southernmost, minlat, filteredList.get(southid).getTitle()));
        westernly.setText(getResources().getString(R.string.westernmost,toplon, filteredList.get(westid).getTitle()));
        easternly.setText(getResources().getString(R.string.easternmost,minlon,filteredList.get(eastid).getTitle()));
        deep.setText(getResources().getString(R.string.deepest, deepest , filteredList.get(deepid).getTitle()));
        shallow.setText(getResources().getString(R.string.shallow,shallowest,filteredList.get(shallowid).getTitle()));
        largemag.setText(getResources().getString(R.string.largemag, greatmag,filteredList.get(largemagid).getTitle()));
    }

    private Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("E, dd MMM yyyy").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class ImageDownloader implements Runnable {
        private String link;
        public ImageDownloader(String link) {
            this.link = link;
        }

        public void run() {
            image = loadImage(link);
        }

        public Bitmap loadImage(String link) {
            Bitmap channelLogo = null;
            InputStream stream;
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(link).openConnection();
                con.setDoInput(true);
                con.connect();
                stream = con.getInputStream();
                channelLogo = BitmapFactory.decodeStream(stream);
            } catch (Exception e) {
                Log.e("MyTag",e.toString());
            }
            return channelLogo;
        }
    }

    public class MyReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(ListDataActivity.this,getResources().getString(R.string.refresh),Toast.LENGTH_SHORT).show();
            updateData(intent);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        SpannableString titleformatted = new SpannableString(getResources().getString(R.string.selectid) + String.valueOf(v.getId()));
        titleformatted.setSpan(new TextAppearanceSpan(this, R.style.ContextFont), 0, (getResources().getString(R.string.selectid) + String.valueOf(v.getId())).length(),0);
        menu.setHeaderTitle(titleformatted);
        menu.add(0, v.getId(), 0, getResources().getString(R.string.infoscreen));
        menu.add(0, v.getId(), 0, getResources().getString(R.string.viewmap));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().toString().equals("Info Screen")) {
            in = new Intent(this,InfoActivity.class);
            in.putExtra("ItemID",item.getItemId());
            in.putExtra("persister",p);
            startActivity(in);
        } else if(item.getTitle().toString().equals("View on Map")) {
            in = new Intent(this,MapActivity.class);
            in.putExtra("ItemID",item.getItemId());
            in.putExtra("persister",p);
            startActivity(in);
        }
        return true;
    }
}
