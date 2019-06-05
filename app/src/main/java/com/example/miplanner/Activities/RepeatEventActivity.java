package com.example.miplanner.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miplanner.R;

public class RepeatEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.repeat_window);
        final EditText repeatTimes = findViewById(R.id.countRepeat);
        final Spinner repeatType = findViewById(R.id.spinner);
        final LinearLayout weekRepeat = findViewById(R.id.week_attr);
        final LinearLayout monthRepeat = findViewById(R.id.month_attr);
        final RadioGroup ending = findViewById(R.id.endRepeat);
        final RadioButton neverEnding = findViewById(R.id.neverEnding);
        final RadioButton dayEnding = findViewById(R.id.dayEnding);
        final EditText dateEnding = findViewById(R.id.dateEnding);
        final RadioButton timesEnding = findViewById(R.id.timesEnding);
        final EditText timesCount = findViewById(R.id.timesCount);

        final Bundle bundle = getIntent().getExtras();
        String endDate = bundle.getString("end");
        dateEnding.setText(endDate);
        timesCount.setText("1");

        String[] types = {"день", "неделя", "месяц", "год"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatType.setAdapter(adapter);

        final Spinner monthSpinner = findViewById(R.id.monthType);
        String[] typesMonth = {"раз в месяц", "первое воскресенье месяца"};
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typesMonth);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapterMonth);

        final Button monBtn = findViewById(R.id.monday_btn);
        final Button tueBtn = findViewById(R.id.tuesday_btn);
        final Button wedBtn = findViewById(R.id.wednesday_btn);
        final Button thuBtn = findViewById(R.id.thursday_btn);
        final Button friBtn = findViewById(R.id.friday_btn);
        final Button satBtn = findViewById(R.id.saturday_btn);
        final Button sunBtn = findViewById(R.id.sunday_btn);

        wedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wedBtn.getCurrentTextColor() == getResources().getColor(R.color.black)) {
                    wedBtn.setBackground(getResources().getDrawable(R.drawable.corners3));
                    wedBtn.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    wedBtn.setBackground(getResources().getDrawable(R.drawable.corners2));
                    wedBtn.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        tueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tueBtn.getCurrentTextColor() == getResources().getColor(R.color.black)) {
                    tueBtn.setBackground(getResources().getDrawable(R.drawable.corners3));
                    tueBtn.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    tueBtn.setBackground(getResources().getDrawable(R.drawable.corners2));
                    tueBtn.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        monBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (monBtn.getCurrentTextColor() == getResources().getColor(R.color.black)) {
                    monBtn.setBackground(getResources().getDrawable(R.drawable.corners3));
                    monBtn.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    monBtn.setBackground(getResources().getDrawable(R.drawable.corners2));
                    monBtn.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        thuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thuBtn.getCurrentTextColor() == getResources().getColor(R.color.black)) {
                    thuBtn.setBackground(getResources().getDrawable(R.drawable.corners3));
                    thuBtn.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    thuBtn.setBackground(getResources().getDrawable(R.drawable.corners2));
                    thuBtn.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        friBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friBtn.getCurrentTextColor() == getResources().getColor(R.color.black)) {
                    friBtn.setBackground(getResources().getDrawable(R.drawable.corners3));
                    friBtn.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    friBtn.setBackground(getResources().getDrawable(R.drawable.corners2));
                    friBtn.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        satBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (satBtn.getCurrentTextColor() == getResources().getColor(R.color.black)) {
                    satBtn.setBackground(getResources().getDrawable(R.drawable.corners3));
                    satBtn.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    satBtn.setBackground(getResources().getDrawable(R.drawable.corners2));
                    satBtn.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        sunBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sunBtn.getCurrentTextColor() == getResources().getColor(R.color.black)) {
                    sunBtn.setBackground(getResources().getDrawable(R.drawable.corners3));
                    sunBtn.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    sunBtn.setBackground(getResources().getDrawable(R.drawable.corners2));
                    sunBtn.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        weekRepeat.setVisibility(View.GONE);
                        monthRepeat.setVisibility(View.GONE);
                        break;
                    case 1:
                        weekRepeat.setVisibility(View.VISIBLE);
                        monthRepeat.setVisibility(View.GONE);
                        break;
                    case 2:
                        weekRepeat.setVisibility(View.GONE);
                        monthRepeat.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        weekRepeat.setVisibility(View.GONE);
                        monthRepeat.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        repeatType.setOnItemSelectedListener(itemSelectedListener);

        Button addRepeat = findViewById(R.id.finish);
        addRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RepeatEventActivity.this, AddEventActivity.class);
                if (repeatTimes.getText().equals("")) {
                    Toast.makeText(RepeatEventActivity.this, "Некорректные введенные данные", Toast.LENGTH_SHORT).show();
                    return;
                }

                intent.putExtra("count", repeatTimes.getText().toString());
                intent.putExtra("type", repeatType.getSelectedItemPosition());
                if (repeatType.getSelectedItemPosition() == 1) {
                    String week = "";
                    if (monBtn.getCurrentTextColor() == getResources().getColor(R.color.white))
                        week = "1";
                    if (tueBtn.getCurrentTextColor() == getResources().getColor(R.color.white))
                        if (week.equals(""))
                            week = "2";
                        else
                            week += ",2";
                    if (wedBtn.getCurrentTextColor() == getResources().getColor(R.color.white))
                        if (week.equals(""))
                            week = "3";
                        else
                            week += ",3";
                    if (thuBtn.getCurrentTextColor() == getResources().getColor(R.color.white))
                        if (week.equals(""))
                            week = "4";
                        else
                            week += ",4";
                    if (friBtn.getCurrentTextColor() == getResources().getColor(R.color.white))
                        if (week.equals(""))
                            week = "5";
                        else
                            week += ",5";
                    if (satBtn.getCurrentTextColor() == getResources().getColor(R.color.white))
                        if (week.equals(""))
                            week = "6";
                        else
                            week += ",6";
                    if (sunBtn.getCurrentTextColor() == getResources().getColor(R.color.white))
                        if (week.equals(""))
                            week = "7";
                        else
                            week += ",7";
                    if (week.equals("")) {
                        Toast.makeText(RepeatEventActivity.this, "Некорректные введенные данные", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    intent.putExtra("weekChoice", week);
                }
                if (repeatType.getSelectedItemPosition() == 2)
                    intent.putExtra("monthChoice", monthSpinner.getSelectedItemPosition());

                int selectedRadio = ending.getCheckedRadioButtonId();

                if (selectedRadio == neverEnding.getId())
                    intent.putExtra("end", 1);
                if (selectedRadio == dayEnding.getId()) {
                    intent.putExtra("end", 2);
                    if (dateEnding.getText().equals("")) {
                        Toast.makeText(RepeatEventActivity.this, "Некорректные введенные данные", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent.putExtra("dayEnd", dateEnding.getText().toString());
                }
                if (selectedRadio == timesEnding.getId()) {
                    intent.putExtra("end", 3);
                    if (timesCount.getText().equals("")){
                        Toast.makeText(RepeatEventActivity.this, "Некорректные введенные данные", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent.putExtra("times", timesCount.getText().toString());
                }
                intent.putExtra("rep", "1");

                intent.putExtra("start_date", bundle.getString("start_date"));
                intent.putExtra("start_time", bundle.getString("start_time"));
                intent.putExtra("end_date", bundle.getString("end_date"));
                intent.putExtra("end_time", bundle.getString("end_time"));
                intent.putExtra("time", bundle.getString("time"));
                intent.putExtra("descr", bundle.getString("descr"));
                intent.putExtra("loc", bundle.getString("loc"));
                startActivity(intent);
            }
        });
    }
}
