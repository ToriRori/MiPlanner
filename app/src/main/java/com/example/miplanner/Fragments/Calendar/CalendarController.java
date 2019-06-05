package com.example.miplanner.Fragments.Calendar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.miplanner.Activities.AddEventActivity;
import com.example.miplanner.Activities.EditEventActivity;
import com.example.miplanner.Data.CalendarDbHelper;
import com.example.miplanner.Data.Events;
import com.example.miplanner.R;
import com.example.miplanner.Event;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CalendarController extends Fragment implements CalendarPickerController {

    Event[] events;
    int size;
    private CalendarDbHelper mDbHelper;

    @Bind(R.id.agenda_calendar_view)
    AgendaCalendarView mAgendaCalendarView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        ButterKnife.bind(getActivity());
        //setSupportActionBar(mToolbar);

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

        mDbHelper = new CalendarDbHelper(getActivity());

        List<CalendarEvent> eventList = new ArrayList<>();

        Calendar cal = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy");

        if (getArguments() != null){
            String timeStr = getArguments().getString("Date");
            if (timeStr != null)
            {
                try {
                    cal.setTime(dateFormat.parse(timeStr));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //Parcelable[] temp = getArguments().getParcelableArray("events");
            //if (temp != null)
            //    events = Arrays.copyOf(temp, temp.length, com.example.miplanner.Event[].class);
            //size = getArguments().getInt("size");
            /*if ((size != 0) && (events != null)){
                for (int i = 0; i < size; i += 1){
                    Calendar calTStart = new GregorianCalendar();
                    Calendar calTEnd = new GregorianCalendar();
                    SimpleDateFormat format = new SimpleDateFormat("d.MM.yyyy HH:mm");
                    try {
                        calTStart.setTime(format.parse(events[i].getDateStart()));
                        calTEnd.setTime(format.parse(events[i].getDateEnd()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    format = new SimpleDateFormat("d.MM.yyyy");

                    DrawableCalendarEvent event = new DrawableCalendarEvent(events.length, ContextCompat.getColor(getActivity(), R.color.yellow),
                            events[i].getName(), events[i].getDescription(), null, calTStart, calTEnd, false, null);
                    eventList.add(event);
                }
            }*/
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            String[] projection = {
                    Events.EventAdd._ID,
                    Events.EventAdd.COLUMN_NAME,
                    Events.EventAdd.COLUMN_DESCRIPTION,
                    Events.EventAdd.COLUMN_DATE_START,
                    Events.EventAdd.COLUMN_DATE_END,
                    Events.EventAdd.COLUMN_LOCATION,
                    Events.EventAdd.COLUMN_REPEAT,
                    Events.EventAdd.COLUMN_REPEAT_END};

            Cursor cursor = db.query(
                    Events.EventAdd.TABLE_NAME,   // таблица
                    projection,            // столбцы
                    null,                  // столбцы для условия WHERE
                    null,                  // значения для условия WHERE
                    null,                  // Don't group the rows
                    null,                  // Don't filter by row groups
                    null);                   // порядок сортировки

            int idColumnIndex = cursor.getColumnIndex(Events.EventAdd._ID);
            int nameColumnIndex = cursor.getColumnIndex(Events.EventAdd.COLUMN_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(Events.EventAdd.COLUMN_DESCRIPTION);
            int startDateColumnIndex = cursor.getColumnIndex(Events.EventAdd.COLUMN_DATE_START);
            int endDateColumnIndex = cursor.getColumnIndex(Events.EventAdd.COLUMN_DATE_END);
            int locationColumnIndex = cursor.getColumnIndex(Events.EventAdd.COLUMN_LOCATION);
            int repeatColumnIndex = cursor.getColumnIndex(Events.EventAdd.COLUMN_REPEAT);
            int repeatEndColumnEndex = cursor.getColumnIndex(Events.EventAdd.COLUMN_REPEAT_END);
            int temp = 1;
            while (cursor.moveToNext()) {

                SimpleDateFormat format = new SimpleDateFormat("d.MM.yyyy HH:mm");

                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentDescription = cursor.getString(descriptionColumnIndex);
                String currentStartDate = cursor.getString(startDateColumnIndex);
                String currentEndDate = cursor.getString(endDateColumnIndex);
                String currentLocation = cursor.getString(locationColumnIndex);
                String currentRepeat = cursor.getString(repeatColumnIndex);
                String currentEndRepeat = cursor.getString(repeatEndColumnEndex);

                Calendar cal1 = new GregorianCalendar();
                Calendar cal2 = new GregorianCalendar();
                try {
                    cal1.setTime(format.parse(currentStartDate));
                    cal2.setTime(format.parse(currentEndDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DrawableCalendarEvent event = new DrawableCalendarEvent(currentID, ContextCompat.getColor(getActivity(), R.color.calendar_text_first_day_of_month),
                        currentName, currentDescription, currentLocation, currentRepeat, currentEndRepeat,  cal1, cal2, false, null);
                eventList.add(event);
            }

            NavigationView v = getActivity().findViewById(R.id.nav_view);
            v.getMenu().getItem(0).setChecked(true);
        }

        //if ((size == 0)||(mDbHelper.eventsIsEmpty()))
        //    mockList(eventList);

        mAgendaCalendarView = view.findViewById(R.id.agenda_calendar_view);

        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
        mAgendaCalendarView.addEventRenderer(new DrawableEventRenderer());

        Button btn_add = view.findViewById(R.id.button_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEventActivity.class);
                //Bundle bundle = new Bundle();
                //bundle.putParcelableArray("events", events);
                //bundle.putInt("size", size);
                //intent.putExtras(bundle);
                //intent.putExtra("events", connect);
                //intent.putExtra("size", sizeEv);
                startActivity(intent);
                getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);
            }
        });

        return view;
    }

    @Override
    public void onDaySelected(DayItem dayItem) {
        //Log.d(LOG_TAG, String.format("Selected day: %s", dayItem));
    }

    @Override
    public void onEventSelected(CalendarEvent event) {
        //Log.d(LOG_TAG, String.format("Selected event: %s", event));
        if (!event.getTitle().equals("No events")) {
            LayoutInflater layoutInflater
                    = (LayoutInflater)getActivity()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.event_info, null);
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);

            TextView txtTitle = (TextView) popupView.findViewById(R.id.event_title);
            TextView txtDate1 = (TextView) popupView.findViewById(R.id.event_date1);
            TextView txtDate2 = (TextView) popupView.findViewById(R.id.event_date2);
            TextView txtDescription = (TextView) popupView.findViewById(R.id.event_description);
            LinearLayout layoutLocation = (LinearLayout) popupView.findViewById(R.id.location_layout);
            TextView txtLocation = (TextView) popupView.findViewById(R.id.event_location);

            txtTitle.setText(((BaseCalendarEvent)event).getTitle());

            Event ev_real = mDbHelper.getEventById((int) event.getId());

            SimpleDateFormat format = new SimpleDateFormat("d.MM.yyyy HH:mm");
            Calendar cal1 = new GregorianCalendar();
            Calendar cal2 = new GregorianCalendar();

            try {
                cal1.setTime(format.parse(ev_real.getDateStart()));
                cal2.setTime(format.parse(ev_real.getDateEnd()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            txtDate1.setText("Начало события: " + format1.format(cal1.getTime()) + "  " + format2.format(cal1.getTime()));
            txtDate2.setText("Конец события: " + format1.format(cal2.getTime()) + "  " + format2.format(cal2.getTime()));

            //if (((BaseCalendarEvent) event).isAllDay()) {
            //    txtTime.setText("Событие длится весь день");
            //} else {
            //}
            DrawableCalendarEvent temp = (DrawableCalendarEvent) event;
            txtDescription.setText(((DrawableCalendarEvent)event).getDescription());
            if (temp.getLocation() != null) {
                txtLocation.setText(temp.getLocation());
                if (temp.getLocation().length() > 0) {
                    layoutLocation.setVisibility(View.VISIBLE);
                    txtLocation.setText(temp.getLocation());
                } else {
                    layoutLocation.setVisibility(View.GONE);
                }
            }


            Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
            btnDismiss.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    popupWindow.dismiss();
                }});
            popupWindow.showAtLocation(popupView,  Gravity.CENTER, 0, 0);
        }
        else {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            Bundle bundle = new Bundle();
            //bundle.putParcelableArray("events", events);
            //bundle.putInt("size", size);
            Date cal = event.getDayReference().getDate();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            bundle.putString("day", format.format(cal));
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);
        }
    }

    @Override
    public void onEventSelectedLong(final CalendarEvent event) {
        if (!event.getTitle().equals("No events")) {
            LayoutInflater layoutInflater
                    = (LayoutInflater)getActivity()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.event_actions, null);
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);

            Button btnEdit = (Button)popupView.findViewById(R.id.edit_btn);
            btnEdit.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(getActivity(), EditEventActivity.class);
                    Bundle bundle = new Bundle();
                    //bundle.putParcelableArray("events", events);
                    //bundle.putInt("size", size);
                    bundle.putLong("event_id", event.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().overridePendingTransition (R.anim.enter, R.anim.exit); }});
            Button btnDelete = (Button)popupView.findViewById(R.id.delete_btn);
            btnDelete.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    mDbHelper.deleteEventById((int) event.getId());
                    refreshItems();
                    popupWindow.dismiss();
                }});
            popupWindow.showAtLocation(popupView,  Gravity.CENTER, 0, 0);
        }
    }

    public void refreshItems(){
        final android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.attach(this);
        fragmentTransaction.commit();
    }

    @Override
    public void onScrollToDate(Calendar calendar) {
        //if (getSupportActionBar() != null) {
        //    getSupportActionBar().setTitle(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        //}
    }
}
