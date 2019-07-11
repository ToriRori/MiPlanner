package com.example.miplanner.Activities.Event;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TimePicker;

import com.example.miplanner.R;
import com.google.ical.values.Frequency;
import com.google.ical.values.RRule;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class EventService {

    protected String getRrule(Button repeatbtn, Bundle bundle) {
        String rrule = "";

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
                rrule = bundle.getString("rrule");
                break;
        }
        return rrule;
    }

    protected void initEventsFields(DatePicker dpStart, DatePicker dpEnd, TimePicker tpStart, TimePicker tpEnd, Calendar startDate, Calendar endDate, String name,
                                    EditText nameText, String descr, EditText descriptionText, String loc, EditText locationText, String rrule, Button repeatbtn) {
        if (startDate != null) {
            dpStart.init(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH), null);
            tpStart.setHour(startDate.get(Calendar.HOUR_OF_DAY));
            tpStart.setMinute(startDate.get(Calendar.MINUTE));
        }

        if (endDate != null) {
            dpEnd.init(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), null);
            tpEnd.setHour(endDate.get(Calendar.HOUR_OF_DAY));
            tpEnd.setMinute(endDate.get(Calendar.MINUTE));
        }

        if (name != null) {
            nameText.setText(name);
        }

        if (descr != null) {
            descriptionText.setText(descr);
        }

        if (loc != null) {
            locationText.setText(loc);
        }

        if (rrule != null) {
            RRule rule = null;
            try {
                rule = new RRule("RRULE:"+rrule);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (rule.getCount() == 0 && rule.getUntil() == null) {
                if (rule.getFreq() == Frequency.DAILY && rule.getInterval() == 1)
                    repeatbtn.setText("Каждый день");
                else if (rule.getFreq() == Frequency.WEEKLY && rule.getInterval() == 1)
                    repeatbtn.setText("Каждую неделю");
                else if (rule.getFreq() == Frequency.MONTHLY && rule.getInterval() == 1)
                    repeatbtn.setText("Каждый месяц");
                else if (rule.getFreq() == Frequency.YEARLY && rule.getInterval() == 1)
                    repeatbtn.setText("Каждый год");
                else
                    repeatbtn.setText("Другое...");
            }
            else
                repeatbtn.setText("Другое...");
        }
    }

    protected Calendar getEnd(Calendar calendar, RRule r) {
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
        else
            cal.set(r.getUntil().day(), r.getUntil().month(), r.getUntil().year());

        return cal;
    }
}
