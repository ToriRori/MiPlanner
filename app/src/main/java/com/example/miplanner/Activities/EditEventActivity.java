package com.example.miplanner.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
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

        final Bundle bundle = getIntent().getExtras();
        //Parcelable[] temp = bundle.getParcelableArray("events");
        //events = Arrays.copyOf(temp, temp.length, Event[].class);
        //size = bundle.getInt("size", 0);
        final long itemNumber  = bundle.getLong("event_id");
        final TimePicker tpStart = findViewById(R.id.timePickerStart);
        final DatePicker dpStart = findViewById(R.id.datePickerStart);
        final TimePicker tpEnd = findViewById(R.id.timePickerEnd);
        final DatePicker dpEnd = findViewById(R.id.datePickerEnd);
        final EditText et = findViewById(R.id.nameText);
        final EditText descEt = findViewById(R.id.descriptionText);
        final EditText locEt = findViewById(R.id.locationText);
        final Button repeatbtn = findViewById(R.id.buttonRepeat);


        tpStart.setIs24HourView(true);
        tpEnd.setIs24HourView(true);

        String date = null, startDate = null, endDate = null, startTime = null, endTime = null, name = null, descr = null, loc = null;
        if(bundle != null) {
            if (bundle.getString("rep") != null)
                repeatbtn.setText("Другое...");
            startDate = bundle.getString("start_date");
            endDate = bundle.getString("end_date");
            startTime = bundle.getString("start_time");
            endTime = bundle.getString("end_time");
            name = bundle.getString("name");
            descr = bundle.getString("descr");
            loc = bundle.getString("loc");
        }


        Event event = mDbHelper.getEventById((int)itemNumber);


        if(name == null) {
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
            locEt.setText(event.getLocation());
            if (!event.getRepeat().equals("")) {
                if (event.getRepeat().equals("* * 1 * * * *"))
                    repeatbtn.setText("Каждый день");
                else if (event.getRepeat().equals("* * * 1 * * *"))
                    repeatbtn.setText("Каждую неделю");
                else if (event.getRepeat().equals("* * * * 1 * *"))
                    repeatbtn.setText("Каждый месяц");
                else if (event.getRepeat().equals("* * * * * * 1"))
                    repeatbtn.setText("Каждый год");
                else
                    repeatbtn.setText("Другое...");
            }
        }
        else {
            String[] part = startDate.split(".");
            dpStart.init(Integer.parseInt(part[2]), Integer.parseInt(part[1]), Integer.parseInt(part[0]), null);
            String[] part2 = endDate.split(".");
            dpEnd.init(Integer.parseInt(part[2]), Integer.parseInt(part2[1]), Integer.parseInt(part2[0]), null);
            String[] part3 = startTime.split(":");
            tpStart.setHour(Integer.parseInt(part3[0]));
            tpEnd.setMinute(Integer.parseInt(part3[1]));
            String[] part4 = endTime.split(":");
            tpStart.setHour(Integer.parseInt(part4[0]));
            tpEnd.setMinute(Integer.parseInt(part4[1]));
            et.setText(name);
            descEt.setText(descr);
            locEt.setText(loc);
        }


        repeatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater
                        = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.repeat_info, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);
                Button noRep = popupView.findViewById(R.id.noRepeat);
                Button dayRep = popupView.findViewById(R.id.dayRepeat);
                Button weekRep = popupView.findViewById(R.id.weekRepeat);
                Button monthRep = popupView.findViewById(R.id.monthRepeat);
                Button yearRep = popupView.findViewById(R.id.yearRepeat);
                Button othRep = popupView.findViewById(R.id.otherRepeat);

                noRep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        repeatbtn.setText("Не повторяется");
                    }
                });

                dayRep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        repeatbtn.setText("Каждый день");
                    }
                });

                weekRep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        repeatbtn.setText("Каждую неделю");
                    }
                });

                monthRep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        repeatbtn.setText("Каждый месяц");
                    }
                });

                yearRep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        repeatbtn.setText("Каждый год");
                    }
                });

                othRep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePicker dpStart = findViewById(R.id.datePickerStart);
                        TimePicker tpStart = findViewById(R.id.timePickerStart);
                        DatePicker dpEnd = findViewById(R.id.datePickerEnd);
                        TimePicker tpEnd = findViewById(R.id.timePickerEnd);

                        String selectedStart = dpStart.getDayOfMonth()+"."+(dpStart.getMonth()+1)+"."+dpStart.getYear();
                        String selectedEnd = dpEnd.getDayOfMonth()+"."+(dpEnd.getMonth()+1)+"."+dpEnd.getYear();
                        String selectedStartt = tpStart.getHour()+":"+tpStart.getMinute();
                        String selectedEndt = tpEnd.getHour()+":"+tpEnd.getMinute();


                        popupWindow.dismiss();

                        Intent intent = new Intent(EditEventActivity.this, RepeatEventActivity.class);
                        intent.putExtra("start_date", selectedStart);
                        intent.putExtra("end_date", selectedEnd);
                        intent.putExtra("name", et.getText().toString());
                        intent.putExtra("start_time", selectedStartt);
                        intent.putExtra("end_time", selectedEndt);
                        intent.putExtra("descr", descEt.getText().toString());
                        intent.putExtra("loc", locEt.getText().toString());
                        intent.putExtra("id", itemNumber);
                        intent.putExtra("ok", "ok");
                        startActivity(intent);
                    }
                });

                popupWindow.showAtLocation(popupView,  Gravity.CENTER, 0, 0);
            }
        });


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

                nameEvent.clearFocus();
                descriptionEvent.clearFocus();
                locationText.clearFocus();

                String selectedStart = dpStart.getDayOfMonth() + "." + (dpStart.getMonth() + 1) + "." + dpStart.getYear() + " " + tpStart.getHour() + ":" + tpStart.getMinute();
                String selectdEnd = dpEnd.getDayOfMonth() + "." + (dpEnd.getMonth() + 1) + "." + dpEnd.getYear() + " " + tpEnd.getHour() + ":" + tpEnd.getMinute();

                String repeat = "";
                String end_repeat = "";


                if (repeatbtn.getText().equals("Другое...")) {
                    String repeatTimes = bundle.getString("count");
                    int repeatType = bundle.getInt("type");

                    switch (repeatType) {
                        case 0:
                            repeat = "* * "+repeatTimes+" * * * *";
                            break;
                        case 1:
                            String days = bundle.getString("weekChoice");
                            repeat = "* * * "+repeatTimes+" * "+days+" *";
                            break;
                        case 2:
                            int monthType = bundle.getInt("monthChoice");
                            if (monthType == 0)
                                repeat = "* * * * "+repeatTimes+" * *";
                            else
                                repeat = "* * 1-7 * "+repeatTimes+" 7 *";
                            break;
                        case 3:
                            repeat = "* * * * * * "+repeatTimes;
                            break;
                    }

                    int repeatEndType = bundle.getInt("end");
                    if (repeatEndType == 2)
                        end_repeat = bundle.getString("dayEnd");
                    if (repeatEndType == 3){
                        end_repeat = bundle.getString("times");
                    }
                } else
                {
                    if (repeatbtn.getText().equals("Каждый день")) {
                        repeat = "* * 1 * * * *";
                        end_repeat = "";
                    }

                    if (repeatbtn.getText().equals("Каждую неделю")) {
                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        Calendar calStart = new GregorianCalendar();
                        Calendar calEnd = new GregorianCalendar();
                        try {
                            calStart.setTime(format.parse(selectedStart));
                            calEnd.setTime(format.parse(selectdEnd));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        repeat = "* * * 1 * "+ calStart.get(Calendar.DAY_OF_WEEK) +" *";
                        end_repeat = "";
                    }

                    if (repeatbtn.getText().equals("Каждый месяц")) {
                        repeat = "* * * * 1 * *";
                        end_repeat = "";
                    }

                    if (repeatbtn.getText().equals("Каждый год")) {
                        repeat = "* * * * * * 1";
                        end_repeat = "";
                    }

                    if (repeatbtn.getText().equals("Не повторяется")) {
                        repeat = "";
                        end_repeat = "";
                    }
                }



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
                    return;
                }
                if (!repeat.equals("")){
                    Calendar temp = new GregorianCalendar();
                    temp.setTime(calStart.getTime());
                    String[] part = repeat.split(" ");
                    if (!part[6].equals("*")) {
                        temp.add(Calendar.YEAR, Integer.parseInt(part[6]));
                        if (temp.before(calEnd)) {
                            Toast.makeText(EditEventActivity.this, "Некорректное повторение события для данных дат начала и конца", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        temp.setTime(calStart.getTime());
                    }
                    if (!part[4].equals("*")) {
                        temp.add(Calendar.MONTH, Integer.parseInt(part[4]));
                        if (temp.before(calEnd)) {
                            Toast.makeText(EditEventActivity.this, "Некорректное повторение события для данных дат начала и конца", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        temp.setTime(calStart.getTime());
                    }
                    if (!part[3].equals("*")) {
                        temp.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(part[3]));
                        if (temp.before(calEnd)) {
                            Toast.makeText(EditEventActivity.this, "Некорректное повторение события для данных дат начала и конца", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        temp.setTime(calStart.getTime());
                    }
                    if (!part[2].equals("*")) {
                        temp.add(Calendar.DAY_OF_YEAR, Integer.parseInt(part[2]));
                        if (temp.before(calEnd)) {
                            Toast.makeText(EditEventActivity.this, "Некорректное повторение события для данных дат начала и конца", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        temp.setTime(calStart.getTime());
                    }
                }
                if (nameEvent.getText().toString().replaceAll("[\\s\\d]", "").length() > 0) {
                    //events[(int)itemNumber] = new Event((int)itemNumber, nameEvent.getText().toString(), selectedStart, selectdEnd, descriptionEvent.getText().toString());

                    mDbHelper.editEventById((int) itemNumber, nameEvent.getText().toString(), descriptionEvent.getText().toString(), locationText.getText().toString(), repeat, end_repeat, selectedStart, selectdEnd);

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
        });
    }
}
