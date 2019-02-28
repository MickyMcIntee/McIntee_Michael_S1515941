/*  Starter project for Mobile Platform Development in Semester B Session 2018/2019
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Michael McIntee
// Student ID           S1515941
// Programme of Study   BSc SDfB (PTA)
//

// Update the package name to include your Student Identifier
package com.mcintech.mcintee_michael_s1515941;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author michaelmcintee
 * @version 1.0
 * @see OnClickListener
 * S1515941
 * BSc SDfB
 */
public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private TextView rawDataDisplay;
    private Button startButton;
    Intent i;
    private Channel channel;
    Persister p;

    /**
     * The on create instance is called when the activity starts and sets up the graphical user interface of the activity.
     * It's also usually used to add listeners to buttons.
     * @param savedInstanceState this is the stare brought in from the previous activity which is usually a blank stage and intents should be used to retrieve data across activities.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        rawDataDisplay = findViewById(R.id.rawDataDisplay); //Get reference to XML object.
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
    }

    /**
     * The onclick event is called when a button is pressed, this is the non anonymous way to do it.
     * Issue here is if another button was added the same would be called below and the button that was clicked has to be
     * specified.
     * @param aview The view or calling object i.e. the button and can be tested against the button pushed to perform tasks.
     */
    public void onClick(View aview)
    {
        startProgress(); //Call the start function.
    }

    /**
     * The start progress function runs the rest of the app. It creates a dataloader which loads the data in
     * to the channel object and creates a persister object to hold the data as a parcelable object. This
     * intent is created and the persister is passed through it and then the list data activity is called.
     */
    public void startProgress() {
        // Run network access on a separate thread;
        DataLoader dl = new DataLoader();
        channel = dl.getLoadedChannel();
        p = new Persister(channel);
        i = new Intent(MainActivity.this, ListDataActivity.class);
        i.putExtra("persister",p);
        startActivity(i); //Start the activity in the intent.
    }
}