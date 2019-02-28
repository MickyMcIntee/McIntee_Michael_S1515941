package com.mcintech.mcintee_michael_s1515941;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * The search dialog class is a custom dialog box using the dialog fragment superclass. This allows
 * something that appears to be a dialog pop up in front of our main activity but the fragment
 * itself can still raise dialogs. This had to be done as we want the date fields of this activity
 * to be date picker dialogs which are not possible if this itself was a dialog and not a fragment.
 * @author michaelmcintee
 * @version 1.0
 * @see DialogFragment
 * StudentID - S1515941
 * Programme - BSc SDfB
 */
public class SearchDialog extends DialogFragment {
    private Spinner category;
    private EditText origDateFrom;
    private EditText origDateTo;
    private EditText magnitude;
    private EditText location;
    private EditText edittitle;
    private EditText depth;
    private Button searchButton;
    private Button backButton;
    private Calendar cal;
    private Channel channel;
    private Channel filchannel;
    private Intent in;
    private Persister p;

    /**
     * Blank constructor as instance created is handled below.
     */
    public SearchDialog() {

    }

    /**
     * The new instance method creates the fragment accepting the required fields that need to be used
     * in the dialog like the persister to be searched and a title to be applied to the fragment.
     * @param title The value of the title to be applied to the fragment.
     * @param persister The value of the persister to be used in search.
     * @return Return the object so that show can be called.
     */
    public static SearchDialog newInstance(String title,Persister persister) {
        SearchDialog myfragment = new SearchDialog(); //Create instance of the fragment.
        Bundle args = new Bundle(); //Create a bundle of arguments.
        args.putString("title", title); //Put title in the bundle.
        args.putSerializable("channel",persister.getChannel()); //Put the channel in the bundle.
        args.putParcelable("persister",persister);  //Put the persister in the bundle.
        myfragment.setArguments(args); //Set arguments of the fragment to the populated bundle.
        return myfragment; //Return the search dialog fragment with the arguments.
    }

