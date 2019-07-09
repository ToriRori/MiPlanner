package com.example.miplanner.Activities.Event;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.miplanner.Activities.MainActivity;
import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumPatterns;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.Patterns;
import com.example.miplanner.R;
import com.example.miplanner.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.ical.values.RRule;

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

    //private CalendarDbHelper mDbHelper;
    String tokenID = null;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        Button addBtn = findViewById(R.id.buttonAddEvent);
        final Button repeatbtn = findViewById(R.id.buttonRepeat);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        //mDbHelper = new CalendarDbHelper(this);

        initFields();

        repeatbtn.setOnClickListener(repeatListener);

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

                //get start/end dates
                final String selectedStart = dpStart.getDayOfMonth()+"."+(dpStart.getMonth()+1)+"."+dpStart.getYear()+" "+tpStart.getHour()+":"+tpStart.getMinute();
                final String selectdEnd = dpEnd.getDayOfMonth()+"."+(dpEnd.getMonth()+1)+"."+dpEnd.getYear()+" "+tpEnd.getHour()+":"+tpEnd.getMinute();

                String rrule = getRrule();

                //check dates correct
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

                //check repeat correct with dates
                if (rrule != null){
                    Calendar temp = new GregorianCalendar();
                    temp.setTime(calStart.getTime());
                    RRule rule = null;
                    try {
                        rule = new RRule("RRULE:"+rrule);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    switch(rule.getFreq()) {
                        case DAILY :
                            temp.add(Calendar.DAY_OF_YEAR, rule.getInterval());
                            break;
                        case WEEKLY:
                            temp.add(Calendar.WEEK_OF_YEAR, rule.getInterval());
                            break;
                        case MONTHLY:
                            temp.add(Calendar.MONTH, rule.getInterval());
                            break;
                        case YEARLY:
                            temp.add(Calendar.YEAR, rule.getInterval());
                            break;
                    }
                    if (temp.before(calEnd)) {
                        Toast.makeText(AddEventActivity.this, "Некорректное повторение события для данных дат начала и конца", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (nameEvent.getText().toString().replaceAll("[\\s\\d]", "").length() <= 0) {
                    Toast.makeText(AddEventActivity.this, "Название события не корректно", Toast.LENGTH_SHORT).show();
                    return;
                }

                requestForAdd(rrule);
            }
        });
    }

    public String getRrule() {
        String rrule = "";
        final Button repeatbtn = findViewById(R.id.buttonRepeat);
        switch (repeatbtn.getText().toString()) {
            case "Не повторяется":
                rrule = null;
                break;
            case "Каждый день":
                rrule = "FREQ=DAILY;INTERVAL=1;";
                break;
            case "Каждую неделю":
                rrule = "FREQ=WEEKLY;INTERVAL=1;";
                break;
            case "Каждый месяц":
                rrule = "FREQ=MONTHLY;INTERVAL=1;";
                break;
            case "Каждый год":
                rrule = "FREQ=YEARLY;INTERVAL=1;";
                break;
            case "Другое...":
                Bundle bundle = getIntent().getExtras();
                rrule = bundle.getString("rrule");
                break;
        }
        return rrule;
    }

    public void initFields() {
        final Button repeatbtn = findViewById(R.id.buttonRepeat);
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
        String date = bundle.getString("day");
        String startDate = bundle.getString("start_date");
        String endDate = bundle.getString("end_date");
        String startTime = bundle.getString("start_time");
        String endTime = bundle.getString("end_time");
        String name = bundle.getString("name");
        String descr = bundle.getString("descr");
        String loc = bundle.getString("loc");

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

        if (bundle.getString("rrule") != null)
            repeatbtn.setText("Другое...");
    }

    View.OnClickListener repeatListener  =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final EditText et = findViewById(R.id.nameText);
            final EditText descriptionEvent = findViewById(R.id.descriptionText);
            final EditText locationText = findViewById(R.id.locationText);
            final Button repeatbtn = findViewById(R.id.buttonRepeat);
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

                    Bundle bundle = getIntent().getExtras();
                    if (bundle.getString("rrule") != null) {
                        intent.putExtra("rrule", bundle.getString("rrule"));
                        intent.putExtra("ok", "ok");
                    }
                    startActivity(intent);
                }
            });

            popupWindow.showAtLocation(popupView,  Gravity.CENTER, 0, 0);
        }
    };

    public void requestForAdd(final String mRrule) {
        final Button repeatbtn = findViewById(R.id.buttonRepeat);
        TimePicker tpStart = findViewById(R.id.timePickerStart);
        DatePicker dpStart = findViewById(R.id.datePickerStart);
        TimePicker tpEnd = findViewById(R.id.timePickerEnd);
        DatePicker dpEnd = findViewById(R.id.datePickerEnd);
        final EditText et = findViewById(R.id.nameText);
        final EditText descriptionEvent = findViewById(R.id.descriptionText);
        final EditText locationText = findViewById(R.id.locationText);

        final String selectedStart = dpStart.getDayOfMonth()+"."+(dpStart.getMonth()+1)+"."+dpStart.getYear()+" "+tpStart.getHour()+":"+tpStart.getMinute();
        final String selectdEnd = dpEnd.getDayOfMonth()+"."+(dpEnd.getMonth()+1)+"."+dpEnd.getYear()+" "+tpEnd.getHour()+":"+tpEnd.getMinute();



        //mDbHelper.insertEvent(nameEvent.getText().toString(), descriptionEvent.getText().toString(), locationText.getText().toString(), repeat, end_repeat, selectedStart, selectdEnd);
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        final DatumEvents datEv = new DatumEvents(descriptionEvent.getText().toString(), locationText.getText().toString(), et.getText().toString(), "");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                tokenID = task.getResult().getToken();
                retrofitClient.getEventRepository().save(datEv,tokenID).enqueue(new Callback<Events>() {
                    @Override
                    public void onResponse(Call<Events> call, Response<Events> response) {
                        List<DatumEvents> event = Arrays.asList(response.body().getData());
                        final Calendar calStart = new GregorianCalendar();
                        Calendar calEnd = new GregorianCalendar();
                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        try {
                            calStart.setTime(format.parse(selectedStart));
                            calEnd.setTime(format.parse(selectdEnd));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String rule = mRrule;

                        DatumPatterns datP;
                        if (mRrule!=null) {
                            RRule r = null;
                            try {
                                r = new RRule("RRULE:"+mRrule);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (r.getUntil() == null && r.getCount() == 0)
                                datP = new DatumPatterns(calEnd.getTimeInMillis()-calStart.getTimeInMillis(), Long.MAX_VALUE-1, "", rule,calStart.getTimeInMillis(),"Asia/Vladivostok");
                            else {
                                Calendar calendarEnd = getEnd(calEnd, r);
                                datP = new DatumPatterns(calEnd.getTimeInMillis() - calStart.getTimeInMillis(), calendarEnd.getTimeInMillis(), "", rule, calStart.getTimeInMillis(), "Asia/Vladivostok");
                            }
                        }
                        else
                            datP = new DatumPatterns(calEnd.getTimeInMillis()-calStart.getTimeInMillis(), calEnd.getTimeInMillis(), "", null,calStart.getTimeInMillis(),"Asia/Vladivostok");
                        retrofitClient.getEventPatternRepository().save(event.get(0).getId(),datP,tokenID).enqueue(new Callback<Patterns>() {
                            @Override
                            public void onResponse(Call<Patterns> call, Response<Patterns> response) {
                                Intent intent = new Intent(AddEventActivity.this, MainActivity.class);
                                Bundle bundle = new Bundle();

                                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                                Calendar cal = new GregorianCalendar();
                                try {
                                    cal.setTime(dateFormat.parse(selectedStart));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                bundle.putString("Date", dateFormat.format(calStart.getTime()));
                                intent.putExtras(bundle);
                                progressBar.setVisibility(View.GONE);
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

    public Calendar getEnd(Calendar calendar, RRule r) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(calendar.getTime());
        if (r.getUntil() == null)
            switch (r.getFreq()){
                case DAILY:
                    cal.add(Calendar.DAY_OF_MONTH, r.getInterval()*r.getCount());
                    break;
                case WEEKLY:
                    cal.add(Calendar.WEEK_OF_YEAR, r.getInterval()*r.getCount());
                    break;
                case MONTHLY:
                    cal.add(Calendar.MONTH, r.getInterval()*r.getCount());
                    break;
                case YEARLY:
                    cal.add(Calendar.YEAR, r.getInterval()*r.getCount());
                    break;
            }
        else {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            try {
                cal.setTime(format.parse(r.getUntil().day()+"."+r.getUntil().month()+"."+r.getUntil().year()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return cal;
    }
}
