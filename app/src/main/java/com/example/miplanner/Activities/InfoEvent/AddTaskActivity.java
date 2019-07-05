package com.example.miplanner.Activities.InfoEvent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.miplanner.Activities.AddEventActivity;
import com.example.miplanner.POJO.DatumTasks;
import com.example.miplanner.POJO.Tasks;
import com.example.miplanner.R;
import com.example.miplanner.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.ical.values.RRule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity {

    String tokenID = null;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);
        Button addBtn = findViewById(R.id.buttonAddEvent);
        mAuth = FirebaseAuth.getInstance();

        TimePicker tpEnd = findViewById(R.id.timePickerEnd);
        tpEnd.setIs24HourView(true);


        //mDbHelper = new CalendarDbHelper(this);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameTask = findViewById(R.id.nameText);
                DatePicker dpEnd = findViewById(R.id.datePickerEnd);
                TimePicker tpEnd = findViewById(R.id.timePickerEnd);

                nameTask.clearFocus();

                if (nameTask.getText().toString().replaceAll("[\\s\\d]", "").length() <= 0) {
                    Toast.makeText(AddTaskActivity.this, "Название события не корректно", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar cal = new GregorianCalendar();
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                try {
                    cal.setTime(format.parse(dpEnd.getDayOfMonth()+"."+dpEnd.getMonth()+"."+dpEnd.getYear()+" "+
                            tpEnd.getHour()+":"+tpEnd.getMinute()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                final Bundle bundle = getIntent().getExtras();
                final RetrofitClient retrofitClient = RetrofitClient.getInstance();
                final DatumTasks taskNew = new DatumTasks("", 0, cal.getTimeInMillis(),nameTask.getText().toString(),"");

                mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        tokenID = task.getResult().getToken();
                        retrofitClient.getTasksRepository().save(bundle.getLong("event_id"), taskNew, tokenID).enqueue(new Callback<Tasks>() {
                            @Override
                            public void onResponse(Call<Tasks> call, Response<Tasks> response) {
                                Intent intent = new Intent(AddTaskActivity.this, InfoEventActivity.class);

                                Bundle bundle1 = new Bundle();
                                bundle1.putString("name", bundle.getString("name"));
                                bundle1.putString("description", bundle.getString("description"));
                                bundle1.putString("location", bundle.getString("location"));
                                bundle1.putString("time_start", bundle.getString("time_start"));
                                bundle1.putString("time_end", bundle.getString("time_end"));
                                bundle1.putString("rrule", bundle.getString("rrule"));
                                bundle1.putLong("event_id", bundle.getLong("event_id"));

                                intent.putExtras(bundle1);
                                startActivity(intent);
                                overridePendingTransition (R.anim.enter, R.anim.exit);
                            }

                            @Override
                            public void onFailure(Call<Tasks> call, Throwable t) {

                            }
                        });
                    }
                });

            }
        });
    }


}
