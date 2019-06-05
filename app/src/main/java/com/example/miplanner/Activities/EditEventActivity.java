package com.example.miplanner.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.miplanner.Data.CalendarDbHelper;
import com.example.miplanner.Event;
import com.example.miplanner.R;
import com.example.miplanner.Event;
import com.example.miplanner.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditEventActivity extends AppCompatActivity {

    Event[] events;
    int size;
    private CalendarDbHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event);
        Button addBtn = findViewById(R.id.buttonAddEvent);

        mDbHelper = new CalendarDbHelper(this);

        Bundle bundle = getIntent().getExtras();
        //Parcelable[] temp = bundle.getParcelableArray("events");
        //events = Arrays.copyOf(temp, temp.length, Event[].class);
        //size = bundle.getInt("size", 0);
        final long itemNumber  = bundle.getLong("event_id");
        TimePicker tpStart = findViewById(R.id.timePickerStart);
        DatePicker dpStart = findViewById(R.id.datePickerStart);
        TimePicker tpEnd = findViewById(R.id.timePickerEnd);
        DatePicker dpEnd = findViewById(R.id.datePickerEnd);
        EditText et = findViewById(R.id.nameText);
        EditText descEt = findViewById(R.id.descriptionText);

        tpStart.setIs24HourView(true);
        tpEnd.setIs24HourView(true);

        Event event = mDbHelper.getEventById((int)itemNumber);

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Calendar calStart = new GregorianCalendar();
        Calendar calEnd = new GregorianCalendar();
        try {
            calStart.setTime(format.parse(event.getDateStart()));
            calEnd.setTime(format.parse(event.getDateEnd()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dpStart.init(calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH), calStart.get(Calendar.DAY_OF_MONTH), null);
        dpEnd.init(calEnd.get(Calendar.YEAR), calEnd.get(Calendar.MONTH), calEnd.get(Calendar.DAY_OF_MONTH), null);
        tpStart.setHour(calStart.get(Calendar.HOUR_OF_DAY));
        tpStart.setMinute(calStart.get(Calendar.MINUTE));
        tpEnd.setHour(calEnd.get(Calendar.HOUR_OF_DAY));
        tpEnd.setMinute(calEnd.get(Calendar.MINUTE));
        et.setText(event.getName());
        descEt.setText(event.getDescription());
        /*String day = bundle.getString("day");
        String name = bundle.getString("name");
        int time = bundle.getInt("time");
        if ((day != null)&&(name != null)) {
            Calendar cal = new GregorianCalendar();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy");
            try {
                cal.setTime(dateFormat.parse(day));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dpStart.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
            tpStart.setHour(time);
            tp.setMinute(0);
            et.setText(name);
        }*/

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameEvent = findViewById(R.id.nameText);
                DatePicker dpStart = findViewById(R.id.datePickerStart);
                TimePicker tpStart = findViewById(R.id.timePickerStart);
                DatePicker dpEnd = findViewById(R.id.datePickerEnd);
                TimePicker tpEnd = findViewById(R.id.timePickerEnd);
                EditText descriptionEvent = findViewById(R.id.descriptionText);
                EditText locationText = findViewById(R.id.locationText);

                String selectedStart = dpStart.getDayOfMonth() + "." + (dpStart.getMonth() + 1) + "." + dpStart.getYear() + " " + tpStart.getHour() + ":" + tpStart.getMinute();
                String selectdEnd = dpEnd.getDayOfMonth() + "." + (dpEnd.getMonth() + 1) + "." + dpEnd.getYear() + " " + tpEnd.getHour() + ":" + tpEnd.getMinute();

                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                Calendar calStart = new GregorianCalendar();
                Calendar calEnd = new GregorianCalendar();
                try {
                    calStart.setTime(format.parse(selectedStart));
                    calEnd.setTime(format.parse(selectdEnd));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (calStart.after(calEnd)) {
                    Toast.makeText(EditEventActivity.this, "Некорректные дата начала и дата конца события", Toast.LENGTH_SHORT).show();
                } else {
                    if (nameEvent.getText().toString().replaceAll("[\\s\\d]", "").length() > 0) {
                        //events[(int)itemNumber] = new Event((int)itemNumber, nameEvent.getText().toString(), selectedStart, selectdEnd, descriptionEvent.getText().toString());

                        String rep = "";

                        mDbHelper.editEventById((int) itemNumber, nameEvent.getText().toString(), descriptionEvent.getText().toString(), locationText.getText().toString(), rep, selectedStart, selectdEnd);

                        Intent intent = new Intent(EditEventActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        //bundle.putParcelableArray("events", events);
                        //bundle.putInt("size", size);
                        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        Calendar cal = new GregorianCalendar();
                        try {
                            cal.setTime(dateFormat.parse(selectedStart));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                        bundle.putString("Date", dateFormat.format(cal.getTime()));
                        dateFormat = new SimpleDateFormat("HH");
                        bundle.putInt("Position", Integer.parseInt(dateFormat.format(cal.getTime())));
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition (R.anim.enter, R.anim.exit);
                    } else
                        Toast.makeText(EditEventActivity.this, "Название события не валидно", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
