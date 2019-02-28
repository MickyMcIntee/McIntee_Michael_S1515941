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
    private MyReceiver rec;
    private IntentFilter ifi;
    private ImageDownloader idd;
    private Bitmap image;
    private Thread t;

    /**
     * The oncreate method is part of the activity lifecycle and is ran on the creation
     * of the new activity. It is normally used to connect graphical elements to java code
     * and to add things like event handlers and perform necessary design changes using java code/
     * @param savedInstanceState The bundle passed across from previous activities on certain things
     *                           like resumes from pauses etc.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        holder = findViewById(R.id.selectionDisplay); //Set up the graphical element and connect the view selectionDisplay to the java variable of holder.
        in = getIntent();                               //Get intents if it exists passed from previous activities saved with an intent.
        p = in.getParcelableExtra("persister"); //Get the persister from the intent object.
        channel = p.getChannel();                       //Get the channel from the persister.
        id = 0;                                         //set id application to give buttons an id programmatically.
        for(Item i:channel.getItems()) {                //For each item in the channel array list
            Button b = new Button(this);        //Create a button in the context of list data activity.
            b.setText(i.getDescription());              //Set the text of the button to the current items description.
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.setMargins(8,5,8,5);
            b.setLayoutParams(param);                   //Apply the above layout parameters to the buttons programmatically.
            b.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20); //Set size of the button text to Dp 20
            b.setId(id);                                //Set the id of the program generated buttons to the current incremented id.
            b.setBackground(getDrawable(R.drawable.button));    //Set the background of the button to a drawable from the res.
            b.setOnClickListener(new View.OnClickListener() {   //Set the listener anonymously for each button.

                /**
                 * The onclick event is mandatory through the onclicklistener interface and the code inside
                 * is run when the view is clicked. as it is anonymous the on click event belongs to the
                 * current incremented button view only thus we don't need to check the calling view.
                 * @param v the v in which the click event was called against as mentioned above
                 *          We can ignore this because this is an anonymous listener. We do need to get the id
                 *          though to pass it in the intent for the selected object.
                 */
                @Override
                public void onClick(View v) {
                    in.putExtra("ItemID",v.getId());    //If the button is clicked put the items id in the intent.
                    in.putExtra("persister",p);         //Put the populated persister to be used in the next activity.
                    startActivity(in);                        //Start the activity with the intent. This id passed over will be used to pull the selected item from the channel.
                }
            });
            registerForContextMenu(b);  //Add a context menu to the button which allows us to long click and select if we want to go straight to map or info first.
            holder.addView(b);          //Add the program generated button to the view.
            id++;                       //Increment the id number to be applied to the next button.
        }

        myCal = Calendar.getInstance(); //Get a calendar object.
        dateFrom = new DatePickerDialog.OnDateSetListener() { //Set date from to a datepicker with a listener.

            /**
             * The on date set listener is part of an interface which states that an ondateset method is required.
             * This is used to call code when a date is selected from a date picker dialog.
             * @param view The view from which the listener was called, as this is anonymous we don't
             *             need to use this to check.
             * @param year The year parameter which will be taken from the date picker and applied to
             *             the new calendar.
             * @param month The month parameter which will be taken from the date picker and applied to
             *              the new calendar.
             * @param dayOfMonth the dayofmonth parameter which will be taken from the date picker and
             *                   applied to the new calendar.
             */
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCal.set(Calendar.YEAR, year);                 //Set the value of the new calendars year to the year parameter.
                myCal.set(Calendar.MONTH, month);               //Set the value of the new calendars month to the month parameter.
                myCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);   //Set the value of the new calendars day of month to the day of month parameter.
                myCal.set(Calendar.HOUR,0);                     //Start from midnight.
                myCal.set(Calendar.MINUTE,0);
                myCal.set(Calendar.SECOND,0);
                updateLabelFrom();
            }
        };

        dateTo = new DatePickerDialog.OnDateSetListener() {

            /**
             * The on date set listener is part of an interface which states that an ondateset method is required.
             * This is used to call code when a date is selected from a date picker dialog.
             * @param view The view from which the listener was called, as this is anonymous we don't
             *             need to use this to check.
             * @param year The year parameter which will be taken from the date picker and applied to
             *             the new calendar.
             * @param month The month parameter which will be taken from the date picker and applied to
             *              the new calendar.
             * @param dayOfMonth the dayofmonth parameter which will be taken from the date picker and
             *                   applied to the new calendar.
             */
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCal.set(Calendar.YEAR, year);                 //Set the value of the new calendars year to the year parameter.
                myCal.set(Calendar.MONTH, month);               //Set the value of the new calendars month to the month parameter.
                myCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);   //Set the value of the new calendars day of month to the day of month parameter.
                myCal.set(Calendar.HOUR,23);                    //End at last point of day.
                myCal.set(Calendar.MINUTE,59);
                myCal.set(Calendar.SECOND,59);
                updateLabelTo();
            }
        };

        fromDate = findViewById(R.id.fromDate);
        fromDate.setOnClickListener(new View.OnClickListener() {

            /**
             * The onclicklistener interface defines that the onclick event must be defined. This is done
             * anonymously so is used to define what happens when the above view is clicked. In this case it
             * creates a date picker object and shows it to the user.
             * @param v The view from which the on click event was called. Not necessary as this is
             *          anonymous and only relevant to the view above.
             */
            @Override
            public void onClick(View v) {
                //Create date picker, to be saved to the date from and with a date of the current date.
                DatePickerDialog dp = new DatePickerDialog(ListDataActivity.this,dateFrom,myCal.get(Calendar.YEAR),myCal.get(Calendar.MONTH),myCal.get(Calendar.DAY_OF_MONTH));
                dp.show();
            }
        });

        toDate = findViewById(R.id.toDate);
        toDate.setOnClickListener(new View.OnClickListener() {
            /**
             * The onclicklistener interface defines that the onclick event must be defined. This is done
             * anonymously so is used to define what happens when the above view is clicked. In this case it
             * creates a date picker object and shows it to the user.
             * @param v The view from which the on click event was called. Not necessary as this is
             *          anonymous and only relevant to the view above.
             */
            @Override
            public void onClick(View v) {
                //Create date picker, to be saved to the date to and with a date of the current date.
                DatePickerDialog dp = new DatePickerDialog(ListDataActivity.this,dateTo,myCal.get(Calendar.YEAR),myCal.get(Calendar.MONTH),myCal.get(Calendar.DAY_OF_MONTH));
                dp.show();
            }
        });

        imageView = findViewById(R.id.imageView);
        idd = new ImageDownloader(channel.getImage().getURL()); //Create a new image downloader object which is a form of a runnable. Nested class can be found further in code.
        Thread tt = new Thread(idd); //Create a new thread to run the runnable.
        tt.start(); //Start running the thread to download the image.

        t = new Thread(new Runnable() { //Start a new thread to check if the image has been populated yet.

            /**
             * The runnable class which implements the runnable interface defines that a run method must
             * exist, this is what runs as part of the runnable on another thread.
             */
            @Override
            public void run() {
                while(image == null) {  //While the image is not there.
                    try {
                        Thread.sleep(2000);             //Sleep for 2 seconds.
                    } catch (InterruptedException e) {
                        e.printStackTrace();                    //If can't sleep print error.
                    }
                }
                runOnUiThread(new Runnable() {  //Once loop is broken then image is populated. Run the below runnable on the main ui thread which must be used to make activity layout changes.

                    /**
                     * The runnable class which implements the runnable interface defines that a run method must
                     * exist, this is what runs as part of the runnable on another thread.
                     */
                    @Override
                    public void run() {
                        imageView.setImageBitmap(image);    //Set the value of the image view to hold the logo to the bitmat of the image created in the first thread.
                    }
                });
            }
        });
        t.start(); //Start the second thread.
        /*The reason for the second thread is to ensure that the ui is not clogged up waiting for the image to download and the user can interact
        * without waiting on the image downloading. Then once the image finally downloads it makes the changes
        * using the main ui thread.
        */

        searchButton = findViewById(R.id.searchIcon);
        searchButton.setOnClickListener(new View.OnClickListener() {

            /**
             * The onclicklistener interface defines that the onclick event must be defined. This is done
             * anonymously so is used to define what happens when the above view is clicked. In this case it
             * creates a new fragment which is a custom search dialog which extends the fragment superclass.
             * @param v The view from which the on click event was called. Not necessary as this is
             *          anonymous and only relevant to the view above.
             */
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager(); //Create a new fragment manager.
                SearchDialog searchFrag = SearchDialog.newInstance(getResources().getString(R.string.searchterms),p); //Create a new search dialog which is a form of fragment.
                searchFrag.show(fm, "search_dialog"); //Show the new fragment which is a sort of custom dialog that means date pickers can still be used.
            }
        });

        in = new Intent(this,InfoActivity.class);       //Set an intent to move to the info activity.

        calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {

            /**
             * The onclick even is mandatory from the implementation of the onClickListener method.
             * it is what is called when the view is clicked and in this case it's anonymous
             * instantiation means it only belongs to the calculateButton view.
             * @param v the calling view, not really necessary as this on click event only belongs
             *          to the calculate button.
             */
            @Override
            public void onClick(View v) {
                runCalculations("fromButton"); //Run calculations informing the method that the call was from a button push.
            }
        });

        northernly = findViewById(R.id.northernly); //Making connections from xml to java etc.
        southernly = findViewById(R.id.southernly);
        westernly = findViewById(R.id.westernly);
        easternly = findViewById(R.id.easternly);
        deep = findViewById(R.id.greatDepth);
        shallow = findViewById(R.id.minDepth);
        largemag = findViewById(R.id.greatMag);

        globalIn = new Intent(this, Reloader.class); //Create an intent to create reloader class.
        rec = new MyReceiver(); //Create a receiver which is a subclass of broadcast Receiver and is a nested class also further down this code.
        ifi = new IntentFilter("com.example.mcintee_michael_s1515941.RefreshData"); //Intent filter which is sort of like listen on this channel.
        registerReceiver(rec,ifi);                  //Register the Receiver using the broadcast Receiver below and the intent filter to listen on that channel.
        //The above picks up changes from a service which reloads the data in the background and informs of changes. It's the subclass of service, the reloader which is a class in this project.
        if(!Reloader.isInstanceCreated()) {     //If the service is not currently running
        Thread t = new Thread() {               //Create a background thread to run indefinitely.
                public void run() {             //Run (not as runnable)
                    startService(globalIn);     //Start the service using the current context and the reloader class.
                }
            };
            t.start();                          //Start the thread which will create the service and run it's code in the background (continuous wait and check and download new data).
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

    /**
     * The on resume method is part of the activity lifecycle and takes place when the user comes back
     * in to the app from it being on pause. This method re registers the receiver as it is stopped
     * on pause to ensure that the updates aren't running when the user is off the app. This method
     * also calls the run calculations which re-runs the calculations for the date range the user
     * as put in, this is to ensure that if they've ran the calculations already the data still shows.
     * This could have also been done with bundles storing each string of information on saved instance
     * and loading it back in if not null but I decided to go this way.
     */
    @Override
    public void onResume() {
        super.onResume();
        runCalculations("fromResume");
        registerReceiver(rec,ifi);
    }

    /**
     * The on pause event is part of the activity lifecycle and runs when the activity is paused due
     * to the user clicking home to go off the app or for any other reason. This method
     * unregisters the receiver to ensure that if the user is off the app they don't keep receiving
     * updates about the data refreshing.
     */
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(rec);
    }

    /**
     * The update label from method is responsible for setting the text of the from date to the value
     * of the text taken from the calendar object. It uses the same format that the data has for it's
     * dates.
     */
    private void updateLabelFrom() {
        String myFormat = "E, dd MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        Log.e("Date",myCal.getTime().toString()); //Used to find out that it's using current time.
        fromDate.setText(sdf.format(myCal.getTime()));
    }

    /**
     * This method is used to set the UI to date text edit to the value of the calendar which is set
     * from the date picker. This means users cannot enter dates in the incorrect format because
     * the date picker makes it in the correct format.
     */
    private void updateLabelTo() {
        String myFormat = "E, dd MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        Log.e("Date",myCal.getTime().toString()); //Used to find out that it's using the current time.
        toDate.setText(sdf.format(myCal.getTime()));
    }

    /**
     * This run calculations method takes a string calling which defines if it is called from a button
     * push (send an alert of empty dates) or coming from a resume (don't make a toast).
     * @param calling The string value of where the method was called from.
     */
    private void runCalculations(String calling) {

        if((fromDate.getText().length() == 0 || toDate.getText().length() == 0) && calling.equals("fromButton")) { //If dates are missing and called from button.
            Toast.makeText(ListDataActivity.this,getResources().getString(R.string.dateerror),Toast.LENGTH_SHORT).show(); //Raise toast.
            return;
        }

        if((fromDate.getText().length() == 0 || toDate.getText().length() == 0) && calling.equals("fromResume")) { //If dates are missing and called from resume.
            //Don't raise toast on resume just on button press.
            return;
        }

        int counter = 0;            //Set placeholder values.
        double toplat = 0.0;        //Set placeholder values.
        double toplon = 0.0;        //Set placeholder values.
        double minlat = 0.0;        //Set placeholder values.
        double minlon = 0.0;        //Set placeholder values.
        double greatmag = 0.0;      //Set placeholder values.
        int deepest = 0;            //Set placeholder values.
        int shallowest = 0;         //Set placeholder values.

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
