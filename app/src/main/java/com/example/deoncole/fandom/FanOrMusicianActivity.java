package com.example.deoncole.fandom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class FanOrMusicianActivity extends AppCompatActivity {

    ImageView fanImg, musicianImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_or_musician);

        fanImg = (ImageView) findViewById(R.id.fanImg);
        musicianImg = (ImageView) findViewById(R.id.musicianImg);

        Toolbar fmToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(fmToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Fandom");
        }

        fanImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(getApplication(), CreateAccountActivity.class);
                signUpIntent.putExtra(CreateAccountActivity.CHOOSE_FAN, false);
                startActivity(signUpIntent);
            }
        });

        musicianImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(getApplication(), CreateAccountActivity.class);
                signUpIntent.putExtra(CreateAccountActivity.CHOOSE_MUSICIAN, true);
                startActivity(signUpIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return true;
    }


}
