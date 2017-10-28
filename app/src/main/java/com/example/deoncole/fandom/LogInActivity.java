package com.example.deoncole.fandom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.deoncole.fandom.actions.FireBaseProvider;
import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.model.Fan;
import com.example.deoncole.fandom.model.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

public class LogInActivity extends AppCompatActivity {

    EditText logInEmailEt, logInPassEt;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private FireBaseProvider mFireBaseProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInEmailEt = (EditText) findViewById(R.id.logInEmailEt);
        logInPassEt = (EditText) findViewById(R.id.logInPassEt);
        Button signInBt = (Button) findViewById(R.id.signInBt);

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.login_progress));

        Toolbar liToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(liToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.login);
        }

        signInBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                signIn(logInEmailEt.getText().toString().trim(), logInPassEt.getText().toString()
                        .trim());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return true;
    }

    private void signIn(String email, String password) {

        if (!checkUserInputs()) {
            return;
        }

        mFireBaseProvider = ((FandomApp) getApplicationContext()).getFireBaseProvider();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    showSignInError();
                } else {
                    mFireBaseProvider.fetchCurrentUser(new FireBaseProvider.OnFetchCurrentUserListener() {
                        @Override
                        public void onSuccess() {
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onError(final DatabaseError error) {
                            showSignInError();
                        }

                        @Override
                        public void onNewFan(final Fan fan, final FirebaseUser firebaseUser) {
                            if (fan != null && firebaseUser != null
                                    && firebaseUser.getUid().equals(fan.getUserUid())) {
                                mFireBaseProvider.setConnectedFan(fan, UserType.FAN);
                                Intent fanIntent = new Intent(getApplication(), ArtistFeedActivity.class);
                                startActivity(fanIntent);
                            }
                        }

                        @Override
                        public void onNewArtist(final Artist artist, final FirebaseUser firebaseUser) {
                            if (artist != null && firebaseUser != null
                                    && firebaseUser.getUid().equals(artist.getUserUid())) {
                                mFireBaseProvider.setConnectedArtist(artist, UserType.ARTIST);
                                startActivity(MusicianDashboardActivity
                                        .createIntent(LogInActivity.this,
                                                firebaseUser.getUid()));
                            }
                        }
                    });

                }
            }
        });
    }

    private void showSignInError() {
        mProgressDialog.dismiss();
        System.out.println("signInWithEmail:failed");
        Toast.makeText(LogInActivity.this, "An error occured: Maybe an email address or password " +
                        "not" +
                        " valid. " +
                        "Please enter a valid email & password or create an account",
                Toast.LENGTH_LONG).show();
    }

    private boolean checkUserInputs() {

        boolean isEmpty = true;
        String email = logInEmailEt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            logInEmailEt.setError("Email address is required");
            isEmpty = false;
        }

        if (!isValidEmail(email)) {
            logInEmailEt.setError("Please enter a valid Email address");
            isEmpty = false;
        }

        if (logInPassEt.getText().length() < 6) {
            logInPassEt.setError("Please enter a password more than 6 characters");
            isEmpty = false;
        }

        return isEmpty;
    }

    public final boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}
