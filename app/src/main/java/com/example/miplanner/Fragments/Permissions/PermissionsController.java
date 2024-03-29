package com.example.miplanner.Fragments.Permissions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.miplanner.Activities.InfoTask.InfoEventActivity;
import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumPermissions;
import com.example.miplanner.POJO.DatumTasks;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.Permissions;
import com.example.miplanner.POJO.Tasks;
import com.example.miplanner.R;
import com.example.miplanner.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PermissionsController extends Fragment {

    FirebaseAuth mAuth;
    String tokenId = null;
    ProgressBar progressBar;
    int[] count = {0};
    final ArrayList<UsersMap> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.permissions_fragment, container, false);

        Button activateLink = view.findViewById(R.id.activate);
        final ListView sharedPeople = view.findViewById(R.id.shared_people);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        ImageButton deleteAll = view.findViewById(R.id.delete_all_btn);
        deleteAll.setOnClickListener(deleteAllListener);
        deleteAll.setVisibility(View.GONE);

        //ImageButton refreshBtn = view.findViewById(R.id.refresh_btn);
        //refreshBtn.setOnClickListener(refreshListener);

        if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getIdToken(false) == null) {
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (!task.isSuccessful()) {
                        badRequestHandle("Не удалось загрузить рарешения");
                        return;
                    }
                    tokenId = task.getResult().getToken();
                    loadPermissions(view);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    badRequestHandle("Не удалось загрузить рарешения");
                }
            });
        }
        else {
            tokenId = mAuth.getCurrentUser().getIdToken(false).getResult().getToken();
            loadPermissions(view);
        }

        activateLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater li = LayoutInflater.from(getContext());
                final View promts = li.inflate(R.layout.paste_link, null);
                builder.setView(promts);
                final EditText link = promts.findViewById(R.id.link_text);
                Button sendLink = promts.findViewById(R.id.send_link_button);
                final AlertDialog alert = builder.create();
                alert.show();
                sendLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            @Override
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                tokenId = task.getResult().getToken();
                                RetrofitClient retrofitClient = RetrofitClient.getInstance();
                                retrofitClient.getSharingRepository().activateShareLink(link.getText().toString(), tokenId).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (!response.isSuccessful()) {
                                            badRequestHandle("Не удалось активировать токен");
                                        }
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Токен был успешно активирован", Toast.LENGTH_LONG).show();
                                        alert.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        badRequestHandle("Не удалось активировать токен");
                                    }
                                });
                            }
                        });
                    }
                });

            }
        });

        sharedPeople.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                LayoutInflater layoutInflater
                        = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.permissions_actions, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);
                final Button deleteButton = popupView.findViewById(R.id.delete_btn);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        final HashMap<String, String> hm = (HashMap<String, String>) parent.getItemAtPosition(position);
                        if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getIdToken(false) == null) {
                            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Не удалось удалить разрешение", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    tokenId = task.getResult().getToken();
                                    deletePermission(Long.parseLong(hm.get("id")), 1);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    badRequestHandle("Не удалось удалить рарешение");
                                }
                            });
                        }
                        else {
                            tokenId = mAuth.getCurrentUser().getIdToken(false).getResult().getToken();
                            deletePermission(Long.parseLong(hm.get("id")), 1);
                        }
                    }
                });
                popupWindow.showAtLocation(popupView,  Gravity.CENTER, 0, 0);
                return true;
            }
        });
        return view;
    }

    private void badRequestHandle(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    View.OnClickListener refreshListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            refreshItems();
        }
    };

    private void loadPermissions(View v) {
        final int[] count = {0};
        final ListView sharedPeople = v.findViewById(R.id.shared_people);
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getSharingRepository().getAllPermissions(tokenId).enqueue(new Callback<Permissions>() {
            @Override
            public void onResponse(Call<Permissions> call, Response<Permissions> response) {
                if (response.body() == null) {
                    badRequestHandle("Не удалось загрузить рарешения");
                    return;
                }
                final List<DatumPermissions> permissions = Arrays.asList(response.body().getData());
                if (permissions.size() == 0) {
                    badRequestHandle("Не удалось загрузить рарешения");
                    return;
                }
                for (int i = 0; i < permissions.size(); i++) {
                    final String[] type = {""};
                    String action_type = permissions.get(i).getName().split("_")[0];
                    switch (action_type){
                        case "READ":
                            type[0] += "чтение ";
                            break;
                        case "UPDATE":
                            type[0] += "изменение ";
                            break;
                        case "DELETE":
                            type[0] += "удаление ";
                            break;
                    }
                    String entity_type = permissions.get(i).getName().split("_")[1];
                    switch(entity_type){
                        case "EVENT":
                            final int finalI = i;
                            retrofitClient.getEventRepository().getEventsById(new Long[]{permissions.get(i).getEntity_id()}, tokenId).enqueue(new Callback<Events>() {
                                @Override
                                public void onResponse(Call<Events> call, Response<Events> response) {
                                    List<DatumEvents> listEvents = Arrays.asList(response.body().getData());
                                    if (listEvents.size() == 0)
                                        type[0] += "события \"" + "баги" + "\"";
                                    else
                                        type[0] += "события \"" + listEvents.get(0).getName() + "\"";
                                    list.add(new UsersMap(permissions.get(finalI).getUser_id(), permissions.get(finalI).getName(), permissions.get(finalI).getEntity_id(),
                                            permissions.get(finalI).getId(), type[0]));
                                    count[0]++;
                                    if (count[0] == permissions.size()) {
                                        ListAdapter adapter = new SimpleAdapter(getContext(), list, R.layout.view_users_drawable,
                                                new String[]{UsersMap.NAME, UsersMap.TYPE}, new int[]{R.id.nameText, R.id.rightText});
                                        progressBar.setVisibility(View.GONE);
                                        sharedPeople.setAdapter(adapter);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Events> call, Throwable t) {
                                    badRequestHandle("Не удалось загрузить рарешения");
                                }
                            });
                            break;
                        case "PATTERN":
                            count[0]++;
                            if (count[0] == permissions.size()) {
                                ListAdapter adapter = new SimpleAdapter(getContext(), list, R.layout.view_users_drawable,
                                        new String[]{UsersMap.NAME, UsersMap.TYPE}, new int[]{R.id.nameText, R.id.rightText});
                                progressBar.setVisibility(View.GONE);
                                sharedPeople.setAdapter(adapter);
                            }
                            break;
                        case "TASK":
                            final int finalI1 = i;
                            retrofitClient.getTasksRepository().getTaskById(permissions.get(i).getEntity_id(), tokenId).enqueue(new Callback<Tasks>() {
                                @Override
                                public void onResponse(Call<Tasks> call, Response<Tasks> response) {
                                    final List<DatumTasks> listTasks = Arrays.asList(response.body().getData());
                                    retrofitClient.getEventRepository().getEventsById(new Long[]{listTasks.get(0).getEvent_id()}, tokenId).
                                            enqueue(new Callback<Events>() {
                                        @Override
                                        public void onResponse(Call<Events> call, Response<Events> response) {
                                            List<DatumEvents> listEvents = Arrays.asList(response.body().getData());
                                            type[0] += "задачи \""+listTasks.get(0).getName()+"\" события \""+listEvents.get(0).getName()+"\"";
                                            list.add(new UsersMap(permissions.get(finalI1).getUser_id(), permissions.get(finalI1).getName(),
                                                    permissions.get(finalI1).getEntity_id(), permissions.get(finalI1).getId(), type[0]));
                                            count[0]++;
                                            if (count[0] == permissions.size()) {
                                                ListAdapter adapter = new SimpleAdapter(getContext(), list, R.layout.view_users_drawable,
                                                        new String[]{UsersMap.NAME, UsersMap.TYPE}, new int[]{R.id.nameText, R.id.rightText});
                                                progressBar.setVisibility(View.GONE);
                                                sharedPeople.setAdapter(adapter);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Events> call, Throwable t) {
                                            badRequestHandle("Не удалось загрузить рарешения");
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Call<Tasks> call, Throwable t) {
                                    badRequestHandle("Не удалось загрузить рарешения");
                                }
                            });
                            break;
                    }
                }

            }

            @Override
            public void onFailure(Call<Permissions> call, Throwable t) {
                badRequestHandle("Не удалось загрузить рарешения");
            }
        });
    }

    private void refreshItems(){
        final android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.attach(this);
        fragmentTransaction.commit();

    }

    View.OnClickListener deleteAllListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            progressBar.setVisibility(View.VISIBLE);
            if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getIdToken(false) == null) {
                mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (!task.isSuccessful()) {
                            badRequestHandle("Не удалось удалить рарешения");
                            return;
                        }
                        tokenId = task.getResult().getToken();
                        deleteAllPermissions();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        badRequestHandle("Не удалось удалить рарешения");
                    }
                });
            }
            else {
                tokenId = mAuth.getCurrentUser().getIdToken(false).getResult().getToken();
                deleteAllPermissions();
            }
        }
    };

    private void deleteAllPermissions() {
        progressBar.setVisibility(View.VISIBLE);
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getSharingRepository().getAllPermissions(tokenId).enqueue(new Callback<Permissions>() {
            @Override
            public void onResponse(Call<Permissions> call, Response<Permissions> response) {
                if (!response.isSuccessful()) {
                    badRequestHandle("Не удалось удалить рарешение");
                    return;
                }
                final List<DatumPermissions> list = Arrays.asList(response.body().getData());
                if (list.size() != 0) {
                    count[0] = 0;
                    for (int i = 0; i < list.size(); i++) {
                        deletePermission(list.get(i).getId(), list.size());
                    }
                }
                else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Permissions> call, Throwable t) {
                badRequestHandle("Не удалось удалить рарешения");
            }
        });
    }

    private void deletePermission(long id, final int size) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        retrofitClient.getSharingRepository().delete(id, tokenId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful())
                    badRequestHandle("Не удалось удалить рарешение");
                count[0]++;
                if (count[0] == size) {
                    refreshItems();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                badRequestHandle("Не удалось удалить рарешение");
            }
        });
    }


}
