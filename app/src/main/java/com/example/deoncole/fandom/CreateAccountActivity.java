package com.example.deoncole.fandom;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.deoncole.fandom.ui.fragment.CreateArtistAccountFragment;
import com.example.deoncole.fandom.ui.fragment.CreateFanAccountFragment;

public class CreateAccountActivity extends AppCompatActivity {

    public static final String CHOOSE_FAN = "choose_fan";
    public static final String CHOOSE_MUSICIAN = "choose_musician";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Toolbar caToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(caToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.create_account);
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(getIntent().hasExtra(CHOOSE_FAN)){
            transaction.replace(R.id.create_account_layout,
                    CreateFanAccountFragment.newInstance());
            transaction.commit();
        }

        if(getIntent().hasExtra(CHOOSE_MUSICIAN)) {
            transaction.replace(R.id.create_account_layout,
                    CreateArtistAccountFragment.newInstance());
            transaction.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_create_account, menu);
        return true;
    }

}