package com.example.deoncole.fandom;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deoncole.fandom.actions.FireBaseProvider;
import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.model.Fan;
import com.facebook.CallbackManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.opentok.android.Session;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;

    private final int RC_VIDEO_APP_PERM = 124;

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button signUpBt = (Button) findViewById(R.id.signUpBt);
        final Button logInBt = (Button) findViewById(R.id.logInBt);
        final TextView forgotPasswordTv = (TextView) findViewById(R.id.forgotPassTv);

        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "Refreshed Token: " + refreshToken);

        requestPermissions();

        signUpBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(getApplication(), FanOrMusicianActivity.class);
                startActivity(signUpIntent);
            }
        });

        logInBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent liIntent = new Intent(getApplication(), LogInActivity.class);
                startActivity(liIntent);
            }
        });

        forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getApplication(), ForgotPasswordActivity.class);
                startActivity(profileIntent);
            }
        });
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (EasyPermissions.hasPermissions(this, perms)) {

            Toast.makeText(this, "Logging you in", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this,
                    "This app needs access to your camera and mic so you can perform video calls",
                    RC_VIDEO_APP_PERM, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, R.string.no_user_connected, Toast.LENGTH_LONG).show();
            return;
        }

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
                if (fan != null && firebaseUser.getUid().equals(fan.getUserUid())) {
                    Intent fanIntent = new Intent(getApplication(), ArtistFeedActivity.class);
                    startActivity(fanIntent);
                }
            }

            @Override
            public void onNewArtist(final Artist artist, final FirebaseUser firebaseUser) {
                if (artist != null && firebaseUser.getUid().equals(artist.getUserUid())) {
                    startActivity(MusicianDashboardActivity
                            .createIntent(MainActivity.this, firebaseUser.getUid()));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}



