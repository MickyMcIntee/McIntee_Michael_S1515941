package com.example.mcintee_michael_s1515941;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
 * Data loader - this class is responsible for loading in the data in a new thread, unlike the reloader class which
 * uses an async task. This is to show both ways of ensuring network operations happen on a new thread
 * and don't interrupt flow of control by the user.
 * @author michaelmcintee
 * @version 1.0
 * StudentID - S1515941
 * Programme - BSc SDfB
 */
public class DataLoader extends AppCompatActivity {

    private String result;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml"; //The xml data link
    private Channel channel;

    /**
     * When a data loader is used it creates and returns a new thread using a task nested class implementing the runnable
     * interface allowing this network operation to be run on another thread, and not the main thread.
     */
    public DataLoader() {
        returnThread();
    }

    /**
     * This method returns a thread which is used to load the new task object which sets the channel of this class
     * to a pull of the xml data using the xml parse.
     * @return Return the thread object running the runnable Task class.
     */
    public Thread returnThread() {
        return new Thread(new Task(urlSource));
    }

    /**
     * The get loaded channel method which will start a new thread with the nested task class to create a populated
     * channel object. It ensures that the thread is complete before running the channel to ensure that
     * an empty channel is not returned.
     * @return the populated channel object with all the items objects.
     */
    public Channel getLoadedChannel() {
        Thread t = new Thread(new Task(urlSource));     //Create new thread
        t.start();                                      //Start the thread
        while(channel == null) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return channel;                                 //Return the populated channel object.
    }

    /**
     * Nested task class which implements runnable allowing this class to be used to run something on
     * a new thread separate from the main ui thread. This ensures that the data loading is not holding up
     * the main methods.
     * @see java.lang.Runnable
     */
    private class Task implements Runnable
    {
        private String url;

        /**
         * Constructor which sets the url to the string passed in on construction.
         * @param aurl The url for the data passed in as part of the construction.
         */
        public Task(String aurl)
        {
            url = aurl;         //Set the value of the url to the value passed in on construction.
        }

        /**
         * The run method which is required to be overridden from the runnable class. This is what will run
         * on a separate thread when a thread is created using the task class.
         */
        @Override
        public void run()
        {

            URL aurl;                             //Create a url object.
            URLConnection yc;                     //Create a url connection object.
            BufferedReader in = null;             //Create a buffered reader.
            String inputLine = "";                //Create an input line string.
            result = "";                          //Create an empty result string.


            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);                                                    //Set the url object to the value of a URL object created from the string url.
                yc = aurl.openConnection();                                             //Open a connection using the url object.
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));    //Create a buffered reader from the input stream from the connection object.
                in.readLine();                                                          //Skip the first line.
                while ((inputLine = in.readLine()) != null)                             //While there are more lines, read a line in.
                {
                    result = result + inputLine;                                        //Add the line to the result string.
                    Log.e("MyTag",inputLine);
                }
                in.close();                                                             //Once complete close the connection.
            }
            catch (IOException ae)                                                      //IO exception, i.e. can't access the file etc.
            {
                Log.e("MyTag", "ioexception");
                ae.printStackTrace();                                                   //Print the error returned from the exception to log.
            }

            channel = parseXML(result);                                                 //Set the channel object to the return of the parseXML method.

        }

        /**
         * The parseXML string which is responsible for parsing the data, putting it in to objects and in to the channel then returning the filled channel.
         * @param data the big string with all the data from the xml in it.
         * @return The populated channel object.
         */
        public Channel parseXML(String data) {

            Item item = null;               //Create item object.
            Channel myChannel = null;       //Create channel object.
            Image myImage = null;           //Create image object.

            try {

                XmlPullParserFactory myFact = XmlPullParserFactory.newInstance();       //Create instance
                myFact.setNamespaceAware(true);                                         //XML Provides support for name spaces, must be done for geo namespace.
                XmlPullParser parser = myFact.newPullParser();                          //Create pull parser from enabled factory.
                parser.setInput(new StringReader(data));                                //Set the input of the parser to a string reader object with the data string.
                int event = parser.getEventType();                                      //Get the type of event of the current purse.
                String currentType = null;                                              //Set current type string to null, used to differentiate titles, descriptions, links etc from items, channels and images etc.

                while (event != XmlPullParser.END_DOCUMENT) {                               //While not the end of the document.
                    if (event == XmlPullParser.START_TAG) {                                 //If it is the start tag.
                        if (parser.getName().equalsIgnoreCase("channel")) {     // Check if the parsed tag is a channel.
                            myChannel = new Channel();                                      //Set mychannel to a new channel instance.
                            currentType = "channel";                                        //Set current type to a channel.
                        } else if (parser.getName().equalsIgnoreCase("title") && currentType == "channel") {    //If the parser is a title tag and it is a channel type
                            myChannel.setTitle(parser.nextText());                          //Set the title to the text from the tag
                        } else if (parser.getName().equalsIgnoreCase("link") && currentType == "channel") {
                            myChannel.setLink(parser.nextText());
                        } else if (parser.getName().equalsIgnoreCase("description") && currentType == "channel") {
                            myChannel.setDescription(parser.nextText());
                        } else if (parser.getName().equalsIgnoreCase("language")) {
                            myChannel.setLanguage(parser.nextText());
                        } else if (parser.getName().equalsIgnoreCase("lastBuildDate")) {
                            myChannel.setLastBuildDate(parser.nextText());
                        }  else if (parser.getName().equalsIgnoreCase("image")) { //If the parser is an image tag
                            myImage = new Image();                                              //Set the image variable to an instance of image
                    currentType = "image";                                                     //Set the type to image for differentiation.
                        } else if (parser.getName().equalsIgnoreCase("title") && currentType == "image") {
                            myImage.setTitle(parser.nextText());
                        } else if (parser.getName().equalsIgnoreCase("url") && currentType == "image") {
                            myImage.setURL(parser.nextText());
                        } else if (parser.getName().equalsIgnoreCase("link") && currentType == "image") {
                            myImage.setLink(parser.nextText());
                        } else if (parser.getName().equalsIgnoreCase("item")) {   //If the parser is an Item tag.
                            item = new Item();                                                //Set item variable to an instance of item.
                            currentType = "item";                                             //Set the current type to item for differentiation.
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
                        } else if (parser.getPrefix() != null) {        //If namespace is not null
                            if (parser.getName().equalsIgnoreCase("lat")) { //Get namespace if lat
                                item.setLat(Double.parseDouble(parser.nextText()));     //Get lat
                            } else {                                                    //Else if namespace is lon
                                item.setLon(Double.parseDouble(parser.nextText()));     //Get lon.

                            }
                        }
                    } else if (event == XmlPullParser.END_TAG) {                        //If end tag
                        if (parser.getName().equalsIgnoreCase("image")) {   //if is image tag finish creating image
                            myChannel.setImage(myImage);                                //Store image to channel
                        } else if (parser.getName().equalsIgnoreCase("item")) {     //If item end tag finish creating item.
                            myChannel.addItem(item);                                            //Add item to channel
                        } else if (parser.getName().equalsIgnoreCase("channel")) {  //If channel end tag.
                            //Do nothing as channel will be complete.
                        }
                    }
                    event = parser.next();      //Get next type from parser.
                }
            } catch (XmlPullParserException xme) {  //Catch parse errors.
                Log.e("myTag",xme.toString());
            } catch (IOException ioe) {             //Catch io exceptions like file not found etc.
                Log.e("myTag", ioe.toString());
            }
            return myChannel;    //Return the populated channel object.
        }
    }
}
