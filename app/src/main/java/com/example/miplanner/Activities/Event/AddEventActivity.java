package com.example.miplanner.Activities.Event;

import android.content.Intent;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.ical.values.RRule;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddEventActivity extends AppCompatActivity {

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
        CardView googlePlaces = findViewById(R.id.idCardView);
        googlePlaces.setVisibility(View.GONE);

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
                EditText descriptionText = findViewById(R.id.descriptionText);
                EditText locationText = findViewById(R.id.locationText);

                nameEvent.clearFocus();
                descriptionText.clearFocus();
                locationText.clearFocus();

                EventService service = new EventService();
                String rrule = service.getRrule(repeatbtn, getIntent().getExtras());

                //check dates correct
                Calendar calStart = new GregorianCalendar();
                Calendar calEnd = new GregorianCalendar();
                calStart.set(dpStart.getYear(), dpStart.getMonth(), dpStart.getDayOfMonth(), tpStart.getHour(), tpStart.getMinute());
                calEnd.set(dpEnd.getYear(), dpEnd.getMonth(), dpEnd.getDayOfMonth(), tpEnd.getHour(), tpEnd.getMinute());

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
                        Toast.makeText(AddEventActivity.this, "Некорректное повторение события для данных дат начала и конца",
                                Toast.LENGTH_SHORT).show();
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


    private void initFields() {
        final Button repeatbtn = findViewById(R.id.buttonRepeat);
        TimePicker tpStart = findViewById(R.id.timePickerStart);
        DatePicker dpStart = findViewById(R.id.datePickerStart);
        TimePicker tpEnd = findViewById(R.id.timePickerEnd);
        DatePicker dpEnd = findViewById(R.id.datePickerEnd);
        final EditText nameText = findViewById(R.id.nameText);
        final EditText descriptionText = findViewById(R.id.descriptionText);
        final EditText locationText = findViewById(R.id.locationText);
        tpStart.setIs24HourView(true);
        tpEnd.setIs24HourView(true);

        final Bundle bundle = getIntent().getExtras();
        Calendar date = (Calendar) bundle.getSerializable("day");
        Calendar startDate = (Calendar) bundle.getSerializable("start_date");
        Calendar endDate = (Calendar) bundle.getSerializable("end_date");
        String name = bundle.getString("name");
        String descr = bundle.getString("descr");
        String loc = bundle.getString("loc");
        String rrule = bundle.getString("rrule");

        if (date != null) {
            dpStart.init(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), null);
            dpEnd.init(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), null);
            tpStart.setHour(0);
            tpStart.setMinute(0);
            tpEnd.setHour(0);
            tpEnd.setMinute(0);
        }

        EventService service = new EventService();
        service.initEventsFields(dpStart, dpEnd, tpStart, tpEnd, startDate, endDate, name, nameText, descr, descriptionText, loc, locationText,
                rrule, repeatbtn);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.API_key));
        PlacesClient placesClient = Places.createClient(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
                locationText.setText(place.getName() + "," + place.getId());
                Log.i("Mi", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                Log.i("Mi", "An error occurred: " + status);
            }
        });

    }

    View.OnClickListener repeatListener  =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final EditText nameText = findViewById(R.id.nameText);
            final EditText descriptionText = findViewById(R.id.descriptionText);
            final EditText locationText = findViewById(R.id.locationText);
            final Button repeatbtn = findViewById(R.id.buttonRepeat);
            LayoutInflater layoutInflater
                    = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
            final View popupView = layoutInflater.inflate(R.layout.repeat_info, null);
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
            Button weekBtns[] = new Button[6];
            weekBtns[0] = popupView.findViewById(R.id.noRepeat);
            weekBtns[1] = popupView.findViewById(R.id.dayRepeat);
            weekBtns[2] = popupView.findViewById(R.id.weekRepeat);
            weekBtns[3] = popupView.findViewById(R.id.monthRepeat);
            weekBtns[4] = popupView.findViewById(R.id.yearRepeat);
            weekBtns[5] = popupView.findViewById(R.id.otherRepeat);

            final String[] weekText = {"Не повторяется", "Каждый день", "Каждую неделю", "Каждый месяц", "Каждый год"};

            for (int i = 0; i < 5; i++) {
                final int finalI = i;
                weekBtns[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        repeatbtn.setText(weekText[finalI]);
                    }
                });
            }
            weekBtns[5].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePicker dpStart = findViewById(R.id.datePickerStart);
                    TimePicker tpStart = findViewById(R.id.timePickerStart);
                    DatePicker dpEnd = findViewById(R.id.datePickerEnd);
                    TimePicker tpEnd = findViewById(R.id.timePickerEnd);

                    Calendar calStart = new GregorianCalendar();
                    Calendar calEnd = new GregorianCalendar();
                    calStart.set(dpStart.getYear(), dpStart.getMonth(), dpStart.getDayOfMonth(), tpStart.getHour(), tpStart.getMinute());
                    calEnd.set(dpEnd.getYear(), dpEnd.getMonth(), dpEnd.getDayOfMonth(), tpEnd.getHour(), tpEnd.getMinute());

                    Intent intent = new Intent(AddEventActivity.this, RepeatEventActivity.class);
                    intent.putExtra("start_date", calStart);
                    intent.putExtra("end_date", calEnd);
                    intent.putExtra("name", nameText.getText().toString());
                    intent.putExtra("descr", descriptionText.getText().toString());
                    intent.putExtra("loc", locationText.getText().toString());

                    Bundle bundle = getIntent().getExtras();
                    if (bundle.getString("rrule") != null) {
                        intent.putExtra("rrule", bundle.getString("rrule"));
                        intent.putExtra("filled", true);
                    }

                    popupWindow.dismiss();
                    startActivity(intent);
                }
            });

            popupWindow.showAtLocation(popupView,  Gravity.CENTER, 0, 0);
        }
    };

    private void badRequestHandle() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(AddEventActivity.this, "Не удалось добавить событие", Toast.LENGTH_SHORT).show();
    }

    private void requestForAdd(final String mRrule) {
        TimePicker tpStart = findViewById(R.id.timePickerStart);
        DatePicker dpStart = findViewById(R.id.datePickerStart);
        TimePicker tpEnd = findViewById(R.id.timePickerEnd);
        DatePicker dpEnd = findViewById(R.id.datePickerEnd);
        final EditText et = findViewById(R.id.nameText);
        final EditText descriptionText = findViewById(R.id.descriptionText);
        final EditText locationText = findViewById(R.id.locationText);

        final Calendar calStart = new GregorianCalendar();
        final Calendar calEnd = new GregorianCalendar();
        calStart.set(dpStart.getYear(), dpStart.getMonth(), dpStart.getDayOfMonth(), tpStart.getHour(), tpStart.getMinute());
        calEnd.set(dpEnd.getYear(), dpEnd.getMonth(), dpEnd.getDayOfMonth(), tpEnd.getHour(), tpEnd.getMinute());

        final DatumEvents datEv = new DatumEvents(
            descriptionText.getText().toString(), locationText.getText().toString(), et.getText().toString(), "");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getIdToken(false).getResult().getToken() == null) {
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (!task.isSuccessful()) {
                        badRequestHandle();
                        return;
                    }
                    tokenID = task.getResult().getToken();
                    addEventnPattern(datEv, mRrule, calStart, calEnd);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    badRequestHandle();
                }
            });
        }
        else {
            tokenID = mAuth.getCurrentUser().getIdToken(false).getResult().getToken();
            addEventnPattern(datEv, mRrule, calStart, calEnd);
        }
    }

    private void addEventnPattern(DatumEvents datEv, final String mRrule, final Calendar calStart, final Calendar calEnd) {
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getEventRepository().save(datEv, tokenID).enqueue(new Callback<Events>() {
            @Override
            public void onResponse(Call<Events> call, Response<Events> response) {
                if (!response.isSuccessful()) {
                    badRequestHandle();
                    return;
                }
                List<DatumEvents> event = Arrays.asList(response.body().getData());

                DatumPatterns datP;
                if (mRrule!=null) {
                    RRule r = null;
                    try {
                        r = new RRule("RRULE:"+mRrule);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long endOfEvent;
                    if (r.getUntil() == null && r.getCount() == 0)
                        endOfEvent = Long.MAX_VALUE;
                    else {
                        EventService service = new EventService();
                        endOfEvent = service.getEnd(calEnd, r).getTimeInMillis();
                    }
                    datP = new DatumPatterns(calEnd.getTimeInMillis() - calStart.getTimeInMillis(), endOfEvent, "",
                            mRrule,calStart.getTimeInMillis(), TimeZone.getDefault().getID());
                }
                else
                    datP = new DatumPatterns(calEnd.getTimeInMillis()-calStart.getTimeInMillis(), calEnd.getTimeInMillis(),
                            "", null,calStart.getTimeInMillis(),TimeZone.getDefault().getID());

                retrofitClient.getEventPatternRepository().save(event.get(0).getId(),datP,tokenID).enqueue(new Callback<Patterns>() {
                    @Override
                    public void onResponse(Call<Patterns> call, Response<Patterns> response) {
                        if (!response.isSuccessful()) {
                            badRequestHandle();
                            return;
                        }
                        Intent intent = new Intent(AddEventActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putString("Date", " ");
                        intent.putExtras(bundle);
                        progressBar.setVisibility(View.GONE);
                        startActivity(intent);
                        overridePendingTransition (R.anim.enter, R.anim.exit);
                    }

                    @Override
                    public void onFailure(Call<Patterns> call, Throwable t) {
                        badRequestHandle();
                    }
                });
            }

            @Override
            public void onFailure(Call<Events> call, Throwable t) {
                badRequestHandle();
            }
        });
    }
}
