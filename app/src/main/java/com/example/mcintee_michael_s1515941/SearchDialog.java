package com.example.mcintee_michael_s1515941;

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

    public SearchDialog() {

    }

    public static SearchDialog newInstance(String title,Persister persister) {
        SearchDialog myfragment = new SearchDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("channel",persister.getChannel());
        args.putParcelable("persister",persister);
        myfragment.setArguments(args);
        return myfragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_dialog, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setLayoutParams(new LinearLayout.LayoutParams(500,500,1));
        String title = getArguments().getString("title", "Enter Search: ");
        channel = (Channel) getArguments().getSerializable("channel");
        p = getArguments().getParcelable("persister");
        searchButton = view.findViewById(R.id.dialogButtonSearch);
        backButton = view.findViewById(R.id.dialogButtonBack);
        category = view.findViewById(R.id.editCat);
        magnitude = view.findViewById(R.id.editMag);
        location = view.findViewById(R.id.editLocation);
        depth = view.findViewById(R.id.editDepth);
        edittitle = view.findViewById(R.id.editTitle);

        List<String> array = new ArrayList<>();
        for(Item i : channel.getItems()) {

            if(!array.contains(i.getCategory())) {
                array.add(i.getCategory());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(super.getContext(),R.layout.spin, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialog.super.dismiss();
            }
        });

        cal = Calendar.getInstance();
        origDateFrom = view.findViewById(R.id.editOrigFrom);
        final DatePickerDialog.OnDateSetListener adf = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFormFrom();
            }
        };

        origDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(getActivity(),adf,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                dp.show();
            }
        });

        origDateTo = view.findViewById(R.id.editOrigTo);
        final DatePickerDialog.OnDateSetListener adt = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFormTo();
            }
        };

        origDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(getActivity(),adt,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
                dp.show();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
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
        getDialog().setTitle(title);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void updateFormFrom() {
        String myFormat = "E, dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        origDateFrom.setText(sdf.format(cal.getTime()));
    }

    public void updateFormTo() {
        String myFormat = "E, dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        origDateTo.setText(sdf.format(cal.getTime()));
    }

    public Channel pipeFilter() {
        Channel filterc = new Channel();
        filterc.setItems((LinkedList<Item>) channel.getItems().clone());

        LinkedList<Item> toRemove = new LinkedList<>();
        for(Item i : filterc.getItems()) {
            boolean check = true;
            if(!category.getSelectedItem().toString().isEmpty()) {
                if (!i.getCategory().contains(category.getSelectedItem().toString())) {
                    check = false;
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

            if(check) {
                toRemove.add(i);
            }
        }

        filterc.setItems(toRemove);
        return filterc;
    }

    private Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
