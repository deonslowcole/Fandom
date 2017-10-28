package com.example.deoncole.fandom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.deoncole.fandom.actions.FireBaseProvider;
import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.model.Fan;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_PHOTO_REQUEST_CODE = 1;

    private ImageView profileImg;
    private EditText profileUserName;
    @Nullable private Uri mImageURI;
    private FireBaseProvider mFireBaseProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImg = (ImageView) findViewById(R.id.profileImg);
        profileUserName = (EditText) findViewById(R.id.profileNameEt);
        final Button updateProfileBt = (Button) findViewById(R.id.updateProfileBt);

        Toolbar proToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(proToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.profile_title);
        }

        mFireBaseProvider = ((FandomApp) getApplicationContext()).getFireBaseProvider();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        displayUser();

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, PICK_PHOTO_REQUEST_CODE);
            }
        });

        updateProfileBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (currentUser == null) {
                    throw new IllegalStateException("No connected user found.");
                }

                mFireBaseProvider.updateUser(currentUser.getUid(),
                        profileUserName.getText().toString(),
                        mImageURI != null ? mImageURI : currentUser.getPhotoUrl(),
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(final Void aVoid) {
                                Toast.makeText(ProfileActivity.this,
                                        R.string.profile_updated, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void displayUser() {
        if (mFireBaseProvider.getConnectedArtist() != null) {
            Artist artist = mFireBaseProvider.getConnectedArtist();
            profileUserName.setText(artist.getArtistName());
            Picasso.with(this).load(artist.getImageUrl()).into(profileImg);
        } else if (mFireBaseProvider.getConnectedFan() != null) {
            Fan fan = mFireBaseProvider.getConnectedFan();
            profileUserName.setText(fan.getFanName());
            Picasso.with(this).load(fan.getImageUrl()).into(profileImg);
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mImageURI = data.getData();
                try {
                    Bitmap userBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                            mImageURI);
                    Toast.makeText(this, R.string.image_saved, Toast.LENGTH_SHORT).show();
                    profileImg.setImageBitmap(userBitmap);

                } catch (IOException e) {
                    Toast.makeText(this, "Could not load image", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }


    public static void open(final Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        context.startActivity(intent);
    }

}
