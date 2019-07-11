package com.example.miplanner.Activities.InfoTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.miplanner.Activities.Event.AddEventActivity;
import com.example.miplanner.POJO.DatumTasks;
import com.example.miplanner.POJO.Tasks;
import com.example.miplanner.R;
import com.example.miplanner.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

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
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);
        Button addBtn = findViewById(R.id.buttonAddEvent);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        initFields();

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
                cal.set( dpEnd.getYear(),  dpEnd.getMonth(), dpEnd.getDayOfMonth(), tpEnd.getHour(), tpEnd.getMinute());

                final DatumTasks taskNew = new DatumTasks("", 0, cal.getTimeInMillis(),nameTask.getText().toString(),"");

                progressBar.setVisibility(View.VISIBLE);

                addTaskRequest(taskNew);

            }
        });
    }

    private void badRequestHandle() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(AddTaskActivity.this, "Не удалось добавить задачу", Toast.LENGTH_SHORT).show();
    }

    private void addTaskRequest(final DatumTasks taskNew) {
        if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getIdToken(false) == null) {
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(AddTaskActivity.this, "Не удалось добавить задачу", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tokenID = task.getResult().getToken();
                    addTask(taskNew);
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
            addTask(taskNew);
        }
    }

    private void addTask(DatumTasks taskNew) {
        final Bundle bundle = getIntent().getExtras();
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getTasksRepository().save(bundle.getLong("event_id"), taskNew, tokenID).enqueue(new Callback<Tasks>() {
            @Override
            public void onResponse(Call<Tasks> call, Response<Tasks> response) {
                if (!response.isSuccessful()) {
                    badRequestHandle();
                    return;
                }
                Intent intent = new Intent(AddTaskActivity.this, InfoEventActivity.class);

                Bundle bundle1 = new Bundle();
                bundle1.putString("name", bundle.getString("name"));
                bundle1.putString("description", bundle.getString("description"));
                bundle1.putString("location", bundle.getString("location"));
                bundle1.putSerializable("time_start", bundle.getSerializable("time_start"));
                bundle1.putSerializable("time_end", bundle.getSerializable("time_end"));
                bundle1.putString("rrule", bundle.getString("rrule"));
                bundle1.putString("owner", bundle.getString("owner"));
                bundle1.putLong("event_id", bundle.getLong("event_id"));
                bundle1.putSerializable("time_start_current", bundle.getSerializable("time_start_current"));
                bundle1.putSerializable("time_end_current", bundle.getSerializable("time_end_current"));

                intent.putExtras(bundle1);
                progressBar.setVisibility(View.GONE);
                startActivity(intent);
                overridePendingTransition (R.anim.enter, R.anim.exit);
            }

            @Override
            public void onFailure(Call<Tasks> call, Throwable t) {
                badRequestHandle();
                return;
            }
        });
    }

    private void initFields() {
        DatePicker dpEnd = findViewById(R.id.datePickerEnd);
        TimePicker tpEnd = findViewById(R.id.timePickerEnd);
        tpEnd.setIs24HourView(true);

        Bundle bundle = getIntent().getExtras();
        Calendar cal = (Calendar) bundle.getSerializable("time_end_current");
        dpEnd.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
        tpEnd.setHour(cal.get(Calendar.HOUR_OF_DAY));
        tpEnd.setMinute(cal.get(Calendar.MINUTE));
    }
}