    /**
     * On create view which is an method extension of the dialog fragment class this is responsible for inflating the view on
     * the current activity. It requires an inlater, a container in which the view should be inflated on and a bundle
     * of arguments.
     * @param inflater The inflator object to inflate the fragment.
     * @param container The container to hold the fragment.
     * @param savedInstanceState The bundle of arguments.
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_dialog, container);
        return view;
    }

    /**
     * The method which is called when the view has been created. It's an extension of the dialog fragment class
     * and is responsible for making the connections between xml and java and also making any changes to the
     * dialog and adding on click listeners.
     * @param view The create view or dialog box.
     * @param savedInstanceState The bundle of arguments.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setLayoutParams(new LinearLayout.LayoutParams(500,500,1)); //Set layout of dialog

        String title = getArguments().getString("title", "Enter Search: "); //Get the text from arguments bundle.
        channel = (Channel) getArguments().getSerializable("channel"); //Get the channel from arguments bundle.
        p = getArguments().getParcelable("persister"); //Get persister from arguments bundle.

        searchButton = view.findViewById(R.id.dialogButtonSearch); //Connect java to xml etc.
        backButton = view.findViewById(R.id.dialogButtonBack);
        category = view.findViewById(R.id.editCat);
        magnitude = view.findViewById(R.id.editMag);
        location = view.findViewById(R.id.editLocation);
        depth = view.findViewById(R.id.editDepth);
        edittitle = view.findViewById(R.id.editTitle);

        //For each distinct item category of the channel.
        List<String> array = new ArrayList<>();
        for(Item i : channel.getItems()) {

            if(!array.contains(i.getCategory())) {
                array.add(i.getCategory());
            }
        }

        //Create an adapter using the spin layout from xml to adjust style. Populate with distinct array.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(super.getContext(),R.layout.spin, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter); //Set the spinners adapter to the newly created adapter.

        backButton.setOnClickListener(new View.OnClickListener() { //On back button click.
            @Override
            public void onClick(View v) {
                SearchDialog.super.dismiss(); //Dismiss the dialog.
            }
        });

        //Set up calendars for date selection.
        cal = Calendar.getInstance(); //Set to now.
        origDateFrom = view.findViewById(R.id.editOrigFrom);

        final DatePickerDialog.OnDateSetListener adf = new DatePickerDialog.OnDateSetListener() {

            /**
             * On date set listener which kicks off when a date is selected from the picker.
             * @param view The view to be adjusted.
             * @param year The year value.
             * @param month The month value.
             * @param dayOfMonth The day of month picker.
             */
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFormFrom();
            }
        };

        origDateFrom.setOnClickListener(new View.OnClickListener() {

            /**
             * The onclick event has a calling view but as this is anonymous we don't use it.
             * This opens a date picker dialog with the on date set listener above. This sets the value of the
             * picker to the value of the calendar which is set to now.
             * @param v The view in which the event was raised.
             */
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(getActivity(),adf,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                dp.show();
            }
        });

        origDateTo = view.findViewById(R.id.editOrigTo);
        final DatePickerDialog.OnDateSetListener adt = new DatePickerDialog.OnDateSetListener() {

            /**
             * On date set listener which kicks off when a date is selected from the picker.
             * @param view The view to be adjusted.
             * @param year The year value.
             * @param month The month value.
             * @param dayOfMonth The day of month picker.
             */
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFormTo();
            }
        };

        origDateTo.setOnClickListener(new View.OnClickListener() {

            /**
             * The onclick event has a calling view but as this is anonymous we don't use it.
             * This opens a date picker dialog with the on date set listener above. This sets the value of the
             * picker to the value of the calendar which is set to now.
             * @param v The view in which the event was raised.
             */
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(getActivity(),adt,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                dp.show();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {

            /**
             * The onclick event has a calling view but as this is anonymous we don't use it.
             * This runs the pipe and filter to filter out data based on the search criteria entered
             * once pipe filter is complete the filtered channel is checked for data. If there is none
             * raise toast else move to a new search results activity using the package of the filtered channel
             * and the full persister.
             * @param v The view in which the event was raised.
             */
            @Override
            public void onClick(View v) {
                filchannel = pipeFilter();
                if (filchannel.getItems().isEmpty()) {
                    Toast.makeText(getActivity(),"The search yields no results.",Toast.LENGTH_SHORT).show();
                } else {
                    in = new Intent(getActivity(),SearchResults.class);
                    in.putExtra("filtered",filchannel);
                    in.putExtra("persister",p);
                    startActivity(in);
                }
            }
        });

        getDialog().setTitle(title); //Get the dialog and set the title to the value of title.
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //Set the background to invisible to hide the dialog bg and to create custom background in xml.
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); //Raise the keyboard on the dialog on selections.
    }

    /**
     * Updates the date of the origin date from text to the value of the calendar set by the date
     * picker selection.
     */
    public void updateFormFrom() {
        String myFormat = "E, dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        origDateFrom.setText(sdf.format(cal.getTime()));
    }

    /**
     * Updates the date of the origin date to text to the value of the calendar set by the date
     * picker selection.
     */
    public void updateFormTo() {
        String myFormat = "E, dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        origDateTo.setText(sdf.format(cal.getTime()));
    }

    /**
     * The pipe and filter method takes each item from a clone of the channels item linked list and puts
     * it through a series of not checks, where if any are not the inputted search terms the check
     * is set to false for the item and it is not put in to the to remove list which will be the list
     * of elements that meet the criteria.
     * @return
     */
    public Channel pipeFilter() {
        Channel filterc = new Channel();
        filterc.setItems((LinkedList<Item>) channel.getItems().clone());

        LinkedList<Item> toRemove = new LinkedList<>();

        //For each item each check follows the same sort of structure.
        for(Item i : filterc.getItems()) {
            boolean check = true; //Set the items check to true, put in list.
            if(!category.getSelectedItem().toString().isEmpty()) { //If there is no selection on this field then move on.
                if (!i.getCategory().contains(category.getSelectedItem().toString())) { //If there is a selection and the items category is not equal to the input category then
                    check = false; //Set check to false meaning this element does not meet the criteria.
                }
            }

            if(!origDateFrom.getText().toString().isEmpty() && !origDateTo.getText().toString().isEmpty()) {
                if(i.getOriginDate().before(parseDate(origDateFrom.getText().toString() + " 00:00:00")) || i.getOriginDate().after(parseDate(origDateTo.getText().toString() + " 23:59:59"))) {
                    check = false;
                }
            } else if(!origDateFrom.getText().toString().isEmpty()) {
                if(i.getOriginDate().before(parseDate(origDateFrom.getText().toString() + " 00:00:00"))) {
                    check = false;
                }
            } else if(!origDateTo.getText().toString().isEmpty()) {
                if(i.getOriginDate().after(parseDate(origDateTo.getText().toString() + " 23:59:59"))) {
                    check = false;
                }
            }

            if(!magnitude.getText().toString().isEmpty()) {
                if(i.getMagnitude() != Double.parseDouble(magnitude.getText().toString())) {
                    check = false;
                }
            }

            if(!edittitle.getText().toString().isEmpty()) {
                if(!i.getTitle().toUpperCase().contains(edittitle.getText().toString().toUpperCase())) {
                    check = false;
                }
            }

            if(!depth.getText().toString().isEmpty()) {
                if(i.getDepth() != Integer.parseInt(depth.getText().toString())) {
                    check = false;
                }
            }

            if(!location.getText().toString().isEmpty()) {
                if(!i.getLocation().toUpperCase().contains(location.getText().toString().toUpperCase())) {
                    check = false;
                }
            }

            //After checking all if the check is still true the item meets the criteria and should be brought.
            if(check) {
                toRemove.add(i);
            }
        }

        filterc.setItems(toRemove);
        return filterc;
    }

    /**
     * The parse date function which takes a string and turns it in to a date object.
     * @param dateString accept a string date
     * @return return the date object of the string.
     */
    private Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
