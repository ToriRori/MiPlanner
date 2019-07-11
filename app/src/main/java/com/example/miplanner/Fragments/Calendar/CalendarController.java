package com.example.miplanner.Fragments.Calendar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CalendarController extends Fragment implements CalendarPickerController {

    private static final int FILE_SELECT_CODE = 1003;
    private static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1001;
    private static int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 1002;

    @Bind(R.id.agenda_calendar_view)
    AgendaCalendarView mAgendaCalendarView;

    Calendar minDate = Calendar.getInstance();
    Calendar maxDate = Calendar.getInstance();
    List<DatumEventsInstances> evsInst = null;
    List<DatumEvents> evs = null;
    List<DatumPatterns> patt = null;
    List<CalendarEvent> eventList = new ArrayList<>();

    boolean flagIsLoaded = false;
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
                flagIsLoaded = false;
                refreshItems();
            }


            public void onSwipeLeft() {
                minDate.add(Calendar.YEAR, 1);
                maxDate.add(Calendar.YEAR, 1);
                flagIsLoaded = false;
                refreshItems();
            }

        });

        if (!flagIsLoaded) {
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
        ImageButton btn_import = view.findViewById(R.id.button_import);
        btn_import.setOnClickListener(importListener);
        ImageButton btn_refresh = view.findViewById(R.id.button_refresh);
        btn_refresh.setOnClickListener(refreshListener);
        return view;
    }

    private void badRequest(String message) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener refreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            refreshItems();
        }
    };

    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(mAgendaCalendarView.getSelectedDay().getDate());
            intent.putExtra("day", calendar);
            startActivity(intent);
            getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);
        }
    };

    View.OnClickListener shareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ShareActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("start", minDate);
            bundle.putSerializable("end", maxDate);
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);
        }
    };

    View.OnClickListener importListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getContext(), "Установите файловый менеджер",
                        Toast.LENGTH_SHORT).show();
            }

            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION
                );
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = null;

                    if (DocumentsContract.isDocumentUri(getContext().getApplicationContext(), uri)) {
                        if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                            final String docId = DocumentsContract.getDocumentId(uri);
                            final String[] split = docId.split(":");
                            path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                            final String id = DocumentsContract.getDocumentId(uri);
                            String[] temp = id.split("/", 2);
                            path = temp[1];
                            //uri = ContentUris.withAppendedId(
                            //        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                            final String docId = DocumentsContract.getDocumentId(uri);
                            final String[] split = docId.split(":");
                            final String type = split[0];
                            if ("image".equals(type)) {
                                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            } else if ("video".equals(type)) {
                                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            } else if ("audio".equals(type)) {
                                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            }
                        }
                    }
                    if ("file".equalsIgnoreCase(uri.getScheme())) {
                        path = uri.getPath();
                    }
                    final File file = new File(path);
                    mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            tokenID = task.getResult().getToken();
                            RetrofitClient retrofitClient = RetrofitClient.getInstance();
                            RequestBody requestFile =
                                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
                            MultipartBody.Part body =
                                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                            retrofitClient.getTransfersRepository().sendCalendar(body, tokenID).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    try {
                                        Log.d("_____________", response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (response.isSuccessful())
                                        badRequest("Календарь был успешно сохранен");
                                    else
                                        badRequest("Не удалось сохранить календарь");
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    badRequest("Не удалось сохранить календарь");
                                }
                            });
                        }
                    });
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Разрешение было успешно получено, пожалуйста нажмите кнопку снова", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Не удалось получить разрешение", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted!");
                Toast.makeText(getContext(), "Разрешение было успешно получено, пожалуйста нажмите кнопку снова", Toast.LENGTH_LONG).show();
            } else {
                Log.i(TAG, "Permission denied");
                Toast.makeText(getContext(), "Не удалось получить разрешение", Toast.LENGTH_LONG).show();
            }
        }
    }

    View.OnClickListener exportListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getIdToken(false) == null) {
                mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (!task.isSuccessful()) {
                            badRequest("Не удалось сохранить календарь");
                            return;
                        }
                        tokenID = task.getResult().getToken();
                        exportCalendar();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        badRequest("Не удалось сохранить календарь");
                    }
                });
            }
            else {
                tokenID = mAuth.getCurrentUser().getIdToken(false).getResult().getToken();
                exportCalendar();
            }
        }
    };

    private void exportCalendar() {
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getTransfersRepository().getCalendar(tokenID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    badRequest("Не удалось сохранить календарь");
                    return;
                }
                String cal = null;
                try {
                    cal = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int writeExternalStoragePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);


                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
                try {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()  +
                            "/MiCalendar"+format.format(calendar.getTime())+".ics");
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
                badRequest("Не удалось сохранить календарь");
            }
        });
    }

    private void getEvents() {
        progressBar.setVisibility(View.VISIBLE);
        if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getIdToken(false) == null) {
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (!task.isSuccessful()) {
                        badRequest("Не удалось получить события");
                        return;
                    }
                    tokenID = task.getResult().getToken();
                    getInstances();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    badRequest("Не удалось получить события");
                }
            });
        }
        else {
            tokenID = mAuth.getCurrentUser().getIdToken(false).getResult().getToken();
            getInstances();
        }
    }

    private void getInstances() {
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getEventRepository().getInstancesByInterval(minDate.getTimeInMillis(), maxDate.getTimeInMillis(), tokenID).enqueue(
                new Callback<EventsInstances>() {
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
                            retrofitClient.getEventRepository().getEventsById(new Long[]{evsInst.get(i).getEventId()},
                                    tokenID).enqueue(new Callback<com.example.miplanner.POJO.Events>() {
                                @Override
                                public void onResponse(Call<com.example.miplanner.POJO.Events> call,
                                                       Response<com.example.miplanner.POJO.Events> response) {
                                    if (response.isSuccessful()) {
                                        evs = Arrays.asList(response.body().getData());
                                        getEventsPatterns(evsInst.get(fi), evs.get(0));
                                    } else {
                                        badRequest("Не удалось получить события");
                                    }
                                }

                                @Override
                                public void onFailure(Call<com.example.miplanner.POJO.Events> call, Throwable t) {
                                    badRequest("Не удалось получить события");
                                }
                            });
                        }
                    }
                } else {
                    badRequest("Не удалось получить события");
                }
            }

            @Override
            public void onFailure(Call<EventsInstances> call, Throwable throwable) {
                badRequest("Не удалось получить события");
                throwable.printStackTrace();
            }
        });
    }


    private void getEventsPatterns(final DatumEventsInstances instEvs, final DatumEvents evs) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getEventPatternRepository().getPatternsById(instEvs.getEventId(), tokenID).enqueue(new Callback<Patterns>() {
            @Override
            public void onResponse(Call<Patterns> call, Response<Patterns> response) {
                if (response.isSuccessful()&&(response.body().getData() != null)) {
                    patt = Arrays.asList(response.body().getData());

                    Calendar cal1 = new GregorianCalendar();
                    Calendar cal2 = new GregorianCalendar();
                    cal1.setTimeZone(TimeZone.getTimeZone(ZoneId.of(patt.get(0).getTimezone())));
                    cal2.setTimeZone(TimeZone.getTimeZone(ZoneId.of(patt.get(0).getTimezone())));
                    cal1.setTimeInMillis(instEvs.getStartedAt());
                    cal2.setTimeInMillis(instEvs.getEndedAt());
                    cal1.setTimeZone(TimeZone.getDefault());
                    cal2.setTimeZone(TimeZone.getDefault());

                    Calendar calStart = new GregorianCalendar();
                    Calendar calEnd = new GregorianCalendar();
                    calStart.setTimeInMillis(patt.get(0).getStartedAt());
                    calEnd.setTimeInMillis(patt.get(0).getEndedAt());
                    calStart.setTimeZone(TimeZone.getTimeZone(ZoneId.of(patt.get(0).getTimezone())));
                    calEnd.setTimeZone(TimeZone.getTimeZone(ZoneId.of(patt.get(0).getTimezone())));
                    calStart.setTimeInMillis(patt.get(0).getStartedAt());
                    calEnd.setTimeInMillis(patt.get(0).getEndedAt());
                    calStart.setTimeZone(TimeZone.getDefault());
                    calEnd.setTimeZone(TimeZone.getDefault());

                    DrawableCalendarEvent event;

                    if (!evs.getOwnerId().equals(mAuth.getCurrentUser().getUid()))
                        event = new DrawableCalendarEvent(instEvs.getEventId(), ContextCompat.getColor(getActivity(), R.color.theme_accent),
                                evs.getName(), evs.getOwnerId(), evs.getDetails(), evs.getLocation(), patt.get(0).getRrule(), cal1, cal2, calStart, calEnd,
                                false, cal2.getTimeInMillis()-cal1.getTimeInMillis());
                    else
                        event = new DrawableCalendarEvent(instEvs.getEventId(), ContextCompat.getColor(getActivity(), R.color.calendar_text_first_day_of_month),
                                evs.getName(), evs.getOwnerId(), evs.getDetails(), evs.getLocation(), patt.get(0).getRrule(), cal1, cal2, calStart, calEnd,
                                false, cal2.getTimeInMillis()-cal1.getTimeInMillis());

                    eventList.add(event);
                    if (eventList.size() == evsInst.size()) {
                        flagIsLoaded = true;
                        refreshItems();
                    }

                } else {
                    badRequest("Не удалось получить события");
                }
            }

            @Override
            public void onFailure(Call<Patterns> call, Throwable t) {
                badRequest("Не удалось получить события");
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
            Intent intent = new Intent(getActivity(), InfoEventActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("name", event.getTitle());
            bundle.putString("description", event.getDescription());
            bundle.putString("location", event.getLocation());
            bundle.putSerializable("time_start", event.getDateStartGlobal());
            bundle.putSerializable("time_end", event.getDateEndGlobal());
            bundle.putSerializable("time_start_current", event.getStartTime());
            bundle.putSerializable("time_end_current", event.getEndTime());
            bundle.putString("owner", event.getOwner());
            bundle.putString("rrule", event.getRrule());
            bundle.putLong("event_id", event.getId());

            intent.putExtras(bundle);
            progressBar.setVisibility(View.GONE);
            startActivity(intent);
            getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);
        }
        else {
            Intent intent = new Intent(getActivity(), AddEventActivity.class);
            Bundle bundle = new Bundle();
            Calendar cal = new GregorianCalendar();
            cal.setTime(event.getDayReference().getDate());
            bundle.putSerializable("day", cal);
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
                    progressBar.setVisibility(View.VISIBLE);
                    if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getIdToken(false) == null) {
                        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            @Override
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (!task.isSuccessful()) {
                                    badRequest("Не удалось удалить событие");
                                    return;
                                }
                                tokenID = task.getResult().getToken();
                                deleteEvent(event.getId(), popupWindow);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                badRequest("Не удалось удалить событие");
                            }
                        });
                    }
                    else {
                        tokenID = mAuth.getCurrentUser().getIdToken(false).getResult().getToken();
                        deleteEvent(event.getId(), popupWindow);
                    }
                }});
            Button btnShare = popupView.findViewById(R.id.share_btn);
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ShareActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", event.getId());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);
                }
            });
            popupWindow.showAtLocation(popupView,  Gravity.CENTER, 0, 0);
        }
    }

    private void deleteEvent(long id, final PopupWindow popupWindow) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getEventRepository().delete(id, tokenID).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    badRequest("Не удалось удалить событие");
                    return;
                }
                flagIsLoaded = false;
                refreshItems();
                popupWindow.dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                badRequest("Не удалось удалить событие");
            }
        });
    }

    private void goToEdit(final CalendarEvent event) {
        Intent intent = new Intent(getActivity(), EditEventActivity.class);
        Bundle bundle = new Bundle();

        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(event.getDateStartGlobal().getTimeInMillis()+event.getDuration());

        bundle.putLong("event_id", event.getId());
        bundle.putString("name", event.getTitle());
        bundle.putString("descr", ((DrawableCalendarEvent)event).getDescription());
        bundle.putString("loc", ((DrawableCalendarEvent)event).getLocation());
        bundle.putSerializable("start_date", event.getDateStartGlobal());
        bundle.putSerializable("end_date", cal);
        bundle.putString("rrule", event.getRrule());

        intent.putExtras(bundle);
        progressBar.setVisibility(View.GONE);
        startActivity(intent);
        getActivity().overridePendingTransition (R.anim.enter, R.anim.exit);

    }

    private void refreshItems(){
        final android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.attach(this);
        fragmentTransaction.commit();

    }

    @Override
    public void onScrollToDate(Calendar calendar) {
    }

}
