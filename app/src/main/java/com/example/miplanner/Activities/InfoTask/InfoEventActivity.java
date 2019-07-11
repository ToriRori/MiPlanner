package com.example.miplanner.Activities.InfoTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miplanner.Activities.MainActivity;
import com.example.miplanner.Data.CalendarDbHelper;
import com.example.miplanner.POJO.DatumTasks;
import com.example.miplanner.POJO.Tasks;
import com.example.miplanner.R;
import com.example.miplanner.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.ical.values.RRule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoEventActivity extends AppCompatActivity {

    private CalendarDbHelper mDbHelper;

    String tokenID = null;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    Long event_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_info_activity);
        final Bundle bundle = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        tokenID = bundle.getString("token");
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        event_id = bundle.getLong("event_id");

        initFields();

        final ListView tasksList = findViewById(R.id.tasks_list);

        tasksList.setOnItemLongClickListener(tasksAdapter);

        loadTasks();

        Button btn_add = findViewById(R.id.button_add);
        btn_add.setOnClickListener(addTaskListener);
    }

    private void badRequestHandle(String message) {
        Toast.makeText(InfoEventActivity.this, message, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    AdapterView.OnItemLongClickListener tasksAdapter = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
            LayoutInflater layoutInflater
                    = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.event_actions, null);
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
            final Button btnEdit = popupView.findViewById(R.id.edit_btn);
            btnEdit.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InfoEventActivity.this, EditTaskActivity.class);
                    Bundle bundle = getIntent().getExtras();
                    Bundle bundle1 = new Bundle();
                    HashMap<String, String> hm = (HashMap<String, String>) parent.getItemAtPosition(position);

                    bundle1.putString("task_name", hm.get("name"));
                    bundle1.putString("task_time", hm.get("time"));
                    bundle1.putString("task_id", hm.get("id"));
                    bundle1.putString("name", bundle.getString("name"));
                    bundle1.putString("description", bundle.getString("description"));
                    bundle1.putString("location", bundle.getString("location"));
                    bundle1.putSerializable("time_start", bundle.getSerializable("time_start"));
                    bundle1.putSerializable("time_end", bundle.getSerializable("time_end"));
                    bundle1.putString("rrule", bundle.getString("rrule"));
                    bundle1.putString("owner", bundle.getString("owner"));
                    bundle1.putSerializable("time_end_current", bundle.getSerializable("time_end_current"));
                    bundle1.putSerializable("time_start_current", bundle.getSerializable("time_start_current"));
                    bundle1.putLong("event_id", event_id);

                    intent.putExtras(bundle1);
                    startActivity(intent);
                    overridePendingTransition (R.anim.enter, R.anim.exit);
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
                                    badRequestHandle("Не удалось удалить задачу");
                                    return;
                                }
                                tokenID = task.getResult().getToken();
                                deleteTask(parent, position);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                badRequestHandle("Не удалось удалить задачу");
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                    else {
                        tokenID = mAuth.getCurrentUser().getIdToken(false).getResult().getToken();
                        deleteTask(parent, position);
                    }
                }});
            popupWindow.showAtLocation(popupView,  Gravity.CENTER, 0, 0);
            return true;
        }
    };

    private void deleteTask(final AdapterView<?> parent, final int position) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        HashMap<String, String> hm = (HashMap<String, String>) parent.getItemAtPosition(position);
        retrofitClient.getTasksRepository().delete(Long.parseLong(hm.get("id")),tokenID).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    badRequestHandle("Не удалось удалить задачу");
                    return;
                }
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                overridePendingTransition (R.anim.enter, R.anim.exit);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                badRequestHandle("Не удалось удалить задачу");
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    View.OnClickListener addTaskListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(InfoEventActivity.this, AddTaskActivity.class);
            Bundle bundle = getIntent().getExtras();
            Bundle bundle1 = new Bundle();
            bundle1.putString("name", bundle.getString("name"));
            bundle1.putString("description", bundle.getString("description"));
            bundle1.putString("location", bundle.getString("location"));
            bundle1.putSerializable("time_start", bundle.getSerializable("time_start"));
            bundle1.putSerializable("time_end", bundle.getSerializable("time_end"));
            bundle1.putString("rrule", bundle.getString("rrule"));
            bundle1.putSerializable("time_end_current", bundle.getSerializable("time_end_current"));
            bundle1.putSerializable("time_start_current", bundle.getSerializable("time_start_current"));
            bundle1.putString("owner", bundle.getString("owner"));
            bundle1.putLong("event_id", event_id);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition (R.anim.enter, R.anim.exit);

        }
    };

    private void loadTasks() {
        if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getIdToken(false) == null) {
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (!task.isSuccessful()) {
                        badRequestHandle("Не удаловсь загрузить задачи");
                        return;
                    }
                    tokenID = task.getResult().getToken();
                    requestForTasks();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    badRequestHandle("Не удаловсь загрузить задачи");
                }
            });
        }
            else {
            tokenID = mAuth.getCurrentUser().getIdToken(false).getResult().getToken();
            requestForTasks();
        }
    }

    private void requestForTasks() {
        final ListView tasksList = findViewById(R.id.tasks_list);
        final ArrayList<TasksMap> list = new ArrayList<>();
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getTasksRepository().getTasksByEventId(event_id, tokenID).enqueue(new Callback<Tasks>() {
            @Override
            public void onResponse(Call<Tasks> call, Response<Tasks> response) {
                if (!response.isSuccessful()) {
                    badRequestHandle("Не удаловсь загрузить задачи");
                    return;
                }
                List<DatumTasks> tasks = Arrays.asList(response.body().getData());
                for (int i = 0; i < tasks.size(); i++) {
                    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    Calendar cal = new GregorianCalendar();
                    cal.setTimeInMillis(tasks.get(i).getDeadline_at());
                    list.add(new TasksMap(tasks.get(i).getName(), format.format(cal.getTime()),Long.toString(tasks.get(i).getId())));
                }
                TextView title = findViewById(R.id.tasks_text);
                if (list.size() > 0) {
                    title.setText("Задачи");
                    SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), list, R.layout.view_agenda_drawable_task,
                            new String[]{TasksMap.NAME, TasksMap.TIME}, new int[]{R.id.view_agenda_event_title, R.id.view_agenda_event_time});
                    tasksList.setAdapter(adapter);
                }
                else {
                    title.setText("Нет задач");
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<Tasks> call, Throwable t) {
                badRequestHandle("Не удаловсь загрузить задачи");
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initFields() {
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String descr = bundle.getString("description");
        String location = bundle.getString("location");
        Calendar startTime = (Calendar) bundle.getSerializable("time_start");
        Calendar startTimeCur = (Calendar) bundle.getSerializable("time_start_current");
        Calendar endTimeCur = (Calendar) bundle.getSerializable("time_end_current");
        String rrule = bundle.getString("rrule");
        String owner = bundle.getString("owner");

        TextView nameText = findViewById(R.id.nameText);
        TextView descrText = findViewById(R.id.descriptionText);
        TextView locText = findViewById(R.id.locationText);
        TextView startTimeText = findViewById(R.id.timeStartText);
        TextView durationText = findViewById(R.id.durationText);
        TextView repeatText = findViewById(R.id.repeatText);
        TextView ownerText = findViewById(R.id.authorText);
        LinearLayout ownerLayout = findViewById(R.id.authorLayout);

        if (mAuth.getCurrentUser().getUid().equals(owner))
            ownerLayout.setVisibility(View.GONE);
        else
            ownerText.setText(owner);

        nameText.setText(name);
        if (descr == null || descr.equals(""))
            descrText.setText(" не указано");
        else
            descrText.setText(descr);
        if (location == null || location.equals(""))
            locText.setText(" не указано");
        else
            locText.setText(location);

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        startTimeText.setText(format.format(startTime.getTime()));
        durationText.setText(getDuration(startTimeCur, endTimeCur));
        repeatText.setText(getRepeat(rrule));
    }

    private String getDuration(Calendar calStart, Calendar calEnd) {
        LocalDateTime dateStart = LocalDateTime.of(calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH) + 1,
                calStart.get(Calendar.DAY_OF_MONTH), calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE));
        LocalDateTime dateEnd = LocalDateTime.of(calEnd.get(Calendar.YEAR), calEnd.get(Calendar.MONTH) + 1,
                calEnd.get(Calendar.DAY_OF_MONTH), calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE));
        Period p = Period.between(dateStart.toLocalDate(), dateEnd.toLocalDate());
        String res = "";
        if (Math.abs(p.getYears()) != 0) {
            if (Math.abs(p.getYears()) == 1)
                res += Math.abs(p.getYears()) + " год ";
            else if (Math.abs(p.getYears()) < 5)
                res += Math.abs(p.getYears()) + " года ";
            else
                res += Math.abs(p.getYears()) + " лет ";
        }
        if (Math.abs(p.getMonths()) != 0) {
            if (Math.abs(p.getMonths()) == 1)
                res += Math.abs(p.getMonths()) + " месяц ";
            else if (Math.abs(p.getMonths()) < 5)
                res += Math.abs(p.getMonths()) + " месяца ";
            else
                res += Math.abs(p.getMonths()) + " месяцев ";
        }
        int add = 0;
        if (dateStart.getDayOfMonth()+1 == dateEnd.getDayOfMonth() && dateStart.getMonth() == dateEnd.getMonth() &&
                dateStart.getYear() == dateEnd.getYear())
            add = -1;
        if (Math.abs(p.getDays()+add) != 0) {
            if (Math.abs(p.getDays()+add)%10 == 1)
                res += Math.abs(p.getDays()+add) + " день ";
            else if (Math.abs(p.getDays()+add)%10 < 5 && Math.abs(p.getDays()+add)%10 > 0)
                res += Math.abs(p.getDays()+add) + " дня ";
            else
                res += Math.abs(p.getDays()+add)%10 + " дней ";
        }

        if (dateStart.getDayOfMonth()+1 == dateEnd.getDayOfMonth() && dateStart.getMonth() == dateEnd.getMonth() &&
                dateStart.getYear() == dateEnd.getYear()){
            add = 24;
            if (add+dateEnd.getHour() - dateStart.getHour() != 0) {
                if (Math.abs(add+dateEnd.getHour() - dateStart.getHour())%10 == 1)
                    res += Math.abs(add+dateEnd.getHour() - dateStart.getHour()) + " час ";
                else if (Math.abs(add+dateEnd.getHour() - dateStart.getHour())%10 < 5 && Math.abs(add+dateEnd.getHour() - dateStart.getHour())%10 > 0)
                    res += Math.abs(add+dateEnd.getHour() - dateStart.getHour()) + " часа ";
                else
                    res += Math.abs(add+dateEnd.getHour() - dateStart.getHour()) + " часов ";
            }
        }
        else {
            if (dateEnd.getHour() - dateStart.getHour() != 0) {
                if (Math.abs(dateEnd.getHour() - dateStart.getHour())%10 == 1)
                    res += Math.abs(dateEnd.getHour() - dateStart.getHour()) + " час ";
                else if (Math.abs(dateEnd.getHour() - dateStart.getHour())%10 < 5 && Math.abs(dateEnd.getHour() - dateStart.getHour())%10 > 0)
                    res += Math.abs(dateEnd.getHour() - dateStart.getHour()) + " часа ";
                else
                    res += Math.abs(dateEnd.getHour() - dateStart.getHour()) + " часов ";
            }
        }
        if (Math.abs(dateEnd.getMinute()-dateStart.getMinute()) != 0) {
            if (Math.abs(dateEnd.getMinute()-dateStart.getMinute())%10 == 1)
                res += Math.abs(dateEnd.getMinute()-dateStart.getMinute()) + " минута ";
            else if (Math.abs(dateEnd.getMinute()-dateStart.getMinute())%10 < 5 && Math.abs(dateEnd.getMinute()-dateStart.getMinute())%10 > 0)
                res += Math.abs(dateEnd.getMinute()-dateStart.getMinute()) + " минуты ";
            else
                res += Math.abs(dateEnd.getMinute()-dateStart.getMinute()) + " минут ";
        }
        return res;
    }

    private String getRepeat(String rrule) {
        RRule rule = null;
        try {
            rule = new RRule("RRULE:" + rrule);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String rep = "";
        if (rule != null) {
            switch (rule.getFreq()) {
                case YEARLY:
                    if (rule.getInterval() == 1)
                        rep += "каждый год ";
                    else if (rule.getInterval() < 5)
                        rep += "каждые " + rule.getInterval() + " года ";
                    else
                        rep += "каждые " + rule.getInterval() + " лет ";
                    break;
                case MONTHLY:
                    if (rule.getInterval() == 1)
                        rep += "каждый месяц ";
                    else if (rule.getInterval() < 5)
                        rep += "каждые " + rule.getInterval() + " месяца ";
                    else
                        rep += "каждые " + rule.getInterval() + " месяцев ";
                    break;
                case WEEKLY:
                    if (rule.getInterval() == 1)
                        rep += "каждую неделю ";
                    else if (rule.getInterval() < 5)
                        rep += "каждые " + rule.getInterval() + " недели ";
                    else
                        rep += "каждые " + rule.getInterval() + " недель ";
                    break;
                case DAILY:
                    if (rule.getInterval() == 1)
                        rep += "каждый день ";
                    else if (rule.getInterval() < 5)
                        rep += "каждые " + rule.getInterval() + " дня ";
                    else
                        rep += "каждые " + rule.getInterval() + " дней ";
                    break;
            }
            if (rule.getUntil() != null)
                rep += "до " + rule.getUntil().day() + "." + rule.getUntil().month() + "." + rule.getUntil().year();
            else if (rule.getCount() != 0) {
                if (rule.getCount() > 1 && rule.getCount() < 5)
                    rep += rule.getCount() + " раза";
                else
                    rep += rule.getCount() + " раз";
            }
        }
        if (rep.equals(""))
            rep = "Не повторяется";
        return rep;
    }
}
