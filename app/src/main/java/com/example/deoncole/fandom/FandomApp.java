package com.example.deoncole.fandom;

import android.app.Application;

import com.example.deoncole.fandom.actions.FireBaseProvider;
import com.google.firebase.auth.FirebaseAuth;

public class FandomApp extends Application {

    private FireBaseProvider fireBaseProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        fireBaseProvider = new FireBaseProvider(auth);
    }

    public FireBaseProvider getFireBaseProvider() {
        return fireBaseProvider;
    }
}
