package com.example.miplanner.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.miplanner.Fragments.Calendar.CalendarController;
import com.example.miplanner.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import okhttp3.OkHttpClient;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static int RC_SIGN_IN = 9001;

    GoogleSignInClient mGoogleSignInClient;
    String tokenId = null;
    //private CalendarDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24px);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Parcelable[] temp = bundle.getParcelableArray("events");
            if (bundle.getString("Date") != null)
                getDay(bundle);
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

        headerLayout.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        headerLayout.findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        /*mDbHelper = new CalendarDbHelper(this);
        if (mDbHelper.eventsIsEmpty()) {
            Calendar startTime1 = Calendar.getInstance();
            Calendar endTime1 = Calendar.getInstance();
            endTime1.add(Calendar.DAY_OF_MONTH, 3);
            startTime1.set(Calendar.HOUR_OF_DAY, 11);
            endTime1.set(Calendar.HOUR_OF_DAY, 11);
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            mDbHelper.insertEvent("Поход на Тобизина", "Незабываемое приключение!", "м. Тобизина", "", format.format(startTime1.getTime()), format.format(endTime1.getTime()));

            Calendar startTime2 = Calendar.getInstance();
            startTime2.add(Calendar.DAY_OF_YEAR, -2);
            Calendar endTime2 = Calendar.getInstance();
            endTime2.add(Calendar.DAY_OF_YEAR, 1);
            startTime2.set(Calendar.HOUR_OF_DAY, 8);
            endTime2.set(Calendar.HOUR_OF_DAY, 18);
            mDbHelper.insertEvent("Поездка в Арсеньев", "Красивый маленький город", "Арсеньев", "", format.format(startTime2.getTime()), format.format(endTime2.getTime()));

            Calendar startTime3 = Calendar.getInstance();
            Calendar endTime3 = Calendar.getInstance();
            startTime3.set(Calendar.HOUR_OF_DAY, 14);
            startTime3.set(Calendar.MINUTE, 0);
            endTime3.set(Calendar.HOUR_OF_DAY, 15);
            endTime3.set(Calendar.MINUTE, 0);
            mDbHelper.insertEvent("Встреча с Настей", "Лучшая подруга", "Арсеньев", "* * 3 * * * *", "7", format.format(startTime3.getTime()), format.format(endTime3.getTime()));
        }*/

    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        LinearLayout accountInfo = headerLayout.findViewById(R.id.account_info);
        SignInButton btnIn = headerLayout.findViewById(R.id.sign_in_button);
        Button btnOut = headerLayout.findViewById(R.id.sign_out_button);

        if (account != null) {
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            tokenId = account.getIdToken();

            ImageView image = headerLayout.findViewById(R.id.imageView);
            TextView name = headerLayout.findViewById(R.id.nameText);
            TextView email = headerLayout.findViewById(R.id.emailText);

            name.setText(personName);
            email.setText(personEmail);
            Picasso.with(this).load(personPhoto).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });

            accountInfo.setVisibility(View.VISIBLE);
            btnIn.setVisibility(View.GONE);
            btnOut.setVisibility(View.VISIBLE);
        }
        else {
            accountInfo.setVisibility(View.GONE);
            btnIn.setVisibility(View.VISIBLE);
            btnOut.setVisibility(View.GONE);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        final LinearLayout accountInfo = headerLayout.findViewById(R.id.account_info);
        final SignInButton btnIn = headerLayout.findViewById(R.id.sign_in_button);
        final Button btnOut = headerLayout.findViewById(R.id.sign_out_button);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btnIn.setVisibility(View.VISIBLE);
                        btnOut.setVisibility(View.GONE);
                        accountInfo.setVisibility(View.GONE);
                        tokenId = null;
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        LinearLayout accountInfo = headerLayout.findViewById(R.id.account_info);
        SignInButton btnIn = headerLayout.findViewById(R.id.sign_in_button);
        Button btnOut = headerLayout.findViewById(R.id.sign_out_button);

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            tokenId = account.getIdToken();

            ImageView image = headerLayout.findViewById(R.id.imageView);
            TextView name = headerLayout.findViewById(R.id.nameText);
            TextView email = headerLayout.findViewById(R.id.emailText);

            Picasso.with(this).load(personPhoto).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                }
            });

            name.setText(personName);
            email.setText(personEmail);

            accountInfo.setVisibility(View.VISIBLE);
            btnIn.setVisibility(View.GONE);
            btnOut.setVisibility(View.VISIBLE);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("Mi", "получено исключение", e);

            accountInfo.setVisibility(View.GONE);
            btnIn.setVisibility(View.VISIBLE);
            btnOut.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.calendar) {
            fragmentClass = CalendarController.class;
        }/* else if (id == R.id.nav_gallery) {
            fragmentClass = WeekController.class;
        } else if (id == R.id.nav_slideshow) {
            fragmentClass = MonthController.class;
        }*/

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        Bundle bundle = new Bundle();
        bundle.putString("token", tokenId);
        fragment.setArguments(bundle);

        FragmentTransaction fragmentManager = getSupportFragmentManager().beginTransaction();
        //fragmentManager.beginTransaction();
        //fragmentManager.setCustomAnimations(R.anim.right_in, 0);
        fragmentManager.replace(R.id.container, fragment);
        fragmentManager.addToBackStack(null);
        fragmentManager.commit();
        //overridePendingTransition(0,R.anim.left_out);
        item.setChecked(true);
        setTitle(item.getTitle());

        return true;
    }

    public void getDay(Bundle bundle) {
        Fragment fragment = null;
        Class fragmentClass = CalendarController.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        /*Fragment fragment = new CalendarController();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment);
        ft.commit();*/
    }
}
