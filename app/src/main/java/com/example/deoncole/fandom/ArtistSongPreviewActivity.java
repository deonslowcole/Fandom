package com.example.deoncole.fandom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

public class ArtistSongPreviewActivity extends AppCompatActivity {

    public static final String SONG_NAME = "name_of_song";
    public static final String SONG_LINK = "link_to_song";

    WebView songPreviewWV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_song_preview);

        songPreviewWV = (WebView) findViewById(R.id.songPreviewWebView);

        Intent intent = getIntent();
        String title = intent.getStringExtra(SONG_NAME);
        String url = intent.getStringExtra(SONG_LINK);


        Toolbar audioToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(audioToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        songPreviewWV.loadUrl(url);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        songPreviewWV.destroy();
    }

}
