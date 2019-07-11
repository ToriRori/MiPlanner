package com.example.miplanner.Activities.Event;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miplanner.R;
import com.google.ical.values.RRule;
import com.google.ical.values.WeekdayNum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class RepeatEventActivity extends AppCompatActivity {

    TextView dateEnding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.repeat_window);
        final EditText repeatTimes = findViewById(R.id.countRepeat);
        final Spinner repeatType = findViewById(R.id.spinner);
        final RadioGroup ending = findViewById(R.id.endRepeat);
        final RadioButton neverEnding = findViewById(R.id.neverEnding);
        final RadioButton dayEnding = findViewById(R.id.dayEnding);
        dateEnding = findViewById(R.id.dateEnding);
        final RadioButton timesEnding = findViewById(R.id.timesEnding);
        final EditText timesCount = findViewById(R.id.timesCount);

        final Bundle bundle = getIntent().getExtras();

        setTypeRepeatAdapter();

        setLayoutsForAdapter();

        setWeekButtonsListeners();

        initFields();

        Button addRepeat = findViewById(R.id.finish);
        addRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String isForEdit = bundle.getString("edit");

                Intent intent;
                if (isForEdit != null)
                    intent = new Intent(RepeatEventActivity.this, EditEventActivity.class);
                else
                    intent = new Intent(RepeatEventActivity.this, AddEventActivity.class);

                if (repeatTimes.getText().toString().equals("")) {
                    Toast.makeText(RepeatEventActivity.this, "Некорректные введенные данные", Toast.LENGTH_SHORT).show();
                    return;
                }

                String week = "";
                if (repeatType.getSelectedItemPosition() == 1) { //collect chosen days for week repeat
                    week = getWeekDays();
                    if (week == null)
                        return;
                }

                String rrule = "FREQ=";
                switch (repeatType.getSelectedItemPosition()) {
                    case 0:
                        rrule += "DAILY;";
                        rrule += "INTERVAL="+repeatTimes.getText().toString()+";";
                        break;
                    case 1:
                        rrule += "WEEKLY;";
                        rrule += "INTERVAL="+repeatTimes.getText().toString()+";";
                        rrule += "BYDAY="+week+";";
                        break;
                    case 2:
                        rrule += "MONTHLY;";
                        rrule += "INTERVAL="+repeatTimes.getText().toString()+";";
                        break;
                    case 3:
                        rrule += "YEARLY;";
                        rrule += "INTERVAL="+repeatTimes.getText().toString()+";";
                        break;
                }


                //collect end of repeat
                int selectedRadio = ending.getCheckedRadioButtonId();
                if (selectedRadio != neverEnding.getId() && selectedRadio != timesEnding.getId() && selectedRadio != dayEnding.getId()){
                    Toast.makeText(RepeatEventActivity.this, "Выберите тип конца повтора", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedRadio == dayEnding.getId()) {
                    if (dateEnding.getText().equals("")) {
                        Toast.makeText(RepeatEventActivity.this, "Некорректные введенные данные", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    rrule += "UNTIL="+dateEnding.getText().toString()+";";
                }
                if (selectedRadio == timesEnding.getId()) {
                    if (timesCount.getText().equals("")){
                        Toast.makeText(RepeatEventActivity.this, "Некорректные введенные данные", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    rrule += "COUNT="+timesCount.getText().toString()+";";
                }


                intent.putExtra("name", bundle.getString("name"));
                intent.putExtra("start_date", bundle.getSerializable("start_date"));
                intent.putExtra("end_date", bundle.getSerializable("end_date"));
                intent.putExtra("descr", bundle.getString("descr"));
                intent.putExtra("loc", bundle.getString("loc"));
                intent.putExtra("event_id", bundle.getLong("event_id"));
                intent.putExtra("rrule", rrule);
                startActivity(intent);
            }
        });

    }

    private String getWeekDays() {
        Button[] weekBtns = new Button[7];
        weekBtns[0] = findViewById(R.id.monday_btn);
        weekBtns[1] = findViewById(R.id.tuesday_btn);
        weekBtns[2] = findViewById(R.id.wednesday_btn);
        weekBtns[3] = findViewById(R.id.thursday_btn);
        weekBtns[4] = findViewById(R.id.friday_btn);
        weekBtns[5] = findViewById(R.id.saturday_btn);
        weekBtns[6] = findViewById(R.id.sunday_btn);

        String[] weekText = {"MO", "TU", "WE", "TH", "FR", "SA", "SU"};
        String week = "";

        for (int i = 0; i < 7; i++) {
            if (weekBtns[i].getCurrentTextColor() == getResources().getColor(R.color.white))
                if (week.equals(""))
                    week = weekText[i];
                else
                    week += ","+weekText[i];
        }
        if (week.equals("")) {
            Toast.makeText(RepeatEventActivity.this, "Некорректные введенные данные", Toast.LENGTH_SHORT).show();
            return null;
        }
        return week;
    }

    private void initFields() {
        final EditText timesCount = findViewById(R.id.timesCount);
        final EditText repeatTimes = findViewById(R.id.countRepeat);
        final Spinner repeatType = findViewById(R.id.spinner);
        final RadioGroup ending = findViewById(R.id.endRepeat);
        final RadioButton neverEnding = findViewById(R.id.neverEnding);
        dateEnding = findViewById(R.id.dateEnding);
        final RadioButton timesEnding = findViewById(R.id.timesEnding);
        Button[] weekBtns = new Button[7];
        weekBtns[0] = findViewById(R.id.sunday_btn);
        weekBtns[1] = findViewById(R.id.monday_btn);
        weekBtns[2] = findViewById(R.id.tuesday_btn);
        weekBtns[3] = findViewById(R.id.wednesday_btn);
        weekBtns[4] = findViewById(R.id.thursday_btn);
        weekBtns[5] = findViewById(R.id.friday_btn);
        weekBtns[6] = findViewById(R.id.saturday_btn);


        final Bundle bundle = getIntent().getExtras();
        Calendar endDate = (Calendar) bundle.getSerializable("end_date");
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        dateEnding.setText(format.format(endDate.getTime()));
        repeatTimes.setText("1");
        timesCount.setText("1");

        dateEnding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle temp = new Bundle();
                bundle.putString("date", dateEnding.getText().toString());
                showDialog(1, bundle);
            }
        });

        if (bundle.getBoolean("filled")) {
            String rrule = bundle.getString("rrule");
            if (rrule != null) {
                RRule rule = null;
                try {
                    rule = new RRule("RRULE:"+rrule);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                switch (rule.getFreq()) {
                    case DAILY:
                        repeatType.setSelection(0);
                        break;
                    case WEEKLY:
                        repeatType.setSelection(1);
                        List<WeekdayNum> days  = rule.getByDay();
                        for (WeekdayNum day : days) {
                            weekBtns[day.wday.jsDayNum].setBackground(getResources().getDrawable(R.drawable.corners3));
                            weekBtns[day.wday.jsDayNum].setTextColor(getResources().getColor(R.color.white));
                        }
                        break;
                    case MONTHLY:
                        repeatType.setSelection(2);
                        break;
                    case YEARLY:
                        repeatType.setSelection(3);
                        break;
                }
                repeatTimes.setText(Integer.toString(rule.getInterval()));

                if (rule.getCount() == 0 && rule.getUntil() == null) {
                    ((RadioButton) ending.getChildAt(0)).setChecked(true);
                    neverEnding.setSelected(true);
                } else if (rule.getUntil() != null) {
                    ((RadioButton) ending.getChildAt(1)).setChecked(true);
                    dateEnding.setSelected(true);
                    dateEnding.setText(rule.getUntil().day()+"."+rule.getUntil().month()+"."+rule.getUntil().year());
                } else {
                    ((RadioButton) ending.getChildAt(3)).setChecked(true);
                    timesEnding.setSelected(true);
                    timesCount.setText(Integer.toString(rule.getCount()));
                }
            }
        }
    }

    private void setTypeRepeatAdapter() {
        final Spinner repeatType = findViewById(R.id.spinner);
        String[] types = {"день", "неделя", "месяц", "год"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatType.setAdapter(adapter);
    }

    private void setWeekButtonsListeners() {
        Button btn[] = new Button[7];
        btn[0] = findViewById(R.id.monday_btn);
        btn[1] = findViewById(R.id.tuesday_btn);
        btn[2] = findViewById(R.id.wednesday_btn);
        btn[3] = findViewById(R.id.thursday_btn);
        btn[4] = findViewById(R.id.friday_btn);
        btn[5] = findViewById(R.id.saturday_btn);
        btn[6] = findViewById(R.id.sunday_btn);

        for (final Button day : btn) {
            day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (day.getCurrentTextColor() == getResources().getColor(R.color.theme_primary)) {
                        day.setBackground(getResources().getDrawable(R.drawable.corners3));
                        day.setTextColor(getResources().getColor(R.color.white));
                    }
                    else {
                        day.setBackground(getResources().getDrawable(R.drawable.corners2));
                        day.setTextColor(getResources().getColor(R.color.theme_primary));
                    }
                }
            });
        }
    }

    private void setLayoutsForAdapter() {
        final Spinner repeatType = findViewById(R.id.spinner);
        final LinearLayout weekRepeat = findViewById(R.id.week_attr);
        final LinearLayout monthRepeat = findViewById(R.id.month_attr);
        final TextView endText = findViewById(R.id.textView3);

        final RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthRepeat.setVisibility(View.GONE);
                params.setMargins(68,20,20,0);
                endText.setLayoutParams(params);
                if (position == 1) {
                    weekRepeat.setVisibility(View.VISIBLE);
                    params.addRule(RelativeLayout.BELOW, R.id.week_attr);
                }
                else {
                    weekRepeat.setVisibility(View.GONE);
                    params.addRule(RelativeLayout.BELOW, R.id.spinner);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        repeatType.setOnItemSelectedListener(itemSelectedListener);
    }

    protected Dialog onCreateDialog(int id, Bundle bundle) {
        if (id == 1) {
            String date = bundle.getString("date");
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            Calendar cal = new GregorianCalendar();
            try {
                cal.setTime(format.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateEnding.setText(dayOfMonth+"."+monthOfYear+"."+year);
        }
    };
}
