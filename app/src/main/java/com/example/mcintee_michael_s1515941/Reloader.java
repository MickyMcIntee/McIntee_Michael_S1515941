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

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        globalIn = new Intent();
        globalIn.setAction(BROADCASTING);
    }

    @Override
    public void onDestroy() {
        instance = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Task().execute();
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    private class Task extends AsyncTask<String, Integer, Long>{
        final Object lock = new Object();

        @Override
        public Long doInBackground(String... strings) {
            while (true) {
                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";
                result = "";


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
                                item.setDesctription(parser.nextText());
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
                sendBroadcast(globalIn);
                synchronized (this) {
                        try {
                            this.wait(300000);
                        }
                        catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
        }
    }
}
