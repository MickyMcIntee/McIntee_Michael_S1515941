package com.example.mcintee_michael_s1515941;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * The reloader class creates a new service which exists independent of the activities. This means that
 * if not correctly handled in on pause and on resume the broadcast listener and the service can
 * continue to run in the background.
 * @author michaelmcintee
 * @version 1.0
 * @see Service
 * StudentID - S1515941
 * Programme - BSc SDfB
 */
public class Reloader extends Service {

    private static final String TAG = "Reloader";
    public static final String BROADCASTING = "com.example.mcintee_michael_s1515941.RefreshData";
    private static Reloader instance = null;
    public Intent globalIn;
    private String result;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private Persister p;

    /**
     * The is instance created method is checked when creating a new object. If the instance does not exist
     * it will allow creation.
     * @return The boolean value if instance is not null.
     */
    public static boolean isInstanceCreated() {
        return instance != null;
    }

    /**
     * Part of the service extension and binds the service to the intent.
     * @param intent The intent the service should be bound to.
     * @return an IBinder object describing the bind.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * The on create method which is part of the activity lifecycle which is called when the activity
     * is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;                    //Set the instance.
        globalIn = new Intent();            //Set the intent to a global intent.
        globalIn.setAction(BROADCASTING);   //Set the channel of the intent to the value of broadcasting.
    }

    /**
     * When destroyed reset the instance to null. Means thats only 1 can exist at a time and that
     * 1 is open to a new creation based on the last one being destroyed.
     */
    @Override
    public void onDestroy() {
        instance = null;
    }

    /**
     * The on start command is an extension of service and is responsible for creating the new task
     * object which is a background asynchronous task to perform refreshes. This is a nest class
     * described below.
     * @param intent The intent the command is being started against.
     * @param flags The flags for settings of the intent.
     * @param startId The id of the started service.
     * @return return an integer of successful start.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Task().execute();
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    /**
     * The task class extends AsyncTask and is used to perform the reloading of data in the background using
     * a service. When the reload is complete it sends a broadcast that broadcast receivers pick up and
     * depending on what activity is open the receivers will reload the data or raise a toast informing
     * of updates.
     */
    private class Task extends AsyncTask<String, Integer, Long>{ //<parameters of the task, progress value of the task, Result of the task>

        final Object lock = new Object(); //Create new lock of type object.

        /**
         * Extension of the asynctask class this is the method that will run in the background.
         * It has a long value which is the result of the background task, it uses the strings
         * parameters which isn't in use here.
         * @param strings The value of parameters for the background task.
         * @return The long value of result.
         */
        @Override
        public Long doInBackground(String... strings) {
            while (true) { //Forever (true always true).

                //The following code does the same as the initial load in DataLoader but it is done in the background.
                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";
                result = "";

                //Load all lines of data from the input stream using buffers.
                Log.e("MyTag", "in run");
                try {
                    Log.e("MyTag", "in try");
                    aurl = new URL(urlSource);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                    in.readLine();
                    while ((inputLine = in.readLine()) != null) {
                        result = result + inputLine;
                        Log.e("MyTag", inputLine);
                    }
                    in.close();
                } catch (IOException ae) {
                    Log.e("MyTag", "ioexception");
                    ae.printStackTrace();
                }

                Item item = null;
                Channel myChannel = null;
                Image myImage = null;

                //Parse the data using a pull parser as described in the DataLoader
                try {

                    XmlPullParserFactory myFact = XmlPullParserFactory.newInstance(); //Create instance
                    myFact.setNamespaceAware(true); //XML Provides support for name spaces.
                    XmlPullParser parser = myFact.newPullParser();
                    parser.setInput(new StringReader(result));
                    int event = parser.getEventType();
                    String currentType = null;

                    while (event != XmlPullParser.END_DOCUMENT) {
                        if (event == XmlPullParser.START_TAG) {
                            if (parser.getName().equalsIgnoreCase("channel")) {
                                myChannel = new Channel();
                                currentType = "channel";
                            } else if (parser.getName().equalsIgnoreCase("title") && currentType == "channel") {
                                myChannel.setTitle(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("link") && currentType == "channel") {
                                myChannel.setLink(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("description") && currentType == "channel") {
                                myChannel.setDescription(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("language")) {
                                myChannel.setLanguage(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("lastBuildDate")) {
                                myChannel.setLastBuildDate(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("image")) {
                                myImage = new Image();
                                currentType = "image";
                            } else if (parser.getName().equalsIgnoreCase("title") && currentType == "image") {
                                myImage.setTitle(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("url") && currentType == "image") {
                                myImage.setURL(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("link") && currentType == "image") {
                                myImage.setLink(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("item")) {
                                item = new Item();
                                currentType = "item";
                            } else if (parser.getName().equalsIgnoreCase("title") && currentType == "item") {
                                item.setTitle(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("description") && currentType == "item") {
                                item.setDescription(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("link") && currentType == "item") {
                                item.setLink(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("pubDate")) {
                                item.setPubDate(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("category")) {
                                item.setCategory(parser.nextText());
                            } else if (parser.getPrefix() != null) {
                                if (parser.getName().equalsIgnoreCase("lat")) {
                                    item.setLat(Double.parseDouble(parser.nextText()));
                                } else {
                                    item.setLon(Double.parseDouble(parser.nextText()));

                                }
                            }
                        } else if (event == XmlPullParser.END_TAG) {
                            if (parser.getName().equalsIgnoreCase("image")) {
                                myChannel.setImage(myImage);
                            } else if (parser.getName().equalsIgnoreCase("item")) {
                                myChannel.addItem(item);
                            } else if (parser.getName().equalsIgnoreCase("channel")) {
                                //Log.e("Info", Integer.toString(myChannel.getItems().size()));
                            }
                        }
                        event = parser.next();
                    }
                } catch (XmlPullParserException xme) {
                    Log.e("myTag", xme.toString());
                } catch (IOException ioe) {
                    Log.e("myTag", ioe.toString());
                }
                p = new Persister(myChannel);
                globalIn.putExtra("persister", p);
                sendBroadcast(globalIn); //Send broadcast of updated data for receivers to pick up, they get the data from the parcelled persister in the global intenet.
                synchronized (this) { //Using the lock lock the object and wait, this means nothing else can work on the object which is required by wait.
                        try {
                            this.wait(300000); //Wait 5 mins before restarting the download and parse.
                        }
                        catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
        }
    }
}
