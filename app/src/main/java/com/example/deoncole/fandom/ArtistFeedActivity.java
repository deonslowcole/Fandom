package com.example.deoncole.fandom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deoncole.fandom.actions.FireBaseProvider;
import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.model.Fan;
import com.example.deoncole.fandom.ui.fragment.ArtistFeedFragment;
import com.example.deoncole.fandom.ui.fragment.ConnectionsFragment;
import com.example.deoncole.fandom.ui.fragment.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class ArtistFeedActivity extends AppCompatActivity {

    public static final String IS_MUSICIAN = "is_musician";

    private ImageView userImg;
    private TextView welcomeTv;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Nullable private Fan mCurrentFan; // if the current user is of type fan this field will be non null
    @Nullable private Artist mCurrentArtist; // if the current user is of type artist this field will be non null

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_feed);

        userImg = (ImageView) findViewById(R.id.userImage);
        welcomeTv = (TextView) findViewById(R.id.welcomeTv);
        welcomeTv.setText(R.string.hello_user);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        final BottomNavigationView bottomNavigationView =
                (BottomNavigationView) findViewById(R.id.bottomNav);
        fetchUser(mAuth.getCurrentUser());

        Toolbar feedToolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(feedToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if(getIntent().hasExtra(IS_MUSICIAN)){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFrag = null;
                switch(item.getItemId()){
                    case R.id.action_home:
                        selectedFrag = ArtistFeedFragment.newInstance();
                        break;
                    case R.id.action_search:
                        selectedFrag = SearchFragment.newInstance();
                        break;
                    case R.id.action_connections:
                        selectedFrag = ConnectionsFragment.newInstance();
                        break;
                }

                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, selectedFrag);
                fragmentTransaction.commit();
                return true;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ArtistFeedFragment.newInstance());
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(getIntent().hasExtra(IS_MUSICIAN)){
            switch(item.getItemId()) {
                case android.R.id.home:
                    this.finish();
                    break;
                case R.id.action_profile:
                    if (mCurrentArtist != null) {
                        ProfileActivity.open(this);
                    }
                    else if (mCurrentFan != null) {
                        ProfileActivity.open(this);
                    }
                    else {
                        Toast.makeText(this, R.string.error_no_user_to_update, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.action_voice_notify:
                    activityIntent(AudioMessageActivity.class);
                    break;
                case R.id.action_logout:
                    mAuth.signOut();
                    activityIntent(MainActivity.class);
                    break;
            }
            return true;
        } else {
            switch (item.getItemId()){
                case R.id.action_profile:
                    activityIntent(ProfileActivity.class);
                    break;
                case R.id.action_voice_notify:
                    activityIntent(AudioMessageActivity.class);
                    break;
                case R.id.action_logout:
                    mAuth.signOut();
                    activityIntent(MainActivity.class);
                    break;
            }
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void fetchUser(final FirebaseUser user){
        FireBaseProvider fireBaseProvider = ((FandomApp) getApplicationContext()).getFireBaseProvider();
        fireBaseProvider.fetchCurrentUser(new FireBaseProvider.OnFetchCurrentUserListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(final DatabaseError error) {
            }

            @Override
            public void onNewFan(final Fan fan, final FirebaseUser firebaseUser) {
                if(fan != null && user.getUid().equals(fan.getUserUid())){
                    mCurrentFan = fan;
                    Picasso.with(getApplicationContext()).load(fan.getImageUrl())
                            .into(userImg);
                    welcomeTv.setText(fan.getFanName());
                }
            }

            @Override
            public void onNewArtist(final Artist artist, final FirebaseUser firebaseUser) {
                if(artist != null && user.getUid().equals(artist.getUserUid())) {
                    mCurrentArtist = artist;
                    Picasso.with(getApplicationContext()).load(artist.getImageUrl())
                            .into(userImg);
                    welcomeTv.setText(artist.getArtistName());
                }
            }
        });
    }

    private void activityIntent(Class intentClass){
        Intent intent = new Intent(getApplicationContext(), intentClass);
        startActivity(intent);
    }

}
