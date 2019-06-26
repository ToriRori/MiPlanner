package com.example.miplanner.Activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumPatterns;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.Patterns;
import com.example.miplanner.R;
import com.example.miplanner.RetrofitClient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddEventActivity extends AppCompatActivity {

    private CalendarDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        Button addBtn = findViewById(R.id.buttonAddEvent);
        final Button repeatbtn = findViewById(R.id.buttonRepeat);

        mDbHelper = new CalendarDbHelper(this);


        TimePicker tpStart = findViewById(R.id.timePickerStart);
        DatePicker dpStart = findViewById(R.id.datePickerStart);
        TimePicker tpEnd = findViewById(R.id.timePickerEnd);
        DatePicker dpEnd = findViewById(R.id.datePickerEnd);
        final EditText et = findViewById(R.id.nameText);
        final EditText descriptionEvent = findViewById(R.id.descriptionText);
        final EditText locationText = findViewById(R.id.locationText);
        tpStart.setIs24HourView(true);
        tpEnd.setIs24HourView(true);

        final Bundle bundle = getIntent().getExtras();
        String date = null, startDate = null, endDate = null, startTime = null, endTime = null, name = null, descr = null, loc = null;
        if(bundle != null) {
            date = bundle.getString("day");
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

        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            Calendar cal = new GregorianCalendar();
            try {
                cal.setTime(format.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dpStart.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
            dpEnd.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
            tpStart.setHour(0);
            tpStart.setMinute(0);
            tpEnd.setHour(0);
            tpEnd.setMinute(0);
        }

        if (startDate != null) {
            String[] part = startDate.split("\\.");
            dpStart.init(Integer.parseInt(part[2]), Integer.parseInt(part[1])-1, Integer.parseInt(part[0]), null);
        }

        if (endDate != null) {
            String[] part = endDate.split("\\.");
            dpEnd.init(Integer.parseInt(part[2]), Integer.parseInt(part[1])-1, Integer.parseInt(part[0]), null);
        }

        if (startTime != null) {
            String[] part = startTime.split(":");
            tpStart.setHour(Integer.parseInt(part[0]));
            tpStart.setMinute(Integer.parseInt(part[1]));
        }

        if (endTime != null) {
            String[] part = endTime.split(":");
            tpEnd.setHour(Integer.parseInt(part[0]));
            tpEnd.setMinute(Integer.parseInt(part[1]));
        }

        if (name != null) {
            et.setText(name);
        }

        if (descr != null) {
            descriptionEvent.setText(descr);
        }

        if (loc != null) {
            locationText.setText(loc);
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

                        Intent intent = new Intent(AddEventActivity.this, RepeatEventActivity.class);
                        intent.putExtra("start_date", selectedStart);
                        intent.putExtra("end_date", selectedEnd);
                        intent.putExtra("name", et.getText().toString());
                        intent.putExtra("start_time", selectedStartt);
                        intent.putExtra("end_time", selectedEndt);
                        intent.putExtra("descr", descriptionEvent.getText().toString());
                        intent.putExtra("loc", locationText.getText().toString());
                        startActivity(intent);
                    }
                });

                popupWindow.showAtLocation(popupView,  Gravity.CENTER, 0, 1);
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

                final String selectedStart = dpStart.getDayOfMonth()+"."+(dpStart.getMonth()+1)+"."+dpStart.getYear()+" "+tpStart.getHour()+":"+tpStart.getMinute();
                final String selectdEnd = dpEnd.getDayOfMonth()+"."+(dpEnd.getMonth()+1)+"."+dpEnd.getYear()+" "+tpEnd.getHour()+":"+tpEnd.getMinute();

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
                        String days = calStart.get(Calendar.DAY_OF_WEEK)-1+"";
                        calStart.add(Calendar.DAY_OF_YEAR, 1);
                        while (calStart.before(calEnd)) {
                            days += ","+(calStart.get(Calendar.DAY_OF_WEEK)-1);
                            calStart.add(Calendar.DAY_OF_YEAR, 1);
                        }
                        repeat = "* * * 1 * "+ days +" *";
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
                    Toast.makeText(AddEventActivity.this, "Некорректные дата начала и дата конца события", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!repeat.equals("")){
                    Calendar temp = new GregorianCalendar();
                    temp.setTime(calStart.getTime());
                    String[] part = repeat.split(" ");
                    if (!part[6].equals("*")) {
                        temp.add(Calendar.YEAR, Integer.parseInt(part[6]));
                        if (temp.before(calEnd)) {
                            Toast.makeText(AddEventActivity.this, "Некорректное повторение события для данных дат начала и конца", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        temp.setTime(calStart.getTime());
                    }
                    if (!part[4].equals("*")) {
                        temp.add(Calendar.MONTH, Integer.parseInt(part[4]));
                        if (temp.before(calEnd)) {
                            Toast.makeText(AddEventActivity.this, "Некорректное повторение события для данных дат начала и конца", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        temp.setTime(calStart.getTime());
                    }
                    if (!part[3].equals("*")) {
                        temp.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(part[3]));
                        if (temp.before(calEnd)) {
                            Toast.makeText(AddEventActivity.this, "Некорректное повторение события для данных дат начала и конца", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        temp.setTime(calStart.getTime());
                    }
                    if (!part[2].equals("*")) {
                        temp.add(Calendar.DAY_OF_YEAR, Integer.parseInt(part[2]));
                        if (temp.before(calEnd)) {
                            Toast.makeText(AddEventActivity.this, "Некорректное повторение события для данных дат начала и конца", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        temp.setTime(calStart.getTime());
                    }
                }
                final String mRepeat = repeat;
                final String mEnd = end_repeat;

                if (nameEvent.getText().toString().replaceAll("[\\s\\d]", "").length() > 0) {

                    //mDbHelper.insertEvent(nameEvent.getText().toString(), descriptionEvent.getText().toString(), locationText.getText().toString(), repeat, end_repeat, selectedStart, selectdEnd);
                    final RetrofitClient retrofitClient = RetrofitClient.getInstance();
                    DatumEvents datEv = new DatumEvents(descriptionEvent.getText().toString(), locationText.getText().toString(), nameEvent.getText().toString(), "");
                    retrofitClient.getEventRepository().save(datEv).enqueue(new Callback<Events>() {
                        @Override
                        public void onResponse(Call<Events> call, Response<Events> response) {
                            List<DatumEvents> event = Arrays.asList(response.body().getData());
                            Calendar calStart = new GregorianCalendar();
                            Calendar calEnd = new GregorianCalendar();
                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                            try {
                                calStart.setTime(format.parse(selectedStart));
                                calEnd.setTime(format.parse(selectdEnd));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String rule = "";
                            if (mRepeat.equals(""))
                                rule += "FREQ=DAILY;INTERVAL=7;COUNT=1;";
                            else {
                                String[] parts = mRepeat.split(" ");
                                if (!parts[2].equals("*"))
                                    rule += "FREQ=DAILY;INTERVAL=" + parts[2] + ";";
                                else if (!parts[3].equals("*")) {
                                    rule += "FREQ=WEEKLY;INTERVAL=" + parts[3] + ";";
                                    String[] days = parts[5].split(",");
                                    rule += "BYDAY=";
                                    String byday = "";
                                    for (String day : days) {
                                        if (day.equals("1"))
                                            if (byday.equals(""))
                                                byday = "MO";
                                            else
                                                byday = ",MO";
                                        if (day.equals("2"))
                                            if (byday.equals(""))
                                                byday = "TU";
                                            else
                                                byday = ",TU";
                                        if (day.equals("3"))
                                            if (byday.equals(""))
                                                byday = "WE";
                                            else
                                                byday = ",WE";
                                        if (day.equals("4"))
                                            if (byday.equals(""))
                                                byday = "TH";
                                            else
                                                byday = ",TH";
                                        if (day.equals("5"))
                                            if (byday.equals(""))
                                                byday = "FR";
                                            else
                                                byday = ",FR";
                                        if (day.equals("6"))
                                            if (byday.equals(""))
                                                byday = "SA";
                                            else
                                                byday = ",SA";
                                        if (day.equals("7"))
                                            if (byday.equals(""))
                                                byday = "SU";
                                            else
                                                byday = ",SU";

                                    }
                                    rule += byday + ";";
                                }
                                if (!mEnd.equals("")) {
                                    String[] date = mEnd.split(".");
                                    if (date.length > 1)
                                        rule += "UNTIL=" + date[2] + date[1] + date[0] + "T000000Z;";
                                    else
                                        rule += "COUNT=" + mEnd + ";";
                                }
                            }
                            DatumPatterns datP = new DatumPatterns(calEnd.getTimeInMillis()-calStart.getTimeInMillis(), calEnd.getTimeInMillis(), "", rule,calStart.getTimeInMillis(),"Asia/Vladivostok");
                            retrofitClient.getEventPatternRepository().save(event.get(0).getId(),datP).enqueue(new Callback<Patterns>() {
                                @Override
                                public void onResponse(Call<Patterns> call, Response<Patterns> response) {
                                    Intent intent = new Intent(AddEventActivity.this, MainActivity.class);
                                    Bundle bundle = new Bundle();

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
                                }

                                @Override
                                public void onFailure(Call<Patterns> call, Throwable t) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Events> call, Throwable t) {

                        }
                    });

                    /*Intent intent = new Intent(AddEventActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();

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
                    */
                }
                else
                    Toast.makeText(AddEventActivity.this, "Название события не корректно", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
