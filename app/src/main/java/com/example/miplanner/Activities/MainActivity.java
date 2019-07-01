package com.example.miplanner.Activities;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static int RC_SIGN_IN = 9001;

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    String tokenId = null;
    //private CalendarDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getExtras() != null) {
            tokenId = getIntent().getExtras().getString("token");
            getDay(getIntent().getExtras());
        }

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24px);
        actionBar.setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(null);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
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

    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            updateUI(account);
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    tokenId = task.getResult().getToken();
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    navigationView.setNavigationItemSelectedListener(MainActivity.this);
                }
            });
        }
        else {
            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerLayout = navigationView.getHeaderView(0);
            LinearLayout accountInfo = headerLayout.findViewById(R.id.account_info);
            SignInButton btnIn = headerLayout.findViewById(R.id.sign_in_button);
            Button btnOut = headerLayout.findViewById(R.id.sign_out_button);
            accountInfo.setVisibility(View.GONE);
            btnIn.setVisibility(View.VISIBLE);
            btnOut.setVisibility(View.GONE);
            tokenId = null;
        }
    }

    public void updateUI(GoogleSignInAccount account) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        LinearLayout accountInfo = headerLayout.findViewById(R.id.account_info);
        SignInButton btnIn = headerLayout.findViewById(R.id.sign_in_button);
        Button btnOut = headerLayout.findViewById(R.id.sign_out_button);

        if (account != null) {
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            Uri personPhoto = account.getPhotoUrl();

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
            firebaseAuthWithGoogle(account);
        }
        else {
            accountInfo.setVisibility(View.GONE);
            btnIn.setVisibility(View.VISIBLE);
            btnOut.setVisibility(View.GONE);
            tokenId = null;
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Mi", "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            tokenId = user.getIdToken(false).getResult().getToken();
                            Log.d("Mi", tokenId);

                            NavigationView navigationView = findViewById(R.id.nav_view);
                            navigationView.setNavigationItemSelectedListener(MainActivity.this);
                        } else {
                            Log.w("Mi", "signInWithCredential:failure", task.getException());
                            tokenId = null;
                        }
                    }
                });
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
        FirebaseAuth.getInstance().signOut();
        tokenId = null;
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btnIn.setVisibility(View.VISIBLE);
                        btnOut.setVisibility(View.GONE);
                        accountInfo.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.e("Mi", "получено исключение", e);
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

        if (tokenId == null) {
            Toast.makeText(MainActivity.this, "Вы не вошли", Toast.LENGTH_SHORT).show();
        }

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
        fragmentManager.replace(R.id.container, fragment);
        fragmentManager.addToBackStack(null);
        fragmentManager.commit();
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
    }
}
