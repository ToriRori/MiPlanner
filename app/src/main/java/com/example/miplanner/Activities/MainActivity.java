package com.example.miplanner.Activities;

import android.os.Parcelable;
import android.preference.PreferenceFragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.miplanner.Data.CalendarDbHelper;
import com.example.miplanner.Event;
import com.example.miplanner.Fragments.Calendar.CalendarController;
import com.example.miplanner.Fragments.Calendar.DrawableCalendarEvent;
import com.example.miplanner.R;
import com.github.tibolte.agendacalendarview.models.DayItem;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    com.example.miplanner.Event[] connect = new com.example.miplanner.Event[20];
    int sizeEv = 0;

    private CalendarDbHelper mDbHelper;

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

        mDbHelper = new CalendarDbHelper(this);
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

        Bundle bundle = new Bundle();
        bundle.putParcelableArray("events", connect);
        bundle.putInt("size", sizeEv);
        fragment.setArguments(bundle);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

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
