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

import com.example.miplanner.POJO.DatumEvents;
import com.example.miplanner.POJO.DatumPermissions;
import com.example.miplanner.POJO.DatumTasks;
import com.example.miplanner.POJO.Events;
import com.example.miplanner.POJO.Permissions;
import com.example.miplanner.POJO.Tasks;
import com.example.miplanner.R;
import com.example.miplanner.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.permissions_fragment, container, false);

        Button activateLink = view.findViewById(R.id.activate);
        final ListView sharedPeople = view.findViewById(R.id.shared_people);
        final ArrayList<UsersMap> list = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        ImageButton deleteAll = view.findViewById(R.id.delete_all_btn);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        tokenId = task.getResult().getToken();
                        progressBar.setVisibility(View.VISIBLE);
                        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
                        retrofitClient.getSharingRepository().getAllPermissions(tokenId).enqueue(new Callback<Permissions>() {
                            @Override
                            public void onResponse(Call<Permissions> call, Response<Permissions> response) {
                                if (response.code() != 200) {
                                    Toast.makeText(getContext(), "Не удалось удалить право", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    return;
                                }
                                final List<DatumPermissions> list = Arrays.asList(response.body().getData());
                                if (list.size() != 0) {
                                    final int[] count = {0};
                                    for (int i = 0; i < list.size(); i++) {
                                        retrofitClient.getSharingRepository().delete(list.get(i).getId(), tokenId).enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                count[0]++;
                                                if (count[0] == list.size()-1) {
                                                    refreshItems();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {

                                            }
                                        });
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<Permissions> call, Throwable t) {
                                Toast.makeText(getContext(), "Не удалось удалить право", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                tokenId = task.getResult().getToken();
                final int[] count = {0};
                final RetrofitClient retrofitClient = RetrofitClient.getInstance();
                retrofitClient.getSharingRepository().getAllPermissions(tokenId).enqueue(new Callback<Permissions>() {
                    @Override
                    public void onResponse(Call<Permissions> call, Response<Permissions> response) {
                        if (response.body() == null) {
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        final List<DatumPermissions> permissions = Arrays.asList(response.body().getData());
                        if (permissions.size() == 0) {
                            progressBar.setVisibility(View.GONE);
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
                                            retrofitClient.getEventRepository().getEventsById(new Long[]{listTasks.get(0).getEvent_id()}, tokenId).enqueue(new Callback<Events>() {
                                                @Override
                                                public void onResponse(Call<Events> call, Response<Events> response) {
                                                    List<DatumEvents> listEvents = Arrays.asList(response.body().getData());
                                                    type[0] += "задачи \""+listTasks.get(0).getName()+"\" события \""+listEvents.get(0).getName()+"\"";
                                                    list.add(new UsersMap(permissions.get(finalI1).getUser_id(), permissions.get(finalI1).getName(), permissions.get(finalI1).getEntity_id(),
                                                            permissions.get(finalI1).getId(), type[0]));
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

                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(Call<Tasks> call, Throwable t) {

                                        }
                                    });
                                    break;
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<Permissions> call, Throwable t) {

                    }
                });
            }
        });

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
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Ссылка была успешно активирована", Toast.LENGTH_LONG).show();
                                        alert.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {

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
                        HashMap<String, String> hm = (HashMap<String, String>) parent.getItemAtPosition(position);
                        RetrofitClient retrofitClient = RetrofitClient.getInstance();
                        retrofitClient.getSharingRepository().delete(Long.parseLong(hm.get("id")), tokenId).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                refreshItems();
                                popupWindow.dismiss();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });
                    }
                });
                popupWindow.showAtLocation(popupView,  Gravity.CENTER, 0, 0);
                return true;
            }
        });

        return view;
    }

    public void refreshItems(){
        final android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.attach(this);
        fragmentTransaction.commit();

    }
}
