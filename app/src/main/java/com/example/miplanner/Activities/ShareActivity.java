package com.example.miplanner.Activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumPatterns;
import com.example.miplanner.POJO.DatumPermissions;
import com.example.miplanner.POJO.DatumTasks;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.Patterns;
import com.example.miplanner.POJO.RequestPermission;
import com.example.miplanner.POJO.Tasks;
import com.example.miplanner.R;
import com.example.miplanner.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String tokenId = null;
    ProgressBar progressBar;
    final int[] count = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        Button share_btn = findViewById(R.id.button_share);
        share_btn.setOnClickListener(shareListener);
    }

    private void badRequestHandle() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(ShareActivity.this, "Не удалось получить токен", Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener shareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            final RadioGroup entityType = findViewById(R.id.sharing_type);
            final RadioButton eventsType = findViewById(R.id.events_type);
            final RadioButton eventsTasksType = findViewById(R.id.events_tasks_type);

            final int entityTypeId = entityType.getCheckedRadioButtonId();

            if (entityTypeId != eventsType.getId() && entityTypeId != eventsTasksType.getId()){
                Toast.makeText(ShareActivity.this, "Выберите чем хотите поделиться", Toast.LENGTH_SHORT).show();
                return;
            }

            final CheckBox readType = findViewById(R.id.read_type);
            final CheckBox updateType = findViewById(R.id.update_type);
            final CheckBox deleteType = findViewById(R.id.delete_type);

            if ((!readType.isChecked())&&(!updateType.isChecked())&&(!deleteType.isChecked())) {
                Toast.makeText(ShareActivity.this, "Выберите как хотите поделиться", Toast.LENGTH_SHORT).show();
                return;
            }

            final Bundle bundle = getIntent().getExtras();
            if (bundle.getSerializable("start") != null) {

                if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getIdToken(false) == null) {
                    mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(ShareActivity.this, "Не удалось получить ссылку", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            tokenId = task.getResult().getToken();
                            shareAllEvents();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            badRequestHandle();
                        }
                    });
                }
                else {
                    tokenId = mAuth.getCurrentUser().getIdToken(false).getResult().getToken();
                    shareAllEvents();
                }
            }
            else {
                final Long id = bundle.getLong("id");

                final List<RequestPermission> permissions = new ArrayList<>();
                final RetrofitClient retrofitClient = RetrofitClient.getInstance();
                mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        tokenId = task.getResult().getToken();
                        count[0] = 0;
                        createPermissionsForEvent(id, permissions, 1);
                    }
                });
            }
        }
    };

    private void shareAllEvents() {
        Bundle bundle = getIntent().getExtras();
        final Calendar calStart = (Calendar) bundle.getSerializable("start");
        final Calendar calEnd = (Calendar) bundle.getSerializable("end");

        final List<RequestPermission> permissions = new ArrayList<>();
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        count[0] = 0;
        retrofitClient.getEventRepository().getEventsByInterval(calStart.getTimeInMillis(), calEnd.getTimeInMillis(), tokenId).enqueue(new Callback<Events>() {
            @Override
            public void onResponse(Call<Events> call, Response<Events> response) {
                final List<DatumEvents> events = Arrays.asList(response.body().getData());
                for (int i = 0; i < events.size(); i++) {
                    createPermissionsForEvent(events.get(i).getId(), permissions, events.size());
                }
            }

            @Override
            public void onFailure(Call<Events> call, Throwable t) {

            }
        });
    }

    private void  createPermissionsForEvent(final long evenetId, final List<RequestPermission> permissions, final int size) {
        final RadioGroup entityType = findViewById(R.id.sharing_type);
        final RadioButton eventsType = findViewById(R.id.events_type);
        final RadioButton eventsTasksType = findViewById(R.id.events_tasks_type);

        final int entityTypeId = entityType.getCheckedRadioButtonId();

        final CheckBox readType = findViewById(R.id.read_type);
        final CheckBox updateType = findViewById(R.id.update_type);
        final CheckBox deleteType = findViewById(R.id.delete_type);

        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getEventPatternRepository().getPatternsById(evenetId, tokenId).enqueue(new Callback<Patterns>() {
            @Override
            public void onResponse(Call<Patterns> call, Response<Patterns> response) {
                final List<DatumPatterns> patts = Arrays.asList(response.body().getData());
                retrofitClient.getTasksRepository().getTasksByEventId(evenetId, tokenId).enqueue(new Callback<Tasks>() {
                    @Override
                    public void onResponse(Call<Tasks> call, Response<Tasks> response) {
                        if (response.isSuccessful()) {
                            final List<DatumTasks> tasks = Arrays.asList(response.body().getData());
                            if (readType.isChecked()) {
                                permissions.add(new RequestPermission("READ", evenetId, "EVENT"));
                                permissions.add(new RequestPermission("READ", patts.get(0).getId(), "PATTERN"));
                                if (eventsTasksType.getId() == entityTypeId)
                                    for (int j = 0; j < tasks.size(); j++)
                                        permissions.add(new RequestPermission("READ", tasks.get(j).getId(), "TASK"));
                            }
                            if (updateType.isChecked()) {
                                permissions.add(new RequestPermission("UPDATE", evenetId, "EVENT"));
                                permissions.add(new RequestPermission("UPDATE", patts.get(0).getId(), "PATTERN"));
                                if (eventsTasksType.getId() == entityTypeId)
                                    for (int j = 0; j < tasks.size(); j++)
                                        permissions.add(new RequestPermission("UPDATE", tasks.get(j).getId(), "TASK"));
                            }
                            if (deleteType.isChecked()) {
                                permissions.add(new RequestPermission("DELETE", evenetId, "EVENT"));
                                permissions.add(new RequestPermission("DELETE", patts.get(0).getId(), "PATTERN"));
                                if (eventsTasksType.getId() == entityTypeId)
                                    for (int j = 0; j < tasks.size(); j++)
                                        permissions.add(new RequestPermission("DELETE", tasks.get(j).getId(), "TASK"));
                            }
                            count[0]++;
                            if (count[0] == size)
                                showToken(permissions);
                        }
                        else {
                            badRequestHandle();
                        }
                    }

                    @Override
                    public void onFailure(Call<Tasks> call, Throwable t) {
                        badRequestHandle();
                    }
                });
            }
            @Override
            public void onFailure(Call<Patterns> call, Throwable t) {
                badRequestHandle();
            }
        });
    }

    private void showToken(List<RequestPermission> permissions) {
        RequestPermission[] permissionsArr = new RequestPermission[permissions.size()];
        permissions.toArray(permissionsArr);
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getSharingRepository().getShareLink(permissionsArr, tokenId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    badRequestHandle();
                }
                String shareLink = null;
                try {
                    shareLink = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] temp = shareLink.split("/");
                shareLink = temp[temp.length-1];
                AlertDialog.Builder builder = new AlertDialog.Builder(ShareActivity.this);
                final String finalShareLink = shareLink;
                builder.setTitle("Поделитесь токеном с другом")
                        .setMessage(shareLink)
                        .setCancelable(false)
                        .setNegativeButton("Cкопировать",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("", finalShareLink);
                                        clipboard.setPrimaryClip(clip);
                                        dialog.cancel();
                                        Intent intent = new Intent(ShareActivity.this, MainActivity.class);
                                        Bundle bundleOut = new Bundle();
                                        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                                        Calendar cal = new GregorianCalendar();
                                        dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                                        bundleOut.putString("Date", dateFormat.format(cal.getTime()));
                                        intent.putExtras(bundleOut);
                                        startActivity(intent);
                                        overridePendingTransition (R.anim.enter, R.anim.exit);
                                    }
                                });
                AlertDialog alert = builder.create();
                progressBar.setVisibility(View.GONE);
                alert.show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                badRequestHandle();
            }
        });
    }
}
