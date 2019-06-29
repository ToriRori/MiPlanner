package com.example.miplanner.Fragments.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.miplanner.Activities.AddEventActivity;
import com.example.miplanner.Activities.EditEventActivity;
import com.example.miplanner.Data.CalendarDbHelper;
import com.example.miplanner.Fragments.OnSwipeTouchListener;
import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumEventsInstances;
import com.example.miplanner.POJO.DatumPatterns;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.EventsInstances;
import com.example.miplanner.POJO.Patterns;
import com.example.miplanner.R;
import com.example.miplanner.RetrofitClient;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.ical.values.DateValue;
import com.google.ical.values.RRule;
import com.google.ical.values.WeekdayNum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CalendarController extends Fragment implements CalendarPickerController {

    private CalendarDbHelper mDbHelper;

    @Bind(R.id.agenda_calendar_view)
    AgendaCalendarView mAgendaCalendarView;

    Calendar minDate = Calendar.getInstance();
    Calendar maxDate = Calendar.getInstance();
    List<DatumEventsInstances> evsInst = null;
    List<DatumEvents> evs = null;
    List<DatumPatterns> patt = null;
    List<CalendarEvent> eventList = new ArrayList<>();

    String start = null;
    String tokenID = null;

    FirebaseAuth mAuth;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        ButterKnife.bind(getActivity());
        Bundle bundle = getArguments();
        mAuth = FirebaseAuth.getInstance();
        tokenID = bundle.getString("token");

        minDate.set(Calendar.MONTH, 0);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        minDate.set(Calendar.HOUR_OF_DAY, 0);
        maxDate.set(Calendar.MONTH, 11);
        maxDate.set(Calendar.DAY_OF_MONTH, 31);
        maxDate.set(Calendar.HOUR_OF_DAY, 23);

        TextView yearText = view.findViewById(R.id.year_text);
        yearText.setText(Integer.toString(minDate.get(Calendar.YEAR)));

        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                minDate.add(Calendar.YEAR, -1);
                maxDate.add(Calendar.YEAR, -1);
                start = null;
                refreshItems();
            }


            public void onSwipeLeft() {
                minDate.add(Calendar.YEAR, 1);
                maxDate.add(Calendar.YEAR, 1);
                start = null;
                refreshItems();
            }

        });

        if (start == null) {
            getEvents();
        }

        /*mDbHelper = new CalendarDbHelper(getActivity());


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
        }*/

        mAgendaCalendarView = view.findViewById(R.id.agenda_calendar_view);

        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
        mAgendaCalendarView.addEventRenderer(new DrawableEventRenderer());

        Button btn_add = view.findViewById(R.id.button_add);
        btn_add.setOnClickListener(addListener);

        return view;
    }

    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            DayItem day = mAgendaCalendarView.getSelectedDay();
            Date date = day.getDate();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            intent.putExtra("day", format.format(date));
            intent.putExtra("token", tokenID);
            startActivity(intent);
            getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);
        }
    };

    public void getEvents() {
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getEventRepository().getInstancesByInterval(minDate.getTimeInMillis(), maxDate.getTimeInMillis(), tokenID).enqueue(new Callback<EventsInstances>() {
            @Override
            public void onResponse(Call<EventsInstances> call, Response<EventsInstances> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        eventList.clear();
                        evsInst = Arrays.asList(response.body().getData());
                        for (int i = 0; i < evsInst.size(); i++) {
                            final int fi = i;
                            retrofitClient.getEventRepository().getEventsById(new Long[]{evsInst.get(i).getEventId()}, tokenID).enqueue(new Callback<com.example.miplanner.POJO.Events>() {
                                @Override
                                public void onResponse(Call<com.example.miplanner.POJO.Events> call, Response<com.example.miplanner.POJO.Events> response) {
                                    if (response.isSuccessful()) {
                                        evs = Arrays.asList(response.body().getData());
                                        getEventsPatterns(fi, evs.get(0));
                                    } else
                                        evs = null;
                                }

                                @Override
                                public void onFailure(Call<com.example.miplanner.POJO.Events> call, Throwable t) {

                                }
                            });
                        }
                    }
                } else
                    evs = null;
            }

            @Override
            public void onFailure(Call<EventsInstances> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public void getEventsPatterns(final int fi, final DatumEvents evs) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getEventPatternRepository().getPatternsById(evsInst.get(fi).getEventId(), tokenID).enqueue(new Callback<Patterns>() {
            @Override
            public void onResponse(Call<Patterns> call, Response<Patterns> response) {
                if (response.isSuccessful()&&(response.body().getData() != null)) {
                    patt = Arrays.asList(response.body().getData());
                    Calendar cal1 = new GregorianCalendar();
                    Calendar cal2 = new GregorianCalendar();
                    cal1.setTimeInMillis(evsInst.get(fi).getStartedAt());
                    cal2.setTimeInMillis(evsInst.get(fi).getEndedAt());
                    RRule rule = null;
                    String day = "*";
                    String week = "*";
                    String month = "*";
                    String days = "*";
                    String year = "*";
                    try {
                        rule = new RRule("RRULE:"+patt.get(0).getRrule());
                        rule.toIcal();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    switch (rule.getFreq()) {
                        case DAILY:
                            day = Integer.toString(rule.getInterval());
                            break;
                        case WEEKLY:
                            week = Integer.toString(rule.getInterval());
                            if (rule.getByDay().size() > 0) {
                                days = "";
                                for (WeekdayNum dayOfWeek : rule.getByDay())
                                    days += dayOfWeek.num + ",";
                                days = days.substring(0, days.length() - 1);
                            } else
                                days = "1,2,3,4,5,6,7";
                            break;
                        case MONTHLY:
                            month = Integer.toString(rule.getInterval());
                            break;
                        case YEARLY:
                            year = Integer.toString(rule.getInterval());
                    }
                    String rep = "* * " + day + " " + week + " " + month + " " + days + " " + year;
                    String end = "";
                    DateValue dateEnd = rule.getUntil();
                    if (dateEnd != null)
                        end = dateEnd.day() + "." + dateEnd.month() + "." + dateEnd.year();
                    else if (rule.getCount() != 0)
                        end = Integer.toString(rule.getCount());

                    DrawableCalendarEvent event = new DrawableCalendarEvent(evsInst.get(fi).getEventId(), ContextCompat.getColor(getActivity(), R.color.calendar_text_first_day_of_month),
                            evs.getName(), evs.getDetails(), evs.getLocation(), rep, end, cal1, cal2, false, null);
                    eventList.add(event);
                    if (eventList.size() == evsInst.size()) {
                        start = "ok";
                        refreshItems();
                    }

                } else {
                    patt = null;
                    Calendar cal1 = new GregorianCalendar();
                    Calendar cal2 = new GregorianCalendar();
                    cal1.setTimeInMillis(evsInst.get(fi).getStartedAt());
                    cal2.setTimeInMillis(evsInst.get(fi).getEndedAt());
                    DrawableCalendarEvent event = new DrawableCalendarEvent(evsInst.get(fi).getEventId(), ContextCompat.getColor(getActivity(), R.color.calendar_text_first_day_of_month),
                            evs.getName(), evs.getDetails(), evs.getLocation(), "", "", cal1, cal2, false, null);
                    eventList.add(event);
                    if (eventList.size() == evsInst.size()) {
                        start = "ok";
                        refreshItems();
                    }
                }
            }

            @Override
            public void onFailure(Call<Patterns> call, Throwable t) {

            }
        });
    }


    @Override
    public void onDaySelected(DayItem dayItem) {
    }

    @Override
    public void onEventSelected(final CalendarEvent event) {
        if (!event.getTitle().equals("No events")) {
            LayoutInflater layoutInflater
                    = (LayoutInflater)getActivity()
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.event_info, null);
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);

            TextView txtTitle = popupView.findViewById(R.id.event_title);
            final TextView txtDate1 = popupView.findViewById(R.id.event_date1);
            final TextView txtDate2 = popupView.findViewById(R.id.event_date2);
            final TextView txtDescription = popupView.findViewById(R.id.event_description);
            final LinearLayout layoutLocation = popupView.findViewById(R.id.location_layout);
            final TextView txtLocation = popupView.findViewById(R.id.event_location);

            txtTitle.setText(event.getTitle());

            //Event ev_real = mDbHelper.getEventById((int) event.getId());

            Calendar cal1 = new GregorianCalendar();
            Calendar cal2 = new GregorianCalendar();
            cal1.setTime(event.getStartTime().getTime());
            cal2.setTime(event.getEndTime().getTime());

            SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            txtDate1.setText("Начало события: " + format1.format(cal1.getTime()) + "  " + format2.format(cal1.getTime()));
            txtDate2.setText("Конец события: " + format1.format(cal2.getTime()) + "  " + format2.format(cal2.getTime()));

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

            /*SimpleDateFormat format = new SimpleDateFormat("d.MM.yyyy HH:mm");
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
            }*/


            Button btnDismiss = popupView.findViewById(R.id.dismiss);
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
            final Button btnEdit = popupView.findViewById(R.id.edit_btn);
            btnEdit.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    goToEdit(event);
                }});
            Button btnDelete = popupView.findViewById(R.id.delete_btn);
            btnDelete.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //mDbHelper.deleteEventById((int) event.getId());
                    RetrofitClient retrofitClient = RetrofitClient.getInstance();
                    retrofitClient.getEventRepository().delete(event.getId(), tokenID).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            start = null;
                            refreshItems();
                            popupWindow.dismiss();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }});
            popupWindow.showAtLocation(popupView,  Gravity.CENTER, 0, 0);
        }
    }

    public void goToEdit(final CalendarEvent event) {
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getEventRepository().getInstancesById(new Long[]{event.getId()}, tokenID).enqueue(new Callback<EventsInstances>() {
            @Override
            public void onResponse(Call<EventsInstances> call, Response<EventsInstances> response) {
                List<DatumEventsInstances> evsInst = Arrays.asList(response.body().getData());
                retrofitClient.getEventPatternRepository().getPatternsById(evsInst.get(0).getEventId(), tokenID).enqueue(new Callback<Patterns>() {
                    @Override
                    public void onResponse(Call<Patterns> call, Response<Patterns> response) {
                        List<DatumPatterns> patt = Arrays.asList(response.body().getData());
                        Intent intent = new Intent(getActivity(), EditEventActivity.class);
                        Bundle bundle = new Bundle();

                        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
                        SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                        Calendar cal1 = new GregorianCalendar();
                        Calendar cal2 = new GregorianCalendar();
                        cal1.setTimeInMillis(patt.get(0).getStartedAt());
                        cal2.setTimeInMillis(patt.get(0).getStartedAt()+patt.get(0).getDuration());

                        bundle.putLong("event_id", event.getId());
                        bundle.putString("name", event.getTitle());
                        bundle.putString("descr", ((DrawableCalendarEvent)event).getDescription());
                        bundle.putString("loc", ((DrawableCalendarEvent)event).getLocation());
                        bundle.putString("start_date", format1.format(cal1.getTime()));
                        bundle.putString("start_time", format2.format(cal1.getTime()));
                        bundle.putString("end_date", format1.format(cal2.getTime()));
                        bundle.putString("end_time", format2.format(cal2.getTime()));
                        bundle.putString("token", tokenID);

                        RRule rule = null;
                        String day = "*";
                        String week = "*";
                        String month = "*";
                        String days = "*";
                        String year = "*";
                        try {
                            rule = new RRule("RRULE:"+patt.get(0).getRrule());
                            rule.toIcal();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        switch (rule.getFreq()) {
                            case DAILY:
                                day = Integer.toString(rule.getInterval());
                                break;
                            case WEEKLY:
                                week = Integer.toString(rule.getInterval());
                                if (rule.getByDay().size() > 0) {
                                    days = "";
                                    for (WeekdayNum dayOfWeek : rule.getByDay())
                                        days += dayOfWeek.num + ",";
                                    days = days.substring(0, days.length() - 1);
                                } else
                                    days = "1,2,3,4,5,6,7";
                                break;
                            case MONTHLY:
                                month = Integer.toString(rule.getInterval());
                                break;
                            case YEARLY:
                                year = Integer.toString(rule.getInterval());
                        }
                        String rep = "* * " + day + " " + week + " " + month + " " + days + " " + year;
                        String end = "";
                        DateValue dateEnd = rule.getUntil();
                        if (dateEnd != null)
                            end = dateEnd.day() + "." + dateEnd.month() + "." + dateEnd.year();
                        else if (rule.getCount() != 0)
                            end = Integer.toString(rule.getCount());


                        bundle.putString("rep", rep);
                        bundle.putString("end_rep", end);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);
                    }

                    @Override
                    public void onFailure(Call<Patterns> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<EventsInstances> call, Throwable t) {

            }
        });
    }

    public void refreshItems(){
        final android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.attach(this);
        fragmentTransaction.commit();
    }

    @Override
    public void onScrollToDate(Calendar calendar) {
    }
}
