package com.example.miplanner.Fragments.Calendar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.AsyncLayoutInflater;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miplanner.Activities.Event.AddEventActivity;
import com.example.miplanner.Activities.Event.EditEventActivity;
import com.example.miplanner.Activities.InfoTask.InfoEventActivity;
import com.example.miplanner.Activities.ShareActivity;
import com.example.miplanner.Data.CalendarDbHelper;
import com.example.miplanner.Fragments.OnSwipeTouchListener;
import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumEventsInstances;
import com.example.miplanner.POJO.DatumPatterns;
import com.example.miplanner.POJO.EventsInstances;
import com.example.miplanner.POJO.Patterns;
import com.example.miplanner.R;
import com.example.miplanner.RetrofitClient;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CalendarController extends Fragment implements CalendarPickerController {

    private CalendarDbHelper mDbHelper;
    private static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1001;

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
    ProgressBar progressBar;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);

        ButterKnife.bind(getActivity());
        mAuth = FirebaseAuth.getInstance();
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

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
        ImageButton btn_export = view.findViewById(R.id.button_export);
        btn_export.setOnClickListener(exportListener);
        ImageButton btn_share = view.findViewById(R.id.button_share);
        btn_share.setOnClickListener(shareListener);

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
            startActivity(intent);
            getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);
        }
    };

    View.OnClickListener shareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ShareActivity.class);
            Bundle bundle = new Bundle();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            bundle.putString("start", format.format(minDate.getTime()));
            bundle.putString("end", format.format(maxDate.getTime()));
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "You grant write external storage permission. Please click original button again to continue.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    View.OnClickListener exportListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final RetrofitClient retrofitClient = RetrofitClient.getInstance();
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    retrofitClient.getTransfersRepository().getCalendar(tokenID).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String cal = null;
                            try {
                                cal = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            // If do not grant write external storage permission.
                            if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
                            {
                                // Request user to grant write external storage permission.
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                            }

                            Calendar calendar = new GregorianCalendar();
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
                            try {
                                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()  + "/MiCalendar"+format.format(calendar.getTime())+".ics");
                                file.createNewFile();
                                FileWriter fileWriter = new FileWriter(file);
                                fileWriter.write(cal);
                                fileWriter.flush();
                                fileWriter.close();
                                Toast.makeText(getContext(), "Календарь был успешно сохранен", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });

                }
            });
        }
    };

    public void getEvents() {
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                tokenID = task.getResult().getToken();

                retrofitClient.getEventRepository().getInstancesByInterval(minDate.getTimeInMillis(), maxDate.getTimeInMillis(), tokenID).enqueue(new Callback<EventsInstances>() {
                    @Override
                    public void onResponse(Call<EventsInstances> call, Response<EventsInstances> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                eventList.clear();
                                evsInst = Arrays.asList(response.body().getData());
                                if (evsInst.size() == 0)
                                    progressBar.setVisibility(View.GONE);
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
                    String rrule = patt.get(0).getRrule();
                    DrawableCalendarEvent event = new DrawableCalendarEvent(evsInst.get(fi).getEventId(), ContextCompat.getColor(getActivity(), R.color.calendar_text_first_day_of_month),
                            evs.getName(), evs.getDetails(), evs.getLocation(), rrule, cal1, cal2, false, null);
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
                            evs.getName(), evs.getDetails(), evs.getLocation(), "", cal1, cal2, false, null);
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
            progressBar.setVisibility(View.VISIBLE);
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    RetrofitClient retrofitClient = RetrofitClient.getInstance();
                    retrofitClient.getEventPatternRepository().getPatternsById(event.getId(), tokenID).enqueue(new Callback<Patterns>() {
                        @Override
                        public void onResponse(Call<Patterns> call, Response<Patterns> response) {
                            List<DatumPatterns> patts = Arrays.asList(response.body().getData());
                            Intent intent = new Intent(getActivity(), InfoEventActivity.class);
                            Bundle bundle = new Bundle();
                            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                            Calendar calStart = new GregorianCalendar();
                            calStart.setTimeInMillis(patts.get(0).getStartedAt());
                            Calendar calEnd = new GregorianCalendar();
                            calEnd.setTimeInMillis(patts.get(0).getStartedAt()+patts.get(0).getDuration());
                            bundle.putString("name", event.getTitle());
                            bundle.putString("description", event.getDescription());
                            bundle.putString("location", event.getLocation());
                            bundle.putString("time_start", format.format(calStart.getTime()));
                            bundle.putString("time_end",format.format(calEnd.getTime()));
                            bundle.putString("time_end_current", format.format(event.getEndTime().getTime()));
                            bundle.putString("rrule", event.getRrule());
                            bundle.putLong("event_id", event.getId());

                            intent.putExtras(bundle);
                            progressBar.setVisibility(View.GONE);
                            startActivity(intent);
                            getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);
                        }

                        @Override
                        public void onFailure(Call<Patterns> call, Throwable t) {

                        }
                    });
                }
            });
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
                    progressBar.setVisibility(View.VISIBLE);
                    RetrofitClient retrofitClient = RetrofitClient.getInstance();
                    retrofitClient.getEventRepository().delete(event.getId(), tokenID).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            start = null;
                            progressBar.setVisibility(View.GONE);
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
        progressBar.setVisibility(View.VISIBLE);
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

                        bundle.putString("rrule", patt.get(0).getRrule());
                        intent.putExtras(bundle);
                        progressBar.setVisibility(View.GONE);
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
